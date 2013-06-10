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
package org.jboss.wise.core.client.impl;

import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.jboss.wise.core.client.WSService;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.client.impl.reflection.WSDynamicClientImpl;
import org.jboss.wise.core.consumer.WSConsumer;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.wsf.stack.cxf.client.configuration.JBossWSBusFactory;
import org.milyn.Smooks;

/**
 * A cxf specific version of the wise-core WSDynamicClient
 * 
 * @author alessio.soldano@jboss.com
 * @since 10-Jun-2013
 * 
 */
public class CXFDynamicClient extends WSDynamicClientImpl {
    
    private Bus bus;

    public CXFDynamicClient(WSDynamicClientBuilder builder) throws WiseRuntimeException {
	super(builder);
    }
    
    protected CXFDynamicClient(WSDynamicClientBuilder builder, WSConsumer consumer) throws WiseRuntimeException {
	super(builder, consumer);
    }
    
    public CXFDynamicClient(WSDynamicClientBuilder builder, WSConsumer consumer, Smooks smooks) throws WiseRuntimeException {
	super(builder, consumer, smooks);
    }
    
    @Override
    protected void prepare(WSDynamicClientBuilder builder, WSConsumer consumer) {
	this.bus = JBossWSBusFactory.newInstance().createBus();
	final Bus prevBus = BusFactory.getThreadDefaultBus(false);
	try {
	    BusFactory.setThreadDefaultBus(bus);
	    super.prepare(builder, consumer);
	} finally {
	    BusFactory.setThreadDefaultBus(prevBus);
	}
    }
    
    @Override
    public synchronized Map<String, WSService> processServices() throws IllegalStateException {
	final Bus prevBus = BusFactory.getThreadDefaultBus(false);
	try {
	    if (bus != prevBus) {
		BusFactory.setThreadDefaultBus(bus);
	    }
	    return super.processServices();
	} finally {
	    if (bus != prevBus) {
		BusFactory.setThreadDefaultBus(prevBus);
	    }
	}
    }
    
    @Override
    public synchronized void close() {
	try {
	    super.close();
	} finally {
	    bus.shutdown(true);
	}
    }
}
