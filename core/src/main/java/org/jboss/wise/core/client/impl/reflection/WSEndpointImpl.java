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

import static org.jboss.wise.core.utils.DefaultConfig.MAX_THREAD_POOL_SIZE;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jws.WebMethod;
import javax.xml.ws.handler.Handler;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.jboss.wise.core.client.WSEndpoint;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.client.impl.reflection.WSServiceImpl.WSEndPointbuilder;
import org.jboss.wise.core.wsextensions.WSExtensionEnabler;

/**
 * This represent a WebEndpoint and has utility methods to edit username,
 * password, endpoint address, attach handlers
 * 
 * @author Stefano Maestri, stefano.maestri@javalinux.it
 * @since 09-Sep-2007
 */
@ThreadSafe
public class WSEndpointImpl implements WSEndpoint {

    @GuardedBy("this")
    private String name;

    @GuardedBy("this")
    private Class<?> underlyingObjectClass;

    private final ExecutorService service;

    @GuardedBy("this")
    ClassLoader classLoader;

    @GuardedBy("this")
    private WSEndPointbuilder wsEndPointbuilder;

    @GuardedBy("this")
    public String userName;

    @GuardedBy("this")
    public String password;

    @GuardedBy("this")
    public String targetUrl;

    private final Map<String, WSMethod> wsMethods = Collections.synchronizedMap(new TreeMap<String, WSMethod>());

    public final List<WSExtensionEnabler> extensions = Collections.synchronizedList(new LinkedList<WSExtensionEnabler>());

    public final List<Handler<?>> handlers = Collections.synchronizedList(new LinkedList<Handler<?>>());

    public WSEndpointImpl(int maxThreadPoolSize) {
	this.wsMethods.clear();
	if (maxThreadPoolSize >= 1) {
	    this.service = Executors.newFixedThreadPool(maxThreadPoolSize);
	} else {
	    this.service = Executors.newFixedThreadPool(MAX_THREAD_POOL_SIZE.getIntValue());
	}
    }

    public Object createInstance() {
	return this.getWsEndPointbuilder().createEndPointUnderlyingObject();
    }

    public synchronized String getName() {
	return name;
    }

    /**
     * @return service
     */
    public synchronized ExecutorService getService() {
	return service;
    }

    public synchronized void setName(String name) {
	this.name = name;
    }

    public synchronized String getTargetUrl() {
	return targetUrl;

    }

    public synchronized void setTargetUrl(String targetUrl) {
	this.targetUrl = targetUrl;
    }

    public synchronized String getUsername() {
	return userName;

    }

    /**
     * Set username used for Basic HTTP auth in calling ws
     * 
     * @param username
     */
    public synchronized void setUsername(String username) {
	this.userName = username;

    }

    public synchronized String getPassword() {
	return password;
    }

    /**
     * Set password used for Basic HTTP auth in calling ws
     * 
     * @param password
     */
    public synchronized void setPassword(String password) {
	this.password = password;
    }

    public synchronized Class<?> getUnderlyingObjectClass() {
	return underlyingObjectClass;
    }

    public synchronized void setUnderlyingObjectClass(Class<?> clazz) {
	this.underlyingObjectClass = clazz;
    }

    /**
     * Add an Handler to this endpoint. Handler will apply on all endpoint
     * method called
     * 
     * @see #getWSMethods()
     * @param handler
     */
    public void addHandler(Handler<?> handler) {
	handlers.add(handler);
    }

    /**
     * @return handlers
     */
    public final List<Handler<?>> getHandlers() {
	return handlers;
    }

    /**
     * Create the webmethods' map and it back. This maps would be used by
     * clients to get a method to call and invoke it All calls of this method
     * apply all handlers added with {@link #addHandler(Handler)} method
     * 
     * @return The list of WebMethod names
     */
    public synchronized Map<String, WSMethod> getWSMethods() {
	if (wsMethods.size() > 0) {
	    return wsMethods;
	}
	for (Method method : this.getUnderlyingObjectClass().getMethods()) {
	    WebMethod annotation = method.getAnnotation(WebMethod.class);
	    if (annotation != null) {
		if (annotation.operationName() != null && !annotation.operationName().equals("")) {
		    wsMethods.put(annotation.operationName(), new WSMethodImpl(method, this));
		} else {
		    wsMethods.put(method.getName(), new WSMethodImpl(method, this));
		}
	    }
	}
	return wsMethods;
    }

    public synchronized ClassLoader getClassLoader() {
	return classLoader;
    }

    public synchronized void setClassLoader(ClassLoader classLoader) {
	this.classLoader = classLoader;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.WSEndpoint#addWSExtension(org.jboss.wise.core.wsextensions.WSExtensionEnabler)
     */
    public void addWSExtension(WSExtensionEnabler enabler) {
	extensions.add(enabler);
    }

    /**
     * @return extensions
     */
    public final List<WSExtensionEnabler> getExtensions() {
	return extensions;
    }

    /**
     * @return wsEndPointbuilder
     */
    final synchronized WSEndPointbuilder getWsEndPointbuilder() {
	return wsEndPointbuilder;
    }

    /**
     * @param wsEndPointbuilder
     *            Sets wsEndPointbuilder to the specified value.
     */
    final synchronized void setWsEndPointbuilder(WSEndPointbuilder wsEndPointbuilder) {
	this.wsEndPointbuilder = wsEndPointbuilder;
    }

}
