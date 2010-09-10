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
package org.jboss.wise.core.wsextensions.impl.jbosswsnative;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.SOAPBinding;

import org.jboss.wise.core.client.WSEndpoint;
import org.jboss.ws.extensions.addressing.jaxws.WSAddressingClientHandler;
import org.junit.Test;

/**
 * @author stefano.maestri@javalinux.it
 */
public class ReflectionEnablerDelegateTest {

    /**
     * Test method for {@link org.jboss.wise.core.wsextensions.impl.jbosswsnative.DefaultEnablerDelegate#visitMTOM(Object)} .
     */
    @Test
    public void visitMTOMShouldSetMTOMOnBiding() {
	ReflectionEnablerDelegate delegate = new ReflectionEnablerDelegate(null, null);
        BindingProvider bindingProvider = mock(BindingProvider.class);
        SOAPBinding binding = mock(SOAPBinding.class);
        when(bindingProvider.getBinding()).thenReturn(binding);
        delegate.visitMTOM(bindingProvider);
        verify(binding).setMTOMEnabled(true);
    }

    /**
     * Test method for
     * {@link org.jboss.wise.core.wsextensions.impl.jbosswsnative.DefaultEnablerDelegate#visitWSAddressing(Object)} .
     */
    @Test
    public void visitWSAddressingShouldAddRightHandler() {
	ReflectionEnablerDelegate delegate = new ReflectionEnablerDelegate(null, null);
        List<Handler> handlerList = new ArrayList<Handler>();
        BindingProvider bindingProvider = mock(BindingProvider.class);
        SOAPBinding binding = mock(SOAPBinding.class);
        when(bindingProvider.getBinding()).thenReturn(binding);
        when(binding.getHandlerChain()).thenReturn(handlerList);
        delegate.visitWSAddressing(bindingProvider);
        assertThat(handlerList.get(0), instanceOf(WSAddressingClientHandler.class));
    }

    /**
     * Test method for {@link org.jboss.wise.core.wsextensions.impl.jbosswsnative.DefaultEnablerDelegate#visitWSRM(Object)} .
     */
    @Test( expected = UnsupportedOperationException.class )
    public void visitWSRMShouldThrowUnsupportedOperationException() {
	ReflectionEnablerDelegate delegate = new ReflectionEnablerDelegate(null, null);
        WSEndpoint endpoint = mock(WSEndpoint.class);
        delegate.visitWSRM(endpoint);

    }

    // /**
    // * Test method for
    // * {@link
    // org.jboss.wise.core.wsextensions.impl.jbosswsnative.ReflectionEnablerDelegate#visitWSSecurity(org.jboss.wise.core.client.WSEndpoint)}
    // * .
    // */
    // @Test
    // public void visitWSSecurityShouldSetConfig() {
    // ReflectionEnablerDelegate delegate = new ReflectionEnablerDelegate();
    //    
    // WSEndpoint endpoint = mock(WSEndpoint.class);
    // StubExt stub = mock(StubExt.class);
    // BindingProvider provider = mock(BindingProvider.class);
    // when(endpoint.getUnderlyingObjectInstance()).thenReturn(provider).thenReturn(provider).thenReturn(stub);
    // delegate.visitWSSecurity(endpoint);
    // verify(stub).setSecurityConfig(anyString());
    // verify(stub).setConfigName(anyString());
    //    
    // }

}
