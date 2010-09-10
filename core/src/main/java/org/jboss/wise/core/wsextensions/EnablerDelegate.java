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

/**
 * It is an interface defining a visitor pattern which is used by
 * {@link WSExtensionEnabler} to delegate the effective work needed to enable
 * single extensions. The idea is to inject a {@link EnablerDelegate}
 * implementations using IOC (aka jboss-beans.xml file) having the right
 * implementation of extension enabling for current used JAX-WS stack and/or
 * access type to generated classes (reflection vs javassist). It is in fact the
 * way used by wise-core to decouple enabling of extension and
 * operation/configurations needed to do so. You can always provide your own
 * implementation of {@link EnablerDelegate} if you need something different
 * during enabling of Extensions.
 * 
 * @author stefano.maestri@javalinux.it
 */
@ThreadSafe
public interface EnablerDelegate {

    /**
     * Sets the configFile to be used, if any
     * 
     * @param configFile
     */
    public void setConfigFile(String configFile);
    
    /**
     * Sets the configName to be used, if any
     * 
     * @param configName
     */
    public void setConfigName(String configName);
    
    /**
     * 
     * @param endpointInstance
     * @throws UnsupportedOperationException
     * @throws IllegalStateException
     */
    public void visitWSSecurity(Object endpointInstance) throws UnsupportedOperationException, IllegalStateException;

    /**
     * 
     * @param endpointInstance
     * @throws UnsupportedOperationException
     */
    public void visitWSRM(Object endpointInstance) throws UnsupportedOperationException;

    /**
     * 
     * @param endpointInstance
     * @throws UnsupportedOperationException
     */
    public void visitWSAddressing(Object endpointInstance) throws UnsupportedOperationException;

    /**
     * 
     * @param endpointInstance
     * @throws UnsupportedOperationException
     */
    public void visitMTOM(Object endpointInstance) throws UnsupportedOperationException;
}
