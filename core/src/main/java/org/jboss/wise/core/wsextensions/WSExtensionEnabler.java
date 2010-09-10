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
package org.jboss.wise.core.wsextensions;

import net.jcip.annotations.ThreadSafe;
import org.jboss.wise.core.client.WSEndpoint;

/**
 * It is an interface defining a WSExtension to be enabled on an endpoint using wise-core client APIs. The basic idea is to add
 * all WSExtension you want to enable to a {@link WSEndpoint} using addWSExtension method. WSExtension implementation are meant to
 * be pure declarative class delegating all their operations to a "visitor" class injected into the system with IOC Different
 * Visitors implement {@link EnablerDelegate} and have to take care to implement necessary steps to implement various WSExtension
 * for the JAXWS implementation for which they are supposed to work.
 * 
 * @author stefano.maestri@javalinux.it
 */
@ThreadSafe
public interface WSExtensionEnabler {

    /**
     * This is the call back method invoked by {@link WSEndpoint} to ask this extension to enable itself. Implementer should
     * delegate the effective job to {@link EnablerDelegate} implementation for the right JAX-WS stack in use.
     * 
     * @param endpointInstance
     */
    public abstract void enable( Object endpointInstance );

    /**
     * For test purpose
     * 
     * @return visitor
     */
    public EnablerDelegate getDelegate();

}
