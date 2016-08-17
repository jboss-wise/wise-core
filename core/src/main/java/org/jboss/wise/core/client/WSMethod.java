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

import java.io.OutputStream;
import java.util.Map;
import net.jcip.annotations.ThreadSafe;
import org.jboss.wise.core.exception.InvocationException;
import org.jboss.wise.core.exception.MappingException;
import org.jboss.wise.core.exception.WiseWebServiceException;
import org.jboss.wise.core.mapper.WiseMapper;

/**
 * It Represents a webservice operation(action) invocation and it always refer to a specific endpoint.
 *
 * @author stefano.maestri@javalinux.it
 * @since 23-Aug-2007
 */
@ThreadSafe
public interface WSMethod {

    String RESULT = "result";

    String RESULTS = "results";

    String TYPE_PREFIX = "type.";

    String TYPE_RESULT = TYPE_PREFIX + RESULT;

    /**
     * Invokes this method with the provided arguments applying provided mapper
     *
     * @param args the arguments to call operation. It could be a generic Object to be passed to provided mapper. If mapper is
     *        null it works exactly like {@link #invoke(Object)}
     * @param mapper if null no mappings are applied method will be invoked using args directly. in this case the keys of the
     *        map gotta be the parameters names as defined in wsdl/wsconsume generated classes
     * @return return an {@link InvocationResult} object populated with returned values (implementation will process both
     *         directed returned values and OUT parameters as defined in wsdl)
     * @throws WiseWebServiceException issue calling the service
     * @throws InvocationException issue invoking service
     * @throws IllegalArgumentException illegal argument
     * @throws MappingException mapping issue
     */
     InvocationResult invoke(Object args, WiseMapper mapper) throws WiseWebServiceException, InvocationException,
            IllegalArgumentException, MappingException;

    /**
     * Invokes this method with the provided arguments
     *
     * @param args the arguments to call operation. args must be a Map&lt;String, Object&gt;. This Map have to contain entries
     *        for all needed parameters, keys have to reflect operation parameter name as defined in wsdl. Keys which names are
     *        not defined in wsdls will be simply ignored. Implementation will take care values nullability will reflect
     *        "nillable" properties defined in wsdl. order isn't important since WSMethod implementation will take care of
     *        reorder parameters in right position to make operation call. If it isn't a Map&lt;String, Object&gt; or keys don't
     *        contain all parameters name an {@link IllegalArgumentException} is thrown.
     * @return return an {@link InvocationResult} object populated with returned values (implementation will process both
     *         directed returned values and OUT parameters as defined in wsdl)
     * @throws WiseWebServiceException can indicate login credentials needed
     * @throws InvocationException issue invoking service
     * @throws IllegalArgumentException illegal argument
     * @throws MappingException mapping issue
     */
     InvocationResult invoke(Object args) throws WiseWebServiceException, InvocationException, IllegalArgumentException,
            MappingException;

    /**
     * Generates and writes a preview of the request message for invoking this method with the provided arguments.
     *
     * @param args map
     * @param os output stream
     * @throws InvocationException issue invoking service
     */
     void writeRequestPreview(Map<String, Object> args, OutputStream os) throws InvocationException;

    /**
     * Gets the map of {@link WebParameter} for the webserice method represented by instance of this type
     *
     * @return a Map&lt;String, Object&gt; representing valid webparameters where keys contain symbolic names as defined by
     *         wsdl. It may be null in case of selected operation haven't parameter.
     */
     Map<String, ? extends WebParameter> getWebParams();

    /**
     * @return true if operation is defined as OneWay in wsdl
     */
     boolean isOneWay();

    /**
     * @return the endpoint on which this method is attached.
     */
     WSEndpoint getEndpoint();
}
