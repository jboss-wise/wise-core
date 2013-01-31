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

package org.jboss.wise.core.client.impl.reflection;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.Map;
import org.jboss.wise.core.mapper.WiseMapper;
import org.junit.Test;

/**
 * @author stefano.maestri@javalinux.it
 */
public class InvocationResultImplTest {

    private InvocationResultImpl results = null;

    @Test
    public void shoudReturnAnOriginaObjectsEmptyMapIfNameIsNull() throws Exception {
        results = new InvocationResultImpl(null, Long.class, new Long(1), null);
        Map<String, Object> mappedResult = results.getMapRequestAndResult(null, null);
        assertThat(((Map<?,?>)mappedResult.get("results")).isEmpty(), is(true));
    }

    @Test
    public void shoudReturnAnOriginaObjectsEmptyMapIfNameIsEmptyString() throws Exception {
        results = new InvocationResultImpl(" ", Long.class, new Long(1), null);
        Map<String, Object> mappedResult = results.getMapRequestAndResult(null, null);
        assertThat(((Map<?,?>)mappedResult.get("results")).isEmpty(), is(true));
    }

    @Test
    public void shouldReturnOriginalObjectIfMapperIsNull() throws Exception {
        results = new InvocationResultImpl("result", Long.class, new Long(1), null);
        Map<String, Object> mappedResult = results.getMapRequestAndResult(null, null);
        assertThat((Long)((Map<?,?>)mappedResult.get("results")).get("result"), equalTo(new Long(1)));

    }

    @Test
    public void shouldApplyMapping() throws Exception {
        results = new InvocationResultImpl("result", Long.class, new Long(1), null);
        WiseMapper mapper = mock(WiseMapper.class);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("result", new Long(2));
        when(mapper.applyMapping(anyObject())).thenReturn(map);
        Map<String, Object> mappedResult = results.getMapRequestAndResult(mapper, null);
        assertThat((Long)((Map<?,?>)mappedResult).get("result"), equalTo(new Long(2)));

    }

    @Test
    public void shouldReturnInputObjectAndOriginalObjectIfMapperIsNull() throws Exception {
        results = new InvocationResultImpl("result", Long.class, new Long(1), null);
        Map<String, Object> inputMap = new HashMap<String, Object>();
        inputMap.put("origKey", "origValue");
        Map<String, Object> mappedResult = results.getMapRequestAndResult(null, inputMap);
        assertThat((Long)((Map<?,?>)mappedResult.get("results")).get("result"), equalTo(new Long(1)));
        assertThat((String)mappedResult.get("origKey"), equalTo("origValue"));

    }

    @Test
    public void shouldApplyMappingAndReturnIputMap() throws Exception {
        results = new InvocationResultImpl("result", Long.class, new Long(1), null);
        WiseMapper mapper = mock(WiseMapper.class);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("result", new Long(2));
        when(mapper.applyMapping(anyObject())).thenReturn(map);
        Map<String, Object> inputMap = new HashMap<String, Object>();
        inputMap.put("origKey", "origValue");
        Map<String, Object> mappedResult = results.getMapRequestAndResult(null, inputMap);
        assertThat((Long)((Map<?,?>)mappedResult.get("results")).get("result"), equalTo(new Long(1)));
        assertThat((String)mappedResult.get("origKey"), equalTo("origValue"));

    }

}
