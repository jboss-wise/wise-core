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
package org.jboss.wise.core.wsextensions.impl;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.WSEndpoint;
import org.jboss.wise.core.client.impl.reflection.WSEndpointImpl;
import org.jboss.wise.core.wsextensions.DefaultEnablerDelegate;
import org.jboss.wise.core.wsextensions.EnablerDelegate;
import org.jboss.wise.core.wsextensions.WSExtensionEnabler;
import org.junit.Before;
import org.junit.Test;

/**
 * @author stefano.maestri@javalinux.it
 */
public class MTOMEnablerTest {

    private WSDynamicClient client;
    private EnablerDelegate delegate;

    @Before
    public void before() {
        client = mock(WSDynamicClient.class);
        delegate = mock(DefaultEnablerDelegate.class);
        when(client.getWSExtensionEnablerDelegate()).thenReturn(delegate);
    }

    @Test
    public void shouldFindVisitorImpl() {
        WSExtensionEnabler enabler = new MTOMEnabler(client);
        assertThat(enabler.getDelegate(), is(DefaultEnablerDelegate.class));
    }

    @Test
    public void shouldDelegateToVisitor() {
        WSEndpoint ep = mock(WSEndpointImpl.class);
        WSExtensionEnabler enabler = new MTOMEnabler(client);
        enabler.enable(ep);
        verify(delegate).visitMTOM(ep);

    }
}
