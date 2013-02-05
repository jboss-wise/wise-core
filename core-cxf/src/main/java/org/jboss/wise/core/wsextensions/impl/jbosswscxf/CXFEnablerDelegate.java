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
package org.jboss.wise.core.wsextensions.impl.jbosswscxf;

import net.jcip.annotations.Immutable;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.jboss.wise.core.wsextensions.DefaultEnablerDelegate;

/**
 * CXF version of EnablerDelegate 
 * 
 * @author alessio.soldano@jboss.com
 */
@Immutable
public class CXFEnablerDelegate extends DefaultEnablerDelegate {

    public CXFEnablerDelegate() {
	super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.wsextensions.EnablerDelegate#visitWSAddressing(Object)
     */
    public void visitWSAddressing(Object endpointInstance) throws UnsupportedOperationException {
	Bus bus = BusFactory.getThreadDefaultBus();
	Client client = ClientProxy.getClient(endpointInstance);
	WSAddressingFeature feature = new WSAddressingFeature();
	feature.initialize(client, bus);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.wsextensions.EnablerDelegate#visitWSRM(Object)
     */
    public void visitWSRM(Object endpointInstance) throws UnsupportedOperationException {
	throw new UnsupportedOperationException("Not yet implemented");
    }

    public void visitWSSecurity(Object endpointInstance) throws UnsupportedOperationException, IllegalStateException {
	throw new UnsupportedOperationException("Not yet implemented");
    }

}
