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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import org.milyn.javabean.DataDecodeException;
import org.milyn.javabean.DataDecoder;
import org.milyn.javabean.DecodeType;
import org.milyn.javabean.decoders.DateDecoder;

/**
 * {@link javax.xml.datatype.Duration} data decoder.
 * <p/>
 * Decodes the supplied string into a {@link javax.xml.datatype.Duration} String value is supposed to be milliseconds representing
 * this Duration
 */
@DecodeType( Duration.class )
public class DurationDecoder extends DateDecoder implements DataDecoder {

    @Override
    public Object decode( String data ) throws DataDecodeException {
        Object result = null;
        try {
            result = DatatypeFactory.newInstance().newDuration(Long.parseLong(data));
        } catch (DatatypeConfigurationException e) {
            throw new DataDecodeException("Error decoding Duration data value '" + data, e);
        } catch (NumberFormatException e) {
            throw new DataDecodeException("Error decoding Duration data value '" + data, e);
        }

        return result;
    }

}
