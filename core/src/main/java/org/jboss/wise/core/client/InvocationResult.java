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

import java.util.Map;
import net.jcip.annotations.Immutable;
import org.jboss.wise.core.exception.MappingException;
import org.jboss.wise.core.mapper.WiseMapper;

/**
 * Holds the webservice's (JAXWS or JAXRS) invocation result's data. Normally this data are kept in Map&lt;String, Object&gt;
 * for JAXWS and a Stream or String for JAXR, but implementor are free to change internal data structure. Anyway it return a
 * Map&lt;String, Object&gt; with webservice's call results, eventually applying a mapping to custom object using a WiseMapper
 * passed to {@link #getMapRequestAndResult(WiseMapper, Map)} methods
 *
 * @author stefano.maestri@javalinux.it
 * @since 29-07-2007
 */
@Immutable
public interface InvocationResult {
    String RESPONSE = "RESPONSE";

    String STATUS = "STATUS";

    String HEADERS = "HEADERS";

    /**
     * Apply WiseMapper provided to returned Object as defined in wsdl/wiseconsume generated objects. If mapper parameter is
     * null, no mapping are applied and original object are returned. When no mapping applied you will have this answer: For
     * JAXWS the original object answer are returned into this map; For JAXRS a Map contains 2 key/valuepair with keys
     * "ContentType" and "JAXRSStream"
     *
     * @param mapper a WiseMapper used to map JAX-WS generated object returned by method call to arbitrary custom object model.
     *        It could be null to don't apply any kind of mappings
     * @param inputMap It's the map of input object used to give them together with output. It's useful when they are needed by
     *        wise's client in same classLoader used by smooks (i.e when wise is used to enrich set of objects like in ESB
     *        action pipeline)
     * @return a Map&lt;String, Object&gt; containing the result of ws calls eventually mapped using WiseMapper provided
     * @throws MappingException rethrown exception got from provided {@link WiseMapper}
     */

    Map<String, Object> getMapRequestAndResult(WiseMapper mapper, Map<String, Object> inputMap) throws MappingException;

    /**
     * Apply WiseMapper provided with to returned Object as defined in wsdl/wiseconsume generated objects. If mapper parameter
     * is null, no mapping are applied and original object are returned. When no mapping applied you will have this answer: For
     * JAXWS the original object answer are returned into this map; For JAXRS a Map contains 2 key/valuepair with keys
     * "ContentType" and "JAXRSStream"
     *
     * @param mapper a WiseMapper used to map JAX-WS generated object returned by method call to arbitrary custom object model.
     *        It could be null to don't apply any kind of mappings
     * @return a Map&lt;String, Object&gt; containing the result of ws calls eventually mapped using WiseMapper provided
     * @throws MappingException rethrown exception got from provided {@link WiseMapper}
     */
    Map<String, Object> getMappedResult(WiseMapper mapper) throws MappingException;

    /**
     * Return a Map containing objects returned by web service invocation. Both return value and OUT Holders parameters are
     * considered ; For JAXRS a Map contains 2 key/valuepair with keys "ContentType" and "JAXRSStream"
     *
     * @return a Map&lt;String, Object&gt; containing the result of ws calls eventually mapped using WiseMapper provided
     */
    Map<String, Object> getResult();

}
