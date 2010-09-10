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

import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import javax.jws.Oneway;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import org.jboss.wise.core.client.InvocationResult;
import org.jboss.wise.core.client.WSEndpoint;
import org.jboss.wise.core.mapper.WiseMapper;
import org.junit.Before;
import org.junit.Test;

/**
 * @author stefano.maestri@javalinux.it
 */
public class WSMethodImplTest {

    private boolean methodWorked = false;

    @Before
    public void before() {
	this.methodWorked = false;
    }

    public String methodForAnnotation(@WebParam(name = "annotation1", mode = Mode.IN) Integer annotation1, @WebParam(name = "annotation2", mode = Mode.OUT) String annotation2, @WebParam(name = "annotation3", mode = Mode.INOUT) String annotation3) {
	this.methodWorked = true;
	return "great";
    }

    @Oneway
    public void methodOneWay() {
	this.methodWorked = true;
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotPermitMethodNull() throws Exception {
	WSEndpoint endPointMock = mock(WSEndpointImpl.class);
	new WSMethodImpl(null, endPointMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotPermitEndPointNull() throws Exception {
	// using Method super class to avoid mockito problem in mocking final
	// classes
	Method methodMock = this.getClass().getMethod("shouldNotPermitEndPointNull");
	new WSMethodImpl(methodMock, null);
    }

    @Test
    public void shouldInitWebParams() throws Exception {
	// mockito can't mock Method class since it is final
	// Let use a method defined in this test class for this goal
	Method method = this.getClass()
		.getMethod("methodForAnnotation", new Class[] { Integer.class, String.class, String.class });

	WSEndpoint endPointMock = mock(WSEndpointImpl.class);
	WSMethodImpl wsMethod = new WSMethodImpl(method, endPointMock);
	Map<String, WebParameterImpl> params = wsMethod.getWebParams();

	assertThat(params.size(), is(3));
	assertThat(params.get("annotation1").equals(new WebParameterImpl(Integer.class, "annotation1", 0, Mode.IN)), is(true));
	assertThat(params.get("annotation2").equals(new WebParameterImpl(String.class, "annotation2", 1, Mode.OUT)), is(true));

    }

    @Test
    public void getParmeterInRightPositionArrayShouldOrderParameters() throws Exception {
	// mockito can't mock Method class since it is final
	// Let use a method defined in this test class for this goal
	Method method = this.getClass()
		.getMethod("methodForAnnotation", new Class[] { Integer.class, String.class, String.class });
	WSEndpoint endPointMock = mock(WSEndpointImpl.class);
	WSMethodImpl wsMethod = new WSMethodImpl(method, endPointMock);
	Map<String, Object> inputMap = new HashMap<String, Object>();
	inputMap.put("annotation2", "foo");
	inputMap.put("annotation1", Integer.valueOf(3));
	Object[] array = wsMethod.getParmeterInRightPositionArray(inputMap);

	assertThat(array.length, is(3));
	assertThat(array[0].equals(Integer.valueOf(3)), is(true));
	assertThat(array[1].equals("foo"), is(true));

    }

    @Test
    public void getParmeterInRightPositionArrayShouldIgnoreUnknownParametersAndLeaveNullWhenNotProvided() throws Exception {
	// mockito can't mock Method class since it is final
	// Let use a method defined in this test class for this goal
	Method method = this.getClass()
		.getMethod("methodForAnnotation", new Class[] { Integer.class, String.class, String.class });
	WSEndpoint endPointMock = mock(WSEndpointImpl.class);
	WSMethodImpl wsMethod = new WSMethodImpl(method, endPointMock);
	Map<String, Object> inputMap = new HashMap<String, Object>();
	inputMap.put("annotation2", "foo");
	inputMap.put("annotation1", Integer.valueOf(3));
	inputMap.put("unknown", Integer.valueOf(5));
	Object[] array = wsMethod.getParmeterInRightPositionArray(inputMap);

	assertThat(array.length, is(3));
	assertThat(array[0].equals(Integer.valueOf(3)), is(true));
	assertThat(array[1].equals("foo"), is(true));
	assertThat(array[2], nullValue());

    }

    @Test
    public void getParmeterInRightPositionArrayShouldIgnoreUnknownParameters() throws Exception {
	// mockito can't mock Method class since it is final
	// Let use a method defined in this test class for this goal
	Method method = this.getClass()
		.getMethod("methodForAnnotation", new Class[] { Integer.class, String.class, String.class });
	WSEndpoint endPointMock = mock(WSEndpointImpl.class);
	WSMethodImpl wsMethod = new WSMethodImpl(method, endPointMock);
	Map<String, Object> inputMap = new HashMap<String, Object>();
	inputMap.put("annotation2", "foo");
	inputMap.put("annotation1", Integer.valueOf(3));
	inputMap.put("annotation3", Integer.valueOf(5));
	inputMap.put("myAnnotation", Integer.valueOf(6));
	inputMap.put("unknown2", Integer.valueOf(7));

	Object[] array = wsMethod.getParmeterInRightPositionArray(inputMap);

	assertThat(array.length, is(3));
	assertThat(array[0].equals(Integer.valueOf(3)), is(true));
	assertThat(array[1].equals("foo"), is(true));
	assertThat(array[2].equals(Integer.valueOf(5)), is(true));

    }

    @Test
    public void getHoldersResultShouldReturnHolderForRightParameters() throws Exception {
	Method method = this.getClass()
		.getMethod("methodForAnnotation", new Class[] { Integer.class, String.class, String.class });
	WSEndpoint endPointMock = mock(WSEndpointImpl.class);
	Map<String, Object> inputMap = new HashMap<String, Object>();
	inputMap.put("annotation2", "foo2");
	inputMap.put("annotation3", "foo3");
	inputMap.put("annotation1", Integer.valueOf(3));
	WSMethodImpl wsMethod = new WSMethodImpl(method, endPointMock);
	Map<String, Object> results = wsMethod.getHoldersResult(inputMap);
	assertThat(results, hasEntry("annotation2", (Object) "foo2"));
	assertThat(results, hasEntry("annotation3", (Object) "foo3"));
    }

    @Test
    public void getHoldersResultShouldIgnoreUnknowntParameters() throws Exception {
	Method method = this.getClass()
		.getMethod("methodForAnnotation", new Class[] { Integer.class, String.class, String.class });
	WSEndpoint endPointMock = mock(WSEndpointImpl.class);
	Map<String, Object> inputMap = new HashMap<String, Object>();
	inputMap.put("annotation2", "foo2");
	inputMap.put("annotation3", "foo3");
	inputMap.put("unknown", Integer.valueOf(3));

	WSMethodImpl wsMethod = new WSMethodImpl(method, endPointMock);
	Map<String, Object> results = wsMethod.getHoldersResult(inputMap);
	assertThat(results.size(), is(2));
	assertThat(results, hasEntry("annotation2", (Object) "foo2"));
	assertThat(results, hasEntry("annotation3", (Object) "foo3"));
    }

    @Test
    public void getHoldersResultShouldIgnoreINParameters() throws Exception {
	Method method = this.getClass()
		.getMethod("methodForAnnotation", new Class[] { Integer.class, String.class, String.class });
	WSEndpoint endPointMock = mock(WSEndpointImpl.class);
	Map<String, Object> inputMap = new HashMap<String, Object>();
	inputMap.put("annotation2", "foo2");
	inputMap.put("annotation3", "foo3");
	inputMap.put("annotaion1", Integer.valueOf(3));

	WSMethodImpl wsMethod = new WSMethodImpl(method, endPointMock);
	Map<String, Object> results = wsMethod.getHoldersResult(inputMap);
	assertThat(results.size(), is(2));
	assertThat(results, hasEntry("annotation2", (Object) "foo2"));
	assertThat(results, hasEntry("annotation3", (Object) "foo3"));
    }

    @Test
    public void shouldRuninvokeForOneWayMethod() throws Exception {
	Method method = this.getClass().getMethod("methodOneWay", new Class[] {});
	WSEndpointImpl endPointMock = mock(WSEndpointImpl.class);
	when(endPointMock.getService()).thenReturn(Executors.newFixedThreadPool(10));
	when(endPointMock.createInstance()).thenReturn(this);
	WSMethodImpl wsMethod = new WSMethodImpl(method, endPointMock);
	InvocationResult invocationResults = wsMethod.invoke(Collections.EMPTY_MAP);
	Map<String, Object> results = invocationResults.getMapRequestAndResult(null, null);
	assertThat(this.methodWorked, is(true));
	assertThat(results.size(), is(1));
	assertThat(((Map) results.get("results")).isEmpty(), is(true));

    }

    @Test
    public void shouldRuninvokeForMethods() throws Exception {
	Method method = this.getClass()
		.getMethod("methodForAnnotation", new Class[] { Integer.class, String.class, String.class });
	WSEndpointImpl endPointMock = mock(WSEndpointImpl.class);
	when(endPointMock.getService()).thenReturn(Executors.newFixedThreadPool(10));
	when(endPointMock.createInstance()).thenReturn(this);
	WSMethodImpl wsMethod = new WSMethodImpl(method, endPointMock);
	Map<String, Object> inputMap = new HashMap<String, Object>();
	inputMap.put("annotation2", "foo2");
	inputMap.put("annotation3", "foo3");
	inputMap.put("annotaion1", Integer.valueOf(3));
	InvocationResult invocationResults = wsMethod.invoke(inputMap);
	assertThat(this.methodWorked, is(true));
	Map<String, Object> results = (Map) invocationResults.getMapRequestAndResult(null, null).get("results");
	assertThat(results.size(), is(3));
	assertThat(results, hasEntry("result", (Object) "great"));
	assertThat(results, hasEntry("annotation2", (Object) "foo2"));
	assertThat(results, hasEntry("annotation3", (Object) "foo3"));

    }

    @Test
    public void shouldRuninvokeForMethodsApplyingMapping() throws Exception {
	Method method = this.getClass()
		.getMethod("methodForAnnotation", new Class[] { Integer.class, String.class, String.class });
	WSEndpointImpl endPointMock = mock(WSEndpointImpl.class);
	when(endPointMock.getService()).thenReturn(Executors.newFixedThreadPool(10));
	when(endPointMock.createInstance()).thenReturn(this);
	WSMethodImpl wsMethod = new WSMethodImpl(method, endPointMock);
	Map<String, Object> inputMap = new HashMap<String, Object>();
	inputMap.put("annotation2", "foo2");
	inputMap.put("annotation3", "foo3");
	inputMap.put("annotaion1", Integer.valueOf(3));
	WiseMapper mapper = mock(WiseMapper.class);
	when(mapper.applyMapping(anyObject())).thenReturn(inputMap);
	InvocationResult invocationResults = wsMethod.invoke(inputMap, mapper);
	assertThat(this.methodWorked, is(true));
	Map<String, Object> results = (Map) invocationResults.getMapRequestAndResult(null, null).get("results");
	assertThat(results.size(), is(3));
	assertThat(results, hasEntry("result", (Object) "great"));
	assertThat(results, hasEntry("annotation2", (Object) "foo2"));
	assertThat(results, hasEntry("annotation3", (Object) "foo3"));

    }
}
