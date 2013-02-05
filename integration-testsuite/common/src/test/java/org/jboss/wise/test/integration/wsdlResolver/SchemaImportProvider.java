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
package org.jboss.wise.test.integration.wsdlResolver;

import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

/**
 * A test provider with the only purpose
 * of exposing a given wsdl.
 *
 * @author alessio.soldano@jboss.com
 * @since 13-May-2009
 */
@WebServiceProvider(portName="AddNumbersPort", serviceName="AddNumbersService", targetNamespace="http://duke.example.org", wsdlLocation = "WEB-INF/wsdl/SchemaImport/WsdlWithSchemaImport.wsdl")
@ServiceMode(value = Service.Mode.MESSAGE)
public class SchemaImportProvider implements Provider<SOAPMessage>
{
   public SOAPMessage invoke(SOAPMessage request)
   {
      return request;
   }
}
