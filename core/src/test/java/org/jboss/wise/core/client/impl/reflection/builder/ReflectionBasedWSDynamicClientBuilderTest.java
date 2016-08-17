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
package org.jboss.wise.core.client.impl.reflection.builder;

import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.client.factories.WSDynamicClientFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Class to test {@link ReflectionBasedWSDynamicClientBuilder}
 *
 * @author stefano.maestri@javalinux.it
 */
public class ReflectionBasedWSDynamicClientBuilderTest {

    WSDynamicClientBuilder builder;

    @Before
    public void before() throws Exception {
        builder = WSDynamicClientFactory.getJAXWSClientBuilder();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfTrimmedWsdlIsNotSetted() throws Exception {
        builder.build();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfWsdlIsNull() throws Exception {
        builder.wsdlURL(null);
        builder.build();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfTrimmedWsdlLengthIsZero() throws Exception {
        builder.wsdlURL(" ");
        builder.build();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfMaxThreadPoolSizeIsLessThanOne() throws Exception {
        builder.maxThreadPoolSize(0);
        builder.build();
    }
}
