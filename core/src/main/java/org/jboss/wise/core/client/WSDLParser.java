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
package org.jboss.wise.core.client;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.jboss.wsf.spi.util.StAXUtils.attributeAsQName;
import static org.jboss.wsf.spi.util.StAXUtils.match;
import static org.jboss.wsf.spi.util.StAXUtils.nextElement;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.wsf.spi.util.StAXUtils;


/**
 * WSDL parsing utilities
 * 
 * @author alessio.soldano@jboss.com
 * @since 04-Jul-2013
 * 
 */
public class WSDLParser {
    
    private static final String WSDL_NS = "http://schemas.xmlsoap.org/wsdl/";
    private static final String SOAP_NS = "http://schemas.xmlsoap.org/wsdl/soap/";
    private static final String SOAP12_NS = "http://schemas.xmlsoap.org/wsdl/soap12/";
    private static final String DEFINITIONS = "definitions";
    private static final String SERVICE = "service";
    private static final String PORT = "port";
    private static final String ADDRESS = "address";
    private static final String NAME = "name";
    private static final String TARGET_NAMESPACE = "targetNamespace";

    public static Set<String> searchNonSoapServices(String wsdlUrl) throws WiseRuntimeException {
	URL url;
	try {
	    url = new URL(wsdlUrl);
	} catch (MalformedURLException e) {
	    File file = new File(wsdlUrl);
	    try {
		url = file.toURI().toURL();
	    } catch (MalformedURLException mue) {
		throw new WiseRuntimeException(mue);
	    }
	}
	return searchNonSoapServices(url);
    }
    
    public static Set<String> searchNonSoapServices(URL wsdlUrl) throws WiseRuntimeException {
	Set<String> excludedPorts = new HashSet<String>();
	InputStream is = null;
	try {
	    is = wsdlUrl.openStream();
	    XMLStreamReader xmlr = StAXUtils.createXMLStreamReader(is);
	    parse(xmlr, wsdlUrl, excludedPorts);
	    return excludedPorts;
	} catch (Exception e) {
	    throw new WiseRuntimeException("Failed to read: " + wsdlUrl, e);
	} finally {
	    try {
		if (is != null)
		    is.close();
	    } catch (IOException e) {
	    } // ignore
	}
    }
    
    private static void parse(XMLStreamReader reader, URL wsdlUrl, Set<String> excludedPorts) throws XMLStreamException, WiseRuntimeException
    {
       int iterate;
       try
       {
          iterate = reader.nextTag();
       }
       catch (XMLStreamException e)
       {
          // skip non-tag elements
          iterate = reader.nextTag();
       }
       switch (iterate)
       {
          case END_ELEMENT : {
             // we're done
             break;
          }
          case START_ELEMENT : {

             if (match(reader, WSDL_NS, DEFINITIONS))
             {
                String targetNS = reader.getAttributeValue(null, TARGET_NAMESPACE);
                parseDefinitions(reader, targetNS, wsdlUrl, excludedPorts);
             }
             else
             {
                throw new WiseRuntimeException("Unexpected element '" + reader.getLocalName() + "' found parsing " + wsdlUrl.toExternalForm());
             }
          }
       }
    }
    
    private static void parseDefinitions(XMLStreamReader reader, String targetNS, URL wsdlUrl, Set<String> excludedPorts) throws XMLStreamException, WiseRuntimeException
    {
       while (reader.hasNext())
       {
          switch (nextElement(reader))
          {
             case XMLStreamConstants.END_ELEMENT : {
                if (match(reader, WSDL_NS, DEFINITIONS))
                {
                   return;
                }
                continue;
             }
             case XMLStreamConstants.START_ELEMENT : {
                if (match(reader, WSDL_NS, SERVICE)) {
                   parseService(reader, targetNS, wsdlUrl, excludedPorts);
                }
                continue;
             }
          }
       }
       throw new WiseRuntimeException("Reached end of XML document unexpectedly: " + wsdlUrl.toExternalForm());
    }
    
    private static void parseService(XMLStreamReader reader, String targetNS, URL wsdlUrl, Set<String> excludedPorts) throws XMLStreamException
    {
       while (reader.hasNext())
       {
          switch (nextElement(reader))
          {
             case XMLStreamConstants.END_ELEMENT : {
                if (match(reader, WSDL_NS, SERVICE))
                {
                   return;
                }
                continue;
             }
             case XMLStreamConstants.START_ELEMENT : {
                if (match(reader, WSDL_NS, PORT)) {
                   QName name = attributeAsQName(reader, null, NAME, targetNS);
                   if(!isSoapPort(reader, wsdlUrl)) {
                       excludedPorts.add(name.getLocalPart());
                   }
                }
                continue;
             }
          }
       }
       throw new WiseRuntimeException("Reached end of XML document unexpectedly: " + wsdlUrl.toExternalForm());
    }
    
    private static boolean isSoapPort(XMLStreamReader reader, URL wsdlUrl) throws XMLStreamException
    {
       while (reader.hasNext())
       {
          switch (nextElement(reader))
          {
             case XMLStreamConstants.END_ELEMENT : {
                if (match(reader, WSDL_NS, PORT))
                {
                   return false;
                }
                continue;
             }
             case XMLStreamConstants.START_ELEMENT : {
                if (match(reader, SOAP_NS, ADDRESS) || match(reader, SOAP12_NS, ADDRESS)) {
                   return true;
                }
                continue;
             }
          }
       }
       throw new WiseRuntimeException("Reached end of XML document unexpectedly: " + wsdlUrl.toExternalForm());
    }

}
