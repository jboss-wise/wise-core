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

package org.jboss.wise.core.client;

import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;

import org.jboss.wise.core.exception.ResourceNotAvailableException;
import org.jboss.wise.core.wsextensions.EnablerDelegate;
import org.milyn.Smooks;
import net.jcip.annotations.ThreadSafe;

/**
 * This is the Wise core class responsible to invoke the JAX-WS tools that
 * handles wsdl retrieval & parsing. It is used to build the list of WSService
 * representing the services available in parsed wsdl.
 * 
 * @author Stefano Maestri, stefano.maestri@javalinux.it
 */
@ThreadSafe
public interface WSDynamicClient {

    /**
     * Create the services' map and gives it back. Useful when Wise is used for
     * interactive explore and invoke a service.
     * 
     * @return The Map of WSEndpoint with symbolic names as keys
     * @throws IllegalStateException
     *             thrown if method can't process or load generated classes to
     *             find a service
     */
    public Map<String, WSService> processServices() throws IllegalStateException;

    /**
     * @return The classLoader used to load generated class.
     */
    public URLClassLoader getClassLoader();
    
    /**
     * @return The ObjectFactory classes for the generated sources
     */
    public List<Class<?>> getObjectFactories();

    /**
     * It return directly the method to invoke the specified action on specified
     * port of specified service. It is the base method for
     * "one line of code invocation" (see "Wise-core Programmers guide" for more
     * information)
     * 
     * @param serviceName
     * @param portName
     * @param operationName
     * @throws ResourceNotAvailableException
     *             when the specified service, port or operation can not be
     *             found
     * @return the WSMethod class to use for effective service invocation
     */
    public WSMethod getWSMethod(String serviceName, String portName, String operationName) throws ResourceNotAvailableException;

    /**
     * 
     * @return the {@link EnablerDelegate} used to enable the WS-* for all
     *         endpoint attached all serivices attached to this
     *         {@link WSDynamicClient}.
     */
    public EnablerDelegate getWSExtensionEnablerDelegate();

    /**
     * 
     * @return the single smooks instance attached to this
     *         {@link WSDynamicClient}
     */
    public Smooks getSmooksInstance();

    public void close();

}
