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

package org.jboss.wise.core.client.builder;

import java.net.ConnectException;

import net.jcip.annotations.ThreadSafe;

import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.jaxrs.RSDynamicClient;
import org.jboss.wise.core.client.jaxrs.RSDynamicClient.HttpMethod;
import org.jboss.wise.core.exception.WiseRuntimeException;

/**
 * 
 * 
 * @author alessio.soldano@jboss.com
 */
@ThreadSafe
public interface RSDynamicClientBuilder {

    /**
     * Build the {@link WSDynamicClient} with all parameters set on this class
     * 
     * @return {@link WSDynamicClient}
     * @throws IllegalStateException
     * @throws ConnectException
     * @throws WiseRuntimeException
     */
    public RSDynamicClient build() throws IllegalStateException, WiseRuntimeException;

    public RSDynamicClientBuilder resourceURI(String resourceURI);
    
    public RSDynamicClientBuilder httpMethod(HttpMethod httpMethod);
    
    public RSDynamicClientBuilder produceMediaTypes(String produceMediaTypes);
    
    public RSDynamicClientBuilder consumeMediaTypes(String consumeMediaTypes);

    public String getResourceURI();
    
    public String getProduceMediaTypes();
    
    public String getConsumeMediaTypes();
    
    public HttpMethod getHttpMethod();

}
