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

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.jboss.wise.core.client.WSEndpoint;

public class EndpointMethodPreview extends EndpointMethodCaller {
    
    private final PreviewHandler handler;

    public EndpointMethodPreview( WSEndpoint epInstance,
                                 Method methodPointer,
                                 Object[] args,
                                 OutputStream previewOutputStream) {
        super(epInstance, methodPointer, args);
        this.handler = new PreviewHandler(previewOutputStream);
    }

    @Override
    public Object call() throws Exception {
        try {
            super.call();
        } catch (Exception e) {
            //ignore, here we only need to start the invocation so that our handler is executed
        }
        return handler.os;
    }

    @Override
    public void addHandlers() {
	super.addHandlers();
	Binding binding = ((BindingProvider) epUnderlyingObjectInstance.get()).getBinding();
	@SuppressWarnings("rawtypes")
	List<Handler> handlerChain = binding.getHandlerChain();
	handlerChain.add(handler);
	binding.setHandlerChain(handlerChain);
    }
    
    private class PreviewHandler implements SOAPHandler<SOAPMessageContext> {
	
	private OutputStream os;
	
	public PreviewHandler(OutputStream os) {
	    this.os = os;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
	    try {
		SOAPMessage soapMessage = context.getMessage();
		soapMessage.writeTo(os);
	    } catch (Exception e) {
		e.printStackTrace(new PrintStream(os));
	    }
	    return false; //to stop processing handler chain, reverse direction and eventually get back to caller
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
	    return true;
	}

	@Override
	public void close(MessageContext context) {
	}

	@Override
	public Set<QName> getHeaders() {
	    return new HashSet<QName>(); // empty set
	}
    }
}
