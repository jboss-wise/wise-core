/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
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
package org.jboss.wise.core.client;

import java.util.List;
import java.util.Map;

import org.jboss.wise.core.exception.ResourceNotAvailableException;
import net.jcip.annotations.ThreadSafe;

/**
 * Basic version of WSDynamicClient offering no extension and Smooks configuration funcitonalities.
 *
 * @author Stefano Maestri, stefano.maestri@javalinux.it
 */
@ThreadSafe
public interface BasicWSDynamicClient {

    /**
     * Create the services' map and gives it back. Useful when Wise is used for interactive explore and invoke a service.
     *
     * @return The Map of WSEndpoint with symbolic names as keys
     * @throws IllegalStateException thrown if method can't process or load generated classes to find a service
     */
    Map<String, WSService> processServices() throws IllegalStateException;

    /**
     * @return The classLoader used to load generated class.
     */
    ClassLoader getClassLoader();

    /**
     * @return The ObjectFactory classes for the generated sources
     */
    List<Class<?>> getObjectFactories();

    /**
     * It return directly the method to invoke the specified action on specified port of specified service. It is the base
     * method for "one line of code invocation" (see "Wise-core Programmers guide" for more information)
     *
     * @param serviceName string
     * @param portName string
     * @param operationName string
     * @throws ResourceNotAvailableException when the specified service, port or operation can not be found
     * @return the WSMethod class to use for effective service invocation
     */
    WSMethod getWSMethod(String serviceName, String portName, String operationName) throws ResourceNotAvailableException;

    void close();

}
