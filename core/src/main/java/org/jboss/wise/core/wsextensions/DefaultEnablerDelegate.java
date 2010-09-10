/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.wise.core.wsextensions;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPBinding;

import net.jcip.annotations.Immutable;

/**
 * Default implementation of {@link EnablerDelegate}
 * 
 * @author alessio.soldano@jboss.com
 * @since 28-Aug-2010
 */
@Immutable
public class DefaultEnablerDelegate implements EnablerDelegate {

    /**
     * {@inheritDoc}
     * 
     *  @see org.jboss.wise.core.wsextensions.EnablerDelegate#setConfigFile(String)
     */
    public void setConfigFile(String configFile) {
	//NOOP
    }
    
    /**
     * {@inheritDoc}
     * 
     *  @see org.jboss.wise.core.wsextensions.EnablerDelegate#setConfigName(String)
     */
    public void setConfigName(String configName) {
	//NOOP
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.wsextensions.EnablerDelegate#visitMTOM(Object)
     */
    public void visitMTOM( Object endpointInstance ) throws UnsupportedOperationException {
        ((SOAPBinding)((BindingProvider)endpointInstance).getBinding()).setMTOMEnabled(true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.wsextensions.EnablerDelegate#visitWSAddressing(Object)
     */
    public void visitWSAddressing( Object endpointInstance ) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.wsextensions.EnablerDelegate#visitWSRM(Object)
     */
    public void visitWSRM( Object endpointInstance ) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void visitWSSecurity( Object endpointInstance ) throws UnsupportedOperationException, IllegalStateException {
	throw new UnsupportedOperationException();
    }
}
