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

import static org.hamcrest.core.IsNot.not;

import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.mapper.mappingObject.ExternalObject;
import org.jboss.wise.core.mapper.mappingObject.InternalObject;
import org.junit.Test;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;

/**
 * @author stefano.maestri@javalinux.it
 */
public class SmooksMapperTest {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    @Test
    public void shouldMapComplexObjectModel() throws Exception {
	WSDynamicClient client = mock(WSDynamicClient.class);
	when(client.getSmooksInstance()).thenReturn(new Smooks());
	when(client.getClassLoader())
		.thenReturn(new URLClassLoader(new URL[] {}, Thread.currentThread().getContextClassLoader()));
	WiseMapper mapper = new SmooksMapper("./smooks/smooks-config.xml", "target/smooks-report/report.html", client);
	Map<String, Object> originalObjects = new HashMap<String, Object>();
	ExternalObject external = new ExternalObject();
	InternalObject internal = new InternalObject();
	internal.setNumber(Integer.valueOf(1));
	internal.setText("fooText");
	external.setInternal(internal);
	originalObjects.put("external", external);
	Map<String, Object> results;
	results = mapper.applyMapping(originalObjects);
	Integer integerResult = (Integer) results.get("complexObject").getClass().getMethod("getNumberField")
		.invoke(results.get("complexObject"));
	String stringResult = (String) results.get("complexObject").getClass().getMethod("getTextField").invoke(results
		.get("complexObject"));
	assertThat(integerResult, equalTo(internal.getNumber()));
	assertThat(stringResult, equalTo("fooText"));

    }

    @Test
    public void shouldMapObjectContainingXMLGregorianCalendarField() throws Exception {
	WSDynamicClient client = mock(WSDynamicClient.class);
	when(client.getSmooksInstance()).thenReturn(new Smooks());
	when(client.getClassLoader())
		.thenReturn(new URLClassLoader(new URL[] {}, Thread.currentThread().getContextClassLoader()));
	WiseMapper mapper = new SmooksMapper("./smooks/smooks-config-XMLGregorianCalendar.xml", client);
	Map<String, Object> originalObjects = new HashMap<String, Object>();
	ExternalObject external = new ExternalObject();
	String dateString = "2007-03-07T04:27:00";
	Date date = (new SimpleDateFormat(DEFAULT_DATE_FORMAT)).parse(dateString);
	external.setDate(date);
	originalObjects.put("external", external);
	Map<String, Object> results;
	results = mapper.applyMapping(originalObjects);
	long returnedTime = ((XMLGregorianCalendar) results.get("complexObject").getClass().getMethod("getDateField")
		.invoke(results.get("complexObject"))).toGregorianCalendar().getTimeInMillis();

	assertThat(returnedTime, is(date.getTime()));

    }

    @Test
    public void shouldMapToPrimitiveInput() throws Exception {
	WSDynamicClient client = mock(WSDynamicClient.class);
	when(client.getSmooksInstance()).thenReturn(new Smooks());
	when(client.getClassLoader())
		.thenReturn(new URLClassLoader(new URL[] {}, Thread.currentThread().getContextClassLoader()));
	WiseMapper mapper = new SmooksMapper("./smooks/smooks-primitive-input.xml", client);
	Map<String, Object> originalObjects = new HashMap<String, Object>();
	InternalObject internal = new InternalObject();
	internal.setNumber(Integer.valueOf(1));
	internal.setText("fooText");
	originalObjects.put("internal", internal);
	Map<String, Object> results;
	results = mapper.applyMapping(originalObjects);
	assertThat(results, hasEntry("textInput", (Object) "fooText"));
	assertThat(results, hasEntry("intInput", (Object) Integer.valueOf(1)));

    }

    @Test
    public void shouldHaveDifferentContentDeliveryConfigurationPerxecutionContext() {
	WSDynamicClient client = mock(WSDynamicClient.class);
	when(client.getSmooksInstance()).thenReturn(new Smooks());
	when(client.getClassLoader())
		.thenReturn(new URLClassLoader(new URL[] {}, Thread.currentThread().getContextClassLoader()));
	SmooksMapper mapper = new SmooksMapper("./smooks/smooks-primitive-input.xml", client);
	SmooksMapper mapper2 = new SmooksMapper("./smooks/smooks-config-XMLGregorianCalendar.xml", client);
	SmooksMapper mapper3 = new SmooksMapper("./smooks/smooks-config.xml", client);
	ExecutionContext context = mapper.initExecutionContext(null);
	ExecutionContext context2 = mapper2.initExecutionContext(null);
	ExecutionContext context3 = mapper3.initExecutionContext(null);

	assertThat(context.getDeliveryConfig(), not(is(context2.getDeliveryConfig())));
	assertThat(context.getDeliveryConfig(), not(is(context3.getDeliveryConfig())));
	assertThat(context2.getDeliveryConfig(), not(is(context3.getDeliveryConfig())));

    }
}
