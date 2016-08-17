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
package org.jboss.wise.core.client.factories;

import net.jcip.annotations.ThreadSafe;
import org.jboss.wise.core.client.SpiLoader;
import org.jboss.wise.core.client.builder.RSDynamicClientBuilder;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.client.jaxrs.RSDynamicClient;
import org.jboss.wise.core.exception.WiseRuntimeException;

/**
 * @author Stefano Maestri, stefano.maestri@javalinux.it
 */
@ThreadSafe
public abstract class WSDynamicClientFactory {

    public static WSDynamicClientBuilder getJAXWSClientBuilder() {
        return (WSDynamicClientBuilder) SpiLoader.loadService("org.jboss.wise.client.builder.WSDynamicClientBuilder",
                "org.jboss.wise.core.client.impl.reflection.builder.ReflectionBasedWSDynamicClientBuilder");

    }

    /**
     * Return an instance of RSDynamicClient taken from cache if possible, generate and initialise if not.
     *
     * @param endpointURL string
     * @param produceMediaTypes string
     * @param consumeMediaTypes string
     * @param httpMethod http method
     * @param userName string
     * @param password string
     * @return an instance of {@link RSDynamicClient} already initialized, ready to be called
     */
    public static RSDynamicClient getJAXRSClient(String endpointURL, RSDynamicClient.HttpMethod httpMethod,
            String produceMediaTypes, String consumeMediaTypes, String userName, String password) {

        RSDynamicClientBuilder builder = (RSDynamicClientBuilder) SpiLoader.loadService(
                "org.jboss.wise.core.client.builder.RSDynamicClientBuilder", null);
        if (builder == null) {
            throw new WiseRuntimeException("No RSDynamicClientBuilder implementation found!");
        }
        return builder.resourceURI(endpointURL).httpMethod(httpMethod).produceMediaTypes(produceMediaTypes)
                .consumeMediaTypes(consumeMediaTypes).build();
    }

    /**
     * Return an instance of RSDynamicClient taken from cache if possible, generate and initialise if not.
     *
     * @param endpointURL string
     * @param produceMediaTypes string
     * @param consumeMediaTypes string
     * @param httpMethod http method
     * @return an instance of {@link RSDynamicClient} already initialized, ready to be called
     */
    public static RSDynamicClient getJAXRSClient(String endpointURL, RSDynamicClient.HttpMethod httpMethod,
            String produceMediaTypes, String consumeMediaTypes) {
        return getJAXRSClient(endpointURL, httpMethod, produceMediaTypes, consumeMediaTypes, null, null);
    }

}
