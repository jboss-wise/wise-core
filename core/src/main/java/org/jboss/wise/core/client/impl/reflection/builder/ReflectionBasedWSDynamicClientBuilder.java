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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.client.impl.reflection.WSDynamicClientImpl;
import org.jboss.wise.core.client.impl.wsdlResolver.Connection;
import org.jboss.wise.core.client.impl.wsdlResolver.WSDLResolver;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.wise.core.utils.IDGenerator;
import org.jboss.wise.core.utils.LoggingOutputStream;

/**
 * @author stefano.maestri@javalinux.it
 * @author alessio.soldano@jboss.com
 */
@ThreadSafe
public class ReflectionBasedWSDynamicClientBuilder implements WSDynamicClientBuilder {

    private static Logger logger = Logger.getLogger(ReflectionBasedWSDynamicClientBuilder.class);
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
    private String normalizedWsdlUrl;

    @GuardedBy("this")
    private String clientSpecificTmpDir;

    @GuardedBy("this")
    private PrintStream messageStream = ps;

    @GuardedBy("this")
    private int maxThreadPoolSize = MAX_THREAD_POOL_SIZE.getIntValue();

    public ReflectionBasedWSDynamicClientBuilder() {
	super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#build()
     */
    public synchronized WSDynamicClient build() throws IllegalStateException, WiseRuntimeException {
	clientSpecificTmpDir = tmpDir;
	if (clientSpecificTmpDir != null) {
	    clientSpecificTmpDir = new StringBuilder().append(tmpDir).append(File.separator).append("Wise").append(IDGenerator.nextVal()).toString();
	    File tmpDirFile = new File(clientSpecificTmpDir);
	    try {
		FileUtils.forceMkdir(tmpDirFile);
	    } catch (IOException e) {
		throw new IllegalStateException(
			"unable to create tmp dir:" + clientSpecificTmpDir + ". Please provide a valid temp dir if you didn't.");
	    }
	} else {
	    throw new IllegalStateException("temp dir cannot be null!");
	}

	if (this.getMaxThreadPoolSize() < 1) {
	    throw new IllegalStateException("MaxThreadPoolSize cannot be less than 1");
	}
	String wsdlUrl = this.getWsdlURL();
	if (userName != null || (StringUtils.trimToNull(wsdlUrl) != null && Connection.isLocalAddress(wsdlUrl))) {
	    this.setNormalizedWsdlUrl(this.transferWSDL(userName, password, clientSpecificTmpDir));
	} else {
	    this.setNormalizedWsdlUrl(wsdlUrl);
	}

	if (this.getNormalizedWsdlUrl() == null || this.getNormalizedWsdlUrl().trim().length() == 0) {
	    throw new IllegalStateException("wsdlURL cannot be null");
	}

	return createClient();

    }
    
    protected WSDynamicClient createClient() {
	return new WSDynamicClientImpl(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#getWsdlURL()
     */
    public synchronized final String getWsdlURL() {
	return wsdlURL;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#wsdlURL(java.lang.String)
     */
    public synchronized final WSDynamicClientBuilder wsdlURL(String wsdlURL) {
	this.wsdlURL = wsdlURL;
	return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#getUserName()
     */
    public synchronized final String getUserName() {
	return userName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#userName(java.lang.String)
     */
    public synchronized WSDynamicClientBuilder userName(String userName) {
	this.userName = userName;
	return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#getPassword()
     */
    public synchronized final String getPassword() {
	return password;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#password(java.lang.String)
     */
    public synchronized WSDynamicClientBuilder password(String password) {
	this.password = password;
	return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#getTmpDir()
     */
    public synchronized final String getTmpDir() {
	return tmpDir;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#getTargetPackage()
     */
    public synchronized final String getTargetPackage() {
	return targetPackage;
    }

    public synchronized final List<File> getBindingFiles() {
	return this.bindingFiles;
    }

    public synchronized final File getCatalogFile() {
	return this.catalog;
    }

    public synchronized final void setBindingFiles(List<File> bindings) {
	this.bindingFiles = bindings;
    }

    public synchronized final void setCatelogFile(File catalog) {
	this.catalog = catalog;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#targetPackage(java.lang.String)
     */
    public synchronized WSDynamicClientBuilder targetPackage(String targetPackage) {
	this.targetPackage = targetPackage;
	return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#tmpDir(java.lang.String)
     */
    public synchronized WSDynamicClientBuilder tmpDir(String tmpDir) {
	this.tmpDir = tmpDir;
	return this;
    }

    public synchronized WSDynamicClientBuilder bindingFiles(List<File> bindings) {
	this.bindingFiles = bindings;
	return this;
    }

    public synchronized WSDynamicClientBuilder catalogFile(File catalogFile) {
	catalog = catalogFile;
	return this;
    }

    /**
     * Transfer the wsdl to local filesystem. This is required because jaxws
     * tools can't deal with any kind of authentication.
     * 
     * @param username
     * @param password
     * @param tmpDir
     * @return the String representing the url
     * @throws WiseRuntimeException
     * 
     * @throws WiseRuntimeException
     *             If the wsdl cannot be retrieved
     */
    synchronized String transferWSDL(String username, String password, String tmpDir) throws WiseRuntimeException {
	try {
	    WSDLResolver resolver = username != null ? new WSDLResolver(tmpDir, new Connection(username, password))
		    : new WSDLResolver(tmpDir);
	    File wsdlFile = resolver.retrieveWsdlFile(new URL(getWsdlURL()));
	    String result = wsdlFile.getAbsolutePath();
	    logger.info("Main wsdl file stored locally: " + result);
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
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#keepSource(boolean)
     */
    public synchronized WSDynamicClientBuilder keepSource(boolean bool) {
	this.keepSource = bool;
	return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#securityConfigName(java.lang.String)
     */
    public synchronized WSDynamicClientBuilder securityConfigName(String name) {
	this.securityConfigName = name;
	return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#securityConfigUrl(java.lang.String)
     */
    public synchronized WSDynamicClientBuilder securityConfigUrl(String url) {
	this.securityConfigURL = url;
	return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#verbose(boolean)
     */
    public synchronized WSDynamicClientBuilder verbose(boolean bool) {
	this.verbose = bool;
	return this;
    }

    /**
     * @return normalizedWsdlUrl
     */
    public synchronized String getNormalizedWsdlUrl() {
	return normalizedWsdlUrl;
    }

    /**
     * @param normalizedWsdlUrl
     *            Sets normalizedWsdlUrl to the specified value.
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

    public synchronized WSDynamicClientBuilder messageStream(PrintStream messageStream) {
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
    public synchronized WSDynamicClientBuilder maxThreadPoolSize(int maxThreadPoolSize) {
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
