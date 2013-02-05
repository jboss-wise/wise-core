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
package org.jboss.wise.core.mapper;

import java.util.Map;
import net.jcip.annotations.ThreadSafe;
import org.jboss.wise.core.exception.MappingException;

/**
 * It' a simple interface implemented by any mapper used within wise-core requiring a single method {@link #applyMapping(Object)}
 * 
 * @author stefano.maestri@javalinux.it
 */
@ThreadSafe
public interface WiseMapper {

    /**
     * apply this mapping to original object
     * 
     * @param originalObjects
     * @return the mapped object in a Map<String,Object>. Keys of this map normally represent symbolic name of mapped Object. For
     *         JAXRS conventional key used for standard key/value pairs are "ContentType" and "JAXRSStream"
     * @throws MappingException
     */
    public Map<String, Object> applyMapping( Object originalObjects ) throws MappingException;

}
