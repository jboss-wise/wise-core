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
package org.jboss.wise.core.client.jaxrs.impl;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.jboss.wise.core.client.builder.RSDynamicClientBuilder;
import org.jboss.wise.core.client.jaxrs.RSDynamicClient;
import org.jboss.wise.core.client.jaxrs.RSDynamicClient.HttpMethod;
import org.jboss.wise.core.exception.WiseRuntimeException;


/**
 * 
 * @author alessio.soldano@jboss.com
 * @since 05-Sep-2010
 *
 */
@ThreadSafe
public class CXFRSDynamicClientBuilder implements RSDynamicClientBuilder
{
   
   @GuardedBy("this")
   private String resourceURI;
   @GuardedBy("this")
   private HttpMethod httpMethod;
   @GuardedBy("this")
   private String produceMediaTypes;
   @GuardedBy("this")
   private String consumeMediaTypes;
   
   
   public synchronized RSDynamicClient build() throws IllegalStateException, WiseRuntimeException
   {
      return new RSDynamicClientImpl(resourceURI, produceMediaTypes, consumeMediaTypes, httpMethod);
   }

   public synchronized RSDynamicClientBuilder resourceURI(String resourceURI)
   {
      this.resourceURI = resourceURI;
      return this;
   }

   public synchronized RSDynamicClientBuilder httpMethod(HttpMethod httpMethod)
   {
      this.httpMethod = httpMethod;
      return this;
   }

   public synchronized RSDynamicClientBuilder produceMediaTypes(String produceMediaTypes)
   {
      this.produceMediaTypes = produceMediaTypes;
      return this;
   }

   public synchronized RSDynamicClientBuilder consumeMediaTypes(String consumeMediaTypes)
   {
      this.consumeMediaTypes = consumeMediaTypes;
      return this;
   }

   public synchronized String getResourceURI()
   {
      return this.resourceURI;
   }

   public synchronized String getProduceMediaTypes()
   {
      return this.produceMediaTypes;
   }

   public synchronized String getConsumeMediaTypes()
   {
      return this.consumeMediaTypes;
   }

   public synchronized HttpMethod getHttpMethod()
   {
      return this.httpMethod;
   }

}
