/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.wise.core.client.impl.reflection;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.ws.WebServiceClient;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.jboss.logging.Logger;
import org.jboss.wise.core.client.BasicWSDynamicClient;
import org.jboss.wise.core.client.SpiLoader;
import org.jboss.wise.core.client.WSDLParser;
import org.jboss.wise.core.client.WSEndpoint;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.client.WSService;
import org.jboss.wise.core.client.builder.BasicWSDynamicClientBuilder;
import org.jboss.wise.core.consumer.WSConsumer;
import org.jboss.wise.core.exception.ResourceNotAvailableException;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.wise.core.utils.IOUtils;
import org.jboss.wise.core.utils.JavaUtils;
import org.jboss.wise.core.i18n.Messages;

/**
 * This is the Wise core, i.e. the JAX-WS client that handles wsdl retrieval &amp; parsing, invocations, etc.
 *
 * @author Stefano Maestri, stefano.maestri@javalinux.it
 *
 */
@ThreadSafe
public class BasicWSDynamicClientImpl implements BasicWSDynamicClient {

    @GuardedBy("this")
    private ClassLoader classLoader;

    protected final String userName;

    protected final String password;

    private final CopyOnWriteArrayList<String> classNames = new CopyOnWriteArrayList<String>();

    private final Map<String, WSService> servicesMap = Collections.synchronizedMap(new HashMap<String, WSService>());

    private Set<String> excludedPorts;

    private final String tmpDir;

    protected final int maxThreadPoolSize;

    /**
     * @param builder client builder
     * @return consumer
     */
    private static WSConsumer createConsumer(BasicWSDynamicClientBuilder builder) {
        WSConsumer consumer = (WSConsumer) SpiLoader.loadService("org.jboss.wise.consumer.WSConsumer",
                "org.jboss.wise.core.consumer.impl.jbossws.DefaultWSImportImpl");
        consumer.setVerbose(builder.isVerbose());
        consumer.setKeepSource(builder.isKeepSource());
        return consumer;
    }

    public BasicWSDynamicClientImpl(BasicWSDynamicClientBuilder builder) throws WiseRuntimeException {
        this(builder, createConsumer(builder));
    }

    protected BasicWSDynamicClientImpl(BasicWSDynamicClientBuilder builder, WSConsumer consumer) throws WiseRuntimeException {
        userName = builder.getUserName();
        password = builder.getPassword();
        this.maxThreadPoolSize = builder.getMaxThreadPoolSize();
        this.tmpDir = builder.getClientSpecificTmpDir();

        try {
            this.prepare(builder, consumer);
        } catch (WiseRuntimeException e) {
            this.close();
            throw e;
        }
    }

    protected void prepare(BasicWSDynamicClientBuilder builder, WSConsumer consumer) {
        final File outputDir = new File(new StringBuilder(tmpDir).append(File.separator).append("classes")
                .append(File.separator).toString());
        final File sourceDir = new File(new StringBuilder(tmpDir).append(File.separator).append("src").append(File.separator)
                .toString());

        try {
            classNames
                    .addAll(consumer.importObjectFromWsdl(builder.getNormalizedWsdlUrl(), outputDir, sourceDir,
                            builder.getTargetPackage(), builder.getBindingFiles(), builder.getMessageStream(),
                            builder.getCatalogFile()));
        } catch (MalformedURLException e) {
            throw new WiseRuntimeException("Problem consuming wsdl:" + builder.getWsdlURL(), e);
        }
        this.initClassLoader(outputDir);

        if (builder.isExcludeNonSOAPPorts()) {
            excludedPorts = WSDLParser.searchNonSoapServices(builder.getNormalizedWsdlUrl());
        }

        this.processServices();
    }

    /**
     * @param outputDir file
     * @throws WiseRuntimeException wapper of runtime exception
     */

    private synchronized void initClassLoader(File outputDir) throws WiseRuntimeException {
        try {

            final ClassLoader oldLoader = SecurityActions.getContextClassLoader();
            // we need a custom classloader pointing the temp dir
            // in order to load the generated classes on the fly
            this.setClassLoader(new URLClassLoader(new URL[] { outputDir.toURI().toURL(), }, oldLoader));

            try {
                SecurityActions.setContextClassLoader(this.getClassLoader());
                Class<?> clazz = JavaUtils.loadJavaType("javax.xml.ws.spi.Provider", this.getClassLoader());
                clazz.getMethod("provider", new Class[] {}).invoke(null, new Object[] {});
            } finally {
                // restore the original classloader
                SecurityActions.setContextClassLoader(oldLoader);
            }
        } catch (Exception e) {
            throw new WiseRuntimeException("Error occurred while setting up classloader for generated class in directory: "
                    + outputDir, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.WSDynamicClient#processServices()
     */
    public synchronized Map<String, WSService> processServices() throws IllegalStateException {
        ClassLoader oldLoader = SecurityActions.getContextClassLoader();
        try {
            SecurityActions.setContextClassLoader(this.getClassLoader());
            for (String className : classNames) {
                try {
                    Class<?> clazz = JavaUtils.loadJavaType(className, this.getClassLoader());
                    WebServiceClient annotation = clazz.getAnnotation(WebServiceClient.class);
                    if (annotation != null) {
                        WSService service = createService(clazz);
                        servicesMap.put(annotation.name(), service);
                    }
                } catch (Exception e) {
                    throw new IllegalStateException("Error during loading/instanciating class:" + className
                            + " with exception message: " + e.getMessage(), e);
                }
            }
        } finally {
            // restore the original classloader
            SecurityActions.setContextClassLoader(oldLoader);
        }
        return servicesMap;
    }

    protected WSService createService(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        return new WSServiceImpl(clazz, this.getClassLoader(), clazz.newInstance(), userName, password, excludedPorts,
                this.maxThreadPoolSize);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.WSDynamicClient#getWSMethod(String, String, String)
     */
    public synchronized WSMethod getWSMethod(String serviceName, String portName, String operationName)
            throws ResourceNotAvailableException {
        WSService wsService = servicesMap.get(serviceName);
        if (wsService == null) {
            throw new ResourceNotAvailableException("Cannot find requested service: " + serviceName);
        }
        WSEndpoint wsEndpoint = wsService.processEndpoints().get(portName);
        if (wsEndpoint == null) {
            throw new ResourceNotAvailableException("Cannot find requested endpoint (port): " + portName);
        }
        WSMethod wsMethod = wsEndpoint.getWSMethods().get(operationName);
        if (wsMethod == null) {
            throw new ResourceNotAvailableException("Cannot find requested method (operation): " + operationName);
        }
        return wsMethod;
    }

    public final synchronized ClassLoader getClassLoader() {
        return classLoader;
    }

    public final synchronized void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public synchronized List<Class<?>> getObjectFactories() {
        List<Class<?>> list = new LinkedList<Class<?>>();
        for (String className : classNames) {
            if (className.endsWith("ObjectFactory")) {
                try {
                    Class<?> clazz = JavaUtils.loadJavaType(className, this.getClassLoader());
                    // TODO!! Add check on @XmlRegistry
                    list.add(clazz);
                } catch (Exception e) {
                    throw new IllegalStateException("Error during loading/instanciating class:" + className
                            + " with exception message: " + e.getMessage(), e);
                }
            }
        }
        return list;
    }

    public synchronized void close() {
        try {
            IOUtils.forceDelete(new File(tmpDir));
        } catch (IOException e) {
            Logger.getLogger(BasicWSDynamicClientImpl.class).info(Messages.MESSAGES.unableToRemoveTmpdir(tmpDir));
        }
        for (WSService service : servicesMap.values()) {
            if (service instanceof WSServiceImpl) {
                for (WSEndpoint endpoint : ((WSServiceImpl) service).getEndpoints().values()) {
                    if (endpoint instanceof WSEndpointImpl) {
                        ((WSEndpointImpl) endpoint).getService().shutdown();
                    }
                }
            }
        }
        this.classLoader = null;
    }

    /**
     * @return tmpDir
     */
    public synchronized String getTmpDir() {
        return tmpDir;
    }

}
