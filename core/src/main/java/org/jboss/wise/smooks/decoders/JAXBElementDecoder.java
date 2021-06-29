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
package org.jboss.wise.smooks.decoders;

import java.util.Properties;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.smooks.api.SmooksConfigException;
import org.smooks.api.converter.TypeConverter;
import org.smooks.api.converter.TypeConverterException;
import org.smooks.api.resource.config.Configurable;

/**
 * {@link javax.xml.datatype.Duration} data decoder.
 *
 * Decodes the supplied string into a {@link javax.xml.bind.JAXBElement} It requires 2 parameter to build QName for JAXBElement.
 * IOW what it does under the wood is: new JAXBElement&lt;String&gt;(new QName(nameSpaceURI, localPart), String.class, String
 * data);
 */

public class JAXBElementDecoder implements TypeConverter <String, JAXBElement>, Configurable {
    String nameSpaceURI = null;

    String localPart = null;

    Properties properties;
    
    public void setConfiguration(Properties properties) throws SmooksConfigException {
        this.properties = properties;
        nameSpaceURI = properties.getProperty("namespaceURI");
        localPart = properties.getProperty("localPart");
        if (nameSpaceURI == null || localPart == null) {
            throw new SmooksConfigException("Decoder must specify a  QName namespaceURI and a localPart parameter.");
        }
    }

    public Properties getConfiguration() {
        return properties;
    }

    public JAXBElement convert(String data) throws TypeConverterException {
        return new JAXBElement<String>(new QName(nameSpaceURI, localPart), String.class, data);
    }
}
