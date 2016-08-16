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
package org.jboss.wise.core.client.impl.reflection;

import net.jcip.annotations.ThreadSafe;

import org.jboss.wise.core.client.SpiLoader;
import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.consumer.WSConsumer;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.wise.core.wsextensions.EnablerDelegate;
import org.jboss.wise.core.wsextensions.EnablerDelegateProvider;
import org.milyn.Smooks;

/**
 * This is the Wise core, i.e. the JAX-WS client that handles wsdl retrieval &amp; parsing, invocations, etc.
 *
 * @author Stefano Maestri, stefano.maestri@javalinux.it
 *
 */
@ThreadSafe
public class WSDynamicClientImpl extends BasicWSDynamicClientImpl implements WSDynamicClient {

    private final EnablerDelegate wsExtensionEnablerDelegate;

    private final Smooks smooksInstance;

    /**
     * @param builder client builder
     * @return consumer
     */
    private static WSConsumer createConsumer(WSDynamicClientBuilder builder) {
        WSConsumer consumer = (WSConsumer) SpiLoader.loadService("org.jboss.wise.consumer.WSConsumer",
                "org.jboss.wise.core.consumer.impl.jbossws.DefaultWSImportImpl");
        consumer.setVerbose(builder.isVerbose());
        consumer.setKeepSource(builder.isKeepSource());
        return consumer;
    }

    public WSDynamicClientImpl(WSDynamicClientBuilder builder) throws WiseRuntimeException {
        this(builder, createConsumer(builder), new Smooks());
    }

    protected WSDynamicClientImpl(WSDynamicClientBuilder builder, WSConsumer consumer) throws WiseRuntimeException {
        this(builder, consumer, new Smooks());
    }

    protected WSDynamicClientImpl(WSDynamicClientBuilder builder, WSConsumer consumer, Smooks smooks)
            throws WiseRuntimeException {
        super(builder, consumer);
        this.smooksInstance = smooks;
        wsExtensionEnablerDelegate = EnablerDelegateProvider.newEnablerDelegate(builder.getSecurityConfigFileURL(),
                builder.getSecurityConfigName());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.wise.core.client.WSDynamicClient#getWSExtensionEnablerDelegate()
     */
    public EnablerDelegate getWSExtensionEnablerDelegate() {
        return wsExtensionEnablerDelegate;
    }

    /**
     * @return smooksInstance
     */
    public Smooks getSmooksInstance() {
        return smooksInstance;
    }

    public synchronized void close() {
        smooksInstance.close();
        super.close();
    }

}
