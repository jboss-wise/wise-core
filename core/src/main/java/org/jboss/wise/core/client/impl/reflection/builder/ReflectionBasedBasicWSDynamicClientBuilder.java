/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.wise.core.client.impl.reflection.builder;

import static org.jboss.wise.core.utils.DefaultConfig.MAX_THREAD_POOL_SIZE;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.jboss.logging.Logger.Level;
import org.jboss.logging.Logger;
import org.jboss.wise.core.client.BasicWSDynamicClient;
import org.jboss.wise.core.client.builder.BasicWSDynamicClientBuilder;
import org.jboss.wise.core.client.impl.reflection.BasicWSDynamicClientImpl;
import org.jboss.wise.core.client.impl.wsdlResolver.Connection;
import org.jboss.wise.core.client.impl.wsdlResolver.WSDLResolver;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.wise.core.utils.IDGenerator;
import org.jboss.wise.core.utils.IOUtils;
import org.jboss.wise.core.utils.JavaUtils;
import org.jboss.wise.core.utils.LoggingOutputStream;

/**
 * @author stefano.maestri@javalinux.it
 * @author alessio.soldano@jboss.com
 */
@ThreadSafe
public class ReflectionBasedBasicWSDynamicClientBuilder implements BasicWSDynamicClientBuilder {

    private static Logger logger = Logger.getLogger(ReflectionBasedBasicWSDynamicClientBuilder.class);

    private static PrintStream ps = new PrintStream(new LoggingOutputStream(logger, Level.INFO), true);

    @GuardedBy("this")
    private String wsdlURL;

    @GuardedBy("this")
    private String userName;

    @GuardedBy("this")
    private String password;

    @GuardedBy("this")
    private String tmpDir = AccessController.doPrivileged(new PropertyAccessAction("java.io.tmpdir"));

    @GuardedBy("this")
    private String targetPackage;

    @GuardedBy("this")
    private List<File> bindingFiles = null;

    @GuardedBy("this")
    private File catalog = null;

    @GuardedBy("this")
    private String securityConfigURL;

    @GuardedBy("this")
    private String securityConfigName;

    @GuardedBy("this")
    private boolean keepSource;

    @GuardedBy("this")
    private boolean verbose;

    @GuardedBy("this")
    private boolean excludeNonSoapPorts;

    @GuardedBy("this")
    private String normalizedWsdlUrl;

    @GuardedBy("this")
    private String clientSpecificTmpDir;

    @GuardedBy("this")
    private PrintStream messageStream = ps;

    @GuardedBy("this")
    private int maxThreadPoolSize = MAX_THREAD_POOL_SIZE.getIntValue();

    public ReflectionBasedBasicWSDynamicClientBuilder() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#build()
     */
    public synchronized BasicWSDynamicClient build() throws IllegalStateException, WiseRuntimeException {
        clientSpecificTmpDir = tmpDir;
        if (clientSpecificTmpDir != null) {
            clientSpecificTmpDir = new StringBuilder().append(tmpDir).append(File.separator).append("Wise")
                    .append(IDGenerator.nextVal()).toString();
            File tmpDirFile = new File(clientSpecificTmpDir);
            try {
                IOUtils.forceMkdir(tmpDirFile);
            } catch (IOException e) {
                throw new IllegalStateException("unable to create tmp dir:" + clientSpecificTmpDir
                        + ". Please provide a valid temp dir if you didn't.");
            }
        } else {
            throw new IllegalStateException("temp dir cannot be null!");
        }

        if (this.getMaxThreadPoolSize() < 1) {
            throw new IllegalStateException("MaxThreadPoolSize cannot be less than 1");
        }
        final String wsdlUrl = this.getWsdlURL();
        final String nwu;
        if (userName != null || (JavaUtils.trimToNull(wsdlUrl) != null && Connection.isLocalAddress(wsdlUrl))) {
            nwu = this.transferWSDL(userName, password, clientSpecificTmpDir);
        } else {
            nwu = wsdlUrl;
        }
        this.setNormalizedWsdlUrl(nwu);

        if (nwu == null || nwu.trim().length() == 0) {
            throw new IllegalStateException("wsdlURL cannot be null");
        }

        return createClient();

    }

    protected BasicWSDynamicClient createClient() {
        return new BasicWSDynamicClientImpl(this);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#getWsdlURL()
     */
    public final synchronized String getWsdlURL() {
        return wsdlURL;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#wsdlURL(java.lang.String)
     */
    public synchronized BasicWSDynamicClientBuilder wsdlURL(String wsdlURL) {
        this.wsdlURL = wsdlURL;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#getUserName()
     */
    public final synchronized String getUserName() {
        return userName;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#userName(java.lang.String)
     */
    public synchronized BasicWSDynamicClientBuilder userName(String userName) {
        this.userName = userName;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#getPassword()
     */
    public final synchronized String getPassword() {
        return password;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#password(java.lang.String)
     */
    public synchronized BasicWSDynamicClientBuilder password(String password) {
        this.password = password;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#getTmpDir()
     */
    public final synchronized String getTmpDir() {
        return tmpDir;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#getTargetPackage()
     */
    public final synchronized String getTargetPackage() {
        return targetPackage;
    }

    public final synchronized List<File> getBindingFiles() {
        return this.bindingFiles;
    }

    public final synchronized File getCatalogFile() {
        return this.catalog;
    }

    public final synchronized void setBindingFiles(List<File> bindings) {
        this.bindingFiles = bindings;
    }

    public final synchronized void setCatelogFile(File catalog) {
        this.catalog = catalog;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#targetPackage(java.lang.String)
     */
    public synchronized BasicWSDynamicClientBuilder targetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#tmpDir(java.lang.String)
     */
    public synchronized BasicWSDynamicClientBuilder tmpDir(String tmpDir) {
        this.tmpDir = tmpDir;
        return this;
    }

    public synchronized BasicWSDynamicClientBuilder bindingFiles(List<File> bindings) {
        this.bindingFiles = bindings;
        return this;
    }

    public synchronized BasicWSDynamicClientBuilder catalogFile(File catalogFile) {
        catalog = catalogFile;
        return this;
    }

    /**
     * Transfer the wsdl to local filesystem. This is required because jaxws tools can't deal with any kind of authentication.
     *
     * @param username
     * @param password
     * @param tmpDir
     * @return the String representing the url
     * @throws WiseRuntimeException
     *
     * @throws WiseRuntimeException If the wsdl cannot be retrieved
     */
    synchronized String transferWSDL(String username, String password, String tmpDir) throws WiseRuntimeException {
        try {
            WSDLResolver resolver = username != null ? new WSDLResolver(tmpDir, new Connection(username, password))
                    : new WSDLResolver(tmpDir);
            File wsdlFile = resolver.retrieveWsdlFile(new URL(getWsdlURL()));
            String result = wsdlFile.getAbsolutePath();
            return result;
        } catch (Exception e) {
            throw new WiseRuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#getSecurityConfigFileURL()
     */
    public synchronized String getSecurityConfigFileURL() {
        return securityConfigURL;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#getSecurityConfigName()
     */
    public synchronized String getSecurityConfigName() {
        return securityConfigName;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#isKeepSource()
     */
    public synchronized boolean isKeepSource() {
        return keepSource;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#isVerbose()
     */
    public synchronized boolean isVerbose() {
        return verbose;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#isExcludeNonSOAPPorts()
     */
    public synchronized boolean isExcludeNonSOAPPorts() {
        return this.excludeNonSoapPorts;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#keepSource(boolean)
     */
    public synchronized BasicWSDynamicClientBuilder keepSource(boolean bool) {
        this.keepSource = bool;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#securityConfigName(java.lang.String)
     */
    public synchronized BasicWSDynamicClientBuilder securityConfigName(String name) {
        this.securityConfigName = name;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#securityConfigUrl(java.lang.String)
     */
    public synchronized BasicWSDynamicClientBuilder securityConfigUrl(String url) {
        this.securityConfigURL = url;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#verbose(boolean)
     */
    public synchronized BasicWSDynamicClientBuilder verbose(boolean bool) {
        this.verbose = bool;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#excludeNonSOAPPorts(boolean)
     */
    public synchronized BasicWSDynamicClientBuilder excludeNonSOAPPorts(boolean exclude) {
        this.excludeNonSoapPorts = exclude;
        return this;
    }

    /**
     * @return normalizedWsdlUrl
     */
    public synchronized String getNormalizedWsdlUrl() {
        return normalizedWsdlUrl;
    }

    /**
     * @param normalizedWsdlUrl Sets normalizedWsdlUrl to the specified value.
     */
    private synchronized void setNormalizedWsdlUrl(String normalizedWsdlUrl) {
        this.normalizedWsdlUrl = normalizedWsdlUrl;
    }

    /**
     * @return clientSpecificTmpDir
     */
    public synchronized String getClientSpecificTmpDir() {
        return clientSpecificTmpDir;
    }

    public synchronized PrintStream getMessageStream() {
        return messageStream;
    }

    public synchronized BasicWSDynamicClientBuilder messageStream(PrintStream messageStream) {
        this.messageStream = messageStream;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#getMaxThreadPoolSize()
     */
    public synchronized int getMaxThreadPoolSize() {
        return this.maxThreadPoolSize;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#maxThreadPoolSize(int)
     */
    public synchronized BasicWSDynamicClientBuilder maxThreadPoolSize(int maxThreadPoolSize) {
        this.maxThreadPoolSize = maxThreadPoolSize;
        return this;
    }

    private static class PropertyAccessAction implements PrivilegedAction<String> {

        private final String name;

        PropertyAccessAction(String name) {
            this.name = name;
        }

        public String run() {
            return System.getProperty(name);
        }
    }

}
