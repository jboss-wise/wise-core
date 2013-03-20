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
package org.jboss.wise.core.client.impl.reflection;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;
import org.jboss.wise.core.client.InvocationResult;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.exception.MappingException;
import org.jboss.wise.core.mapper.WiseMapper;

/**
 * Holds the webservice's invocation result's data. Can apply a mapping to
 * custom object using a WiseMapper passed to
 * {@link #getMapRequestAndResult(WiseMapper, Map)} methods
 * 
 * @author stefano.maestri@javalinux.it
 */
@Immutable
@ThreadSafe
public class InvocationResultImpl implements InvocationResult {

    private final Map<String, Object> originalObjects;

    /**
     * @param name
     * @param value
     * @param results
     */
    public InvocationResultImpl(String name, Type resultType, Object value, Map<String, Object> results) {

	this.originalObjects = new HashMap<String, Object>();
	if (results == null) {
	    results = Collections.emptyMap();
	}
	this.originalObjects.putAll(results);
	if (name != null && name.trim().length() != 0) {
	    this.originalObjects.put(name, value);
	    if (resultType != null) {
		this.originalObjects.put(WSMethod.TYPE_PREFIX + WSMethod.RESULT, resultType);
	    }
	}
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.InvocationResult#getMapRequestAndResult(WiseMapper, Map)
     */
    public Map<String, Object> getMapRequestAndResult(WiseMapper mapper, Map<String, Object> inputMap) throws MappingException {

	if (inputMap == null) {
	    inputMap = new HashMap<String, Object>();
	}
	inputMap.put(WSMethod.RESULTS, originalObjects);
	Map<String, Object> mappedResult = new HashMap<String, Object>();
	if (mapper != null) {
	    mappedResult.putAll(mapper.applyMapping(inputMap));
	} else {
	    mappedResult.putAll(inputMap);
	}
	return mappedResult;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.InvocationResult#getMappedResult(org.jboss.wise.core.mapper.WiseMapper)
     */
    public Map<String, Object> getMappedResult(WiseMapper mapper) throws MappingException {
	return this.getMapRequestAndResult(mapper, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.InvocationResult#getResult()
     */
    public Map<String, Object> getResult() {
	return originalObjects;
    }

}
