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

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import net.jcip.annotations.ThreadSafe;

import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.client.impl.reflection.WSDynamicClientImpl;
import org.jboss.wise.core.exception.WiseRuntimeException;

/**
 * @author stefano.maestri@javalinux.it
 * @author alessio.soldano@jboss.com
 */
@ThreadSafe
public class ReflectionBasedWSDynamicClientBuilder extends ReflectionBasedBasicWSDynamicClientBuilder implements
        WSDynamicClientBuilder {

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#build()
     */
    public synchronized WSDynamicClient build() throws IllegalStateException, WiseRuntimeException {
        return (WSDynamicClient) super.build();

    }

    protected WSDynamicClient createClient() {
        return new WSDynamicClientImpl(this);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#wsdlURL(java.lang.String)
     */
    public final synchronized WSDynamicClientBuilder wsdlURL(String wsdlURL) {
        super.wsdlURL(wsdlURL);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#userName(java.lang.String)
     */
    public synchronized WSDynamicClientBuilder userName(String userName) {
        super.userName(userName);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#password(java.lang.String)
     */
    public synchronized WSDynamicClientBuilder password(String password) {
        super.password(password);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#targetPackage(java.lang.String)
     */
    public synchronized WSDynamicClientBuilder targetPackage(String targetPackage) {
        super.targetPackage(targetPackage);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#tmpDir(java.lang.String)
     */
    public synchronized WSDynamicClientBuilder tmpDir(String tmpDir) {
        super.tmpDir(tmpDir);
        return this;
    }

    public synchronized WSDynamicClientBuilder bindingFiles(List<File> bindings) {
        super.bindingFiles(bindings);
        return this;
    }

    public synchronized WSDynamicClientBuilder catalogFile(File catalogFile) {
        super.catalogFile(catalogFile);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#keepSource(boolean)
     */
    public synchronized WSDynamicClientBuilder keepSource(boolean bool) {
        super.keepSource(bool);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#securityConfigName(java.lang.String)
     */
    public synchronized WSDynamicClientBuilder securityConfigName(String name) {
        super.securityConfigName(name);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#securityConfigUrl(java.lang.String)
     */
    public synchronized WSDynamicClientBuilder securityConfigUrl(String url) {
        super.securityConfigUrl(url);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#verbose(boolean)
     */
    public synchronized WSDynamicClientBuilder verbose(boolean bool) {
        super.verbose(bool);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#excludeNonSOAPPorts(boolean)
     */
    public synchronized WSDynamicClientBuilder excludeNonSOAPPorts(boolean exclude) {
        super.excludeNonSOAPPorts(exclude);
        return this;
    }

    public synchronized WSDynamicClientBuilder messageStream(PrintStream messageStream) {
        super.messageStream(messageStream);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.builder.WSDynamicClientBuilder#maxThreadPoolSize(int)
     */
    public synchronized WSDynamicClientBuilder maxThreadPoolSize(int maxThreadPoolSize) {
        super.maxThreadPoolSize(maxThreadPoolSize);
        return this;
    }
}
