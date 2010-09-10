/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import org.jboss.wise.core.client.WSEndpoint;
import org.jboss.wise.core.wsextensions.WSExtensionEnabler;

public class EndpointMethodCaller implements Callable<Object> {

    private final ThreadLocal<Object> epUnderlyingObjectInstance = new ThreadLocal<Object>() {
        @Override
        protected Object initialValue() {
            return epInstance.createInstance();
        }
    };
    protected final WSEndpoint epInstance;
    private final Method methodPointer;
    private final Object[] args;

    /**
     * @param epInstance
     * @param methodPointer
     * @param args
     */
    public EndpointMethodCaller( WSEndpoint epInstance,
                                 Method methodPointer,
                                 Object[] args ) {
        super();

        this.epInstance = epInstance;
        this.methodPointer = methodPointer;
        this.args = args;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.concurrent.Callable#call()
     */
    public Object call() throws Exception {
        this.addHandlers();
        this.visitEnabler();
        this.setUsername();
        this.setPassword();
        this.setTargetUrl();
        return methodPointer.invoke(epUnderlyingObjectInstance.get(), args);
    }

    public void visitEnabler() {
        if (epInstance.getExtensions() != null) {
            for (WSExtensionEnabler enabler : epInstance.getExtensions()) {
                enabler.enable(epUnderlyingObjectInstance.get());
            }
        }
    }

    public void addHandlers() {
        if (epInstance.getHandlers() != null) {

            for (Handler<?> handler : epInstance.getHandlers()) {
                List<Handler> handlerChain = ((BindingProvider)epUnderlyingObjectInstance.get()).getBinding().getHandlerChain();
                handlerChain.add(handler);
                ((BindingProvider)epUnderlyingObjectInstance.get()).getBinding().setHandlerChain(handlerChain);
            }
        }
    }

    public synchronized void setUsername() {
        if (epInstance.getUsername() != null) {
            ((BindingProvider)epUnderlyingObjectInstance.get()).getRequestContext().put(BindingProvider.USERNAME_PROPERTY,
                                                                                        epInstance.getUsername());
        }
    }

    public synchronized void setPassword() {
        if (epInstance.getPassword() != null) {
            ((BindingProvider)epUnderlyingObjectInstance.get()).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,
                                                                                        epInstance.getPassword());
        }
    }

    public synchronized void setTargetUrl() {
        if (epInstance.getTargetUrl() != null) {
            ((BindingProvider)epUnderlyingObjectInstance.get()).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                                                                                        epInstance.getTargetUrl());
        }
    }

}
