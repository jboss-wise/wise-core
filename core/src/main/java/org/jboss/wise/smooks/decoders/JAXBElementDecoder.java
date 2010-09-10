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
package org.jboss.wise.smooks.decoders;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;
import org.milyn.javabean.DecodeType;

/**
 * {@link javax.xml.datatype.Duration} data decoder.
 * <p/>
 * Decodes the supplied string into a {@link javax.xml.bind.JAXBElement} It requires 2 parameter to build QName for JAXBElement.
 * IOW what it does under the wood is: new JAXBElement<String>(new QName(nameSpaceURI, localPart), String.class, data);
 */
@DecodeType( JAXBElement.class )
public class JAXBElementDecoder implements DataDecoder {
    String nameSpaceURI = null;
    String localPart = null;

    public void setConfiguration( SmooksResourceConfiguration resourceConfig ) throws SmooksConfigurationException {
        nameSpaceURI = resourceConfig.getStringParameter("namespaceURI");
        localPart = resourceConfig.getStringParameter("localPart");
        if (nameSpaceURI == null || localPart == null) {
            throw new SmooksConfigurationException("Decoder must specify a  QName namespaceURI and a localPart parameter.");
        }
    }

    public Object decode( String data ) throws DataDecodeException {
        return new JAXBElement<String>(new QName(nameSpaceURI, localPart), String.class, data);
    }
}
