/*
 * JBoss, Home of Professional Open Source Copyright 2006, JBoss Inc., and
 * individual contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of individual
 * contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package org.jboss.wise.core.client.impl.reflection;

import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.ws.WebEndpoint;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;
import org.jboss.wise.core.client.WSEndpoint;
import org.jboss.wise.core.client.WSService;
import org.jboss.wise.core.exception.WiseRuntimeException;

/**
 * @author stefano.maestri@javalinux.it
 */
@ThreadSafe
@Immutable
public class WSServiceImpl implements WSService {

    private final Class<?> serviceClass;
    private final URLClassLoader classLoader;
    private final Object service;
    private final String userName;
    private final String password;
    private final Map<String, WSEndpoint> endpoints = Collections.synchronizedMap(new HashMap<String, WSEndpoint>());
    private final int maxThreadPoolSize;

    /**
     * @param serviceClass
     * @param classLoader
     * @param service
     * @param userName
     * @param password
     * @param maxThreadPoolSize the max pool size for method execution of service attached endpoint.
     */
    public WSServiceImpl( Class<?> serviceClass,
                          URLClassLoader classLoader,
                          Object service,
                          String userName,
                          String password,
                          int maxThreadPoolSize ) {
        super();
        this.serviceClass = serviceClass;
        this.classLoader = classLoader;
        this.service = service;
        this.userName = userName;
        this.password = password;
        endpoints.clear();
        this.processEndpoints();
        this.maxThreadPoolSize = maxThreadPoolSize;
    }

    public Map<String, WSEndpoint> getEndpoints() {
        return endpoints;
    }

    /**
     * Create the endpoints' map and gives it back.
     * 
     * @return The Map of WSEndpoint with symbolic names as keys
     */
    public Map<String, WSEndpoint> processEndpoints() {
        if (endpoints.size() > 0) {
            return endpoints;
        }

        for (Method method : this.getServiceClass().getMethods()) {
            WebEndpoint annotation = method.getAnnotation(WebEndpoint.class);
            if (annotation != null) {
                WSEndpoint ep;
                try {
                    if (method.getParameterTypes().length == 0) // required to support JAX-WS 2.1, as you get 2 @WebEndpoint ->
                    // exclude the one with WebServiceFeatures for now
                    {
                        ep = this.getWiseEndpoint(method, annotation.name());
                        endpoints.put(annotation.name(), ep);
                    }
                } catch (WiseRuntimeException e) {
                    e.printStackTrace();
                }

            }
        }
        return endpoints;
    }

    private WSEndpoint getWiseEndpoint( Method method,
                                        String name ) throws WiseRuntimeException {
        ClassLoader oldLoader = SecurityActions.getContextClassLoader();
        WSEndpointImpl ep = new WSEndpointImpl(this.maxThreadPoolSize);
        try {
            SecurityActions.setContextClassLoader(this.getClassLoader());
            ep.setClassLoader(this.getClassLoader());
            // ep.setUnderlyingObjectInstance(this.getServiceClass().getMethod(method.getName(),
            // method.getParameterTypes()).invoke(this.getService(),
            // (Object[])null));
            ep.setWsEndPointbuilder(new WSEndPointbuilder(this.getServiceClass(), this.getService(), method));
            ep.setName(name);
            ep.setUnderlyingObjectClass(this.getServiceClass().getMethod(method.getName(), method.getParameterTypes()).getReturnType());
            if (userName != null && password != null) {
                ep.setUsername(userName);
                ep.setPassword(password);
            }

        } catch (Exception e) {
            throw new WiseRuntimeException("Error while reading an endpoint!", e);
        } finally {
            SecurityActions.setContextClassLoader(oldLoader);
        }
        return ep;
    }

    private synchronized final Class<?> getServiceClass() {
        return serviceClass;
    }

    public synchronized final URLClassLoader getClassLoader() {
        return classLoader;
    }

    private synchronized final Object getService() {
        return service;
    }

    public class WSEndPointbuilder {
        private final Class<?> serviceClass;
        private final Object serviceObject;
        private final Method buildMethod;

        /**
         * @param serviceClass
         * @param serviceObject
         * @param buildMethod
         */
        public WSEndPointbuilder( Class<?> serviceClass,
                                  Object serviceObject,
                                  Method buildMethod ) {
            super();
            this.serviceClass = serviceClass;
            this.serviceObject = serviceObject;
            this.buildMethod = buildMethod;
        }

        public Object createEndPointUnderlyingObject() {
            try {
                return serviceClass.getMethod(buildMethod.getName(), buildMethod.getParameterTypes()).invoke(serviceObject,
                                                                                                             (Object[])null);
            } catch (Exception e) {
                // TODO: something better
                e.printStackTrace();
                return null;
            }
        }
    }
}
