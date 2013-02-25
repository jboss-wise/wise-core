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
package org.jboss.wise.test.integration.complex;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.util.Map;

import javax.jws.WebParam;
import javax.xml.ws.Holder;

import org.jboss.wise.core.client.InvocationResult;
import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.client.WebParameter;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.client.factories.WSDynamicClientFactory;
import org.jboss.wise.core.test.WiseTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author alessio.soldano@jboss.com
 *
 */
public class WiseIntegrationComplexTest extends WiseTest {
    
    private static URL warUrl = null;
    private static WSDynamicClient client;

    @BeforeClass
    public static void setUp() throws Exception {
	warUrl = WiseIntegrationComplexTest.class.getClassLoader().getResource("complex.war");
	deployWS(warUrl);
	
	URL wsdlURL = new URL(getServerHostAndPort() + "/complex/RegistrationService?wsdl");

	WSDynamicClientBuilder clientBuilder = WSDynamicClientFactory.getJAXWSClientBuilder();
	client = clientBuilder.tmpDir("target/temp/wise").verbose(true).keepSource(true).wsdlURL(wsdlURL
		.toString()).build();
    }

    @Test
    public void shouldInvokeRegisterOperation() throws Exception {
	WSMethod method = client.getWSMethod("RegistrationServiceImplService", "RegistrationServiceImplPort", "Register");
	Map<String, ? extends WebParameter> pars = method.getWebParams();
	WebParameter customerPar = pars.get("Customer");
	Class<?> customerClass = (Class<?>)customerPar.getType();
	Object customer = customerClass.newInstance();
	customerClass.getMethod("setId", long.class).invoke(customer, new Long(1234));
	Class<?> nameClass = (Class<?>)customerClass.getDeclaredField("name").getType();
	Object name = nameClass.newInstance();
	nameClass.getMethod("setFirstName", String.class).invoke(name, "Foo");
	nameClass.getMethod("setLastName", String.class).invoke(name, "Bar");
	nameClass.getMethod("setMiddleName", String.class).invoke(name, "The");
	customerClass.getMethod("setName", nameClass).invoke(customer, name);
	
	Map<String, Object> args = new java.util.HashMap<String, Object>();
	args.put("Customer", customer);
	args.put("When", null);
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	method.writeRequestPreview(args, bos);
	Assert.assertTrue(bos.toString().contains("<id>1234</id>"));
	InvocationResult result = method.invoke(args, null);
	Map<String, Object> res = result.getMapRequestAndResult(null, null);
	Map<String, Object> test = (Map<String, Object>) res.get("results");
	Assert.assertEquals(new Long(1234).longValue(), test.get("result"));
	Assert.assertEquals(long.class, test.get("type.result"));
    }

    @Test
    public void shouldInvokeEchoOperation() throws Exception {
	WSMethod method = client.getWSMethod("RegistrationServiceImplService", "RegistrationServiceImplPort", "Echo");
	Map<String, ? extends WebParameter> pars = method.getWebParams();
	WebParameter customerPar = pars.get("Customer");
	Assert.assertEquals(WebParam.Mode.INOUT, customerPar.getMode());
	Class<?> holderClass = (Class<?>)((ParameterizedType)customerPar.getType()).getRawType();
	Assert.assertEquals(Holder.class, holderClass);
	Class<?> customerClass = (Class<?>)((ParameterizedType)customerPar.getType()).getActualTypeArguments()[0];
	Object customer = customerClass.newInstance();
	customerClass.getMethod("setId", long.class).invoke(customer, new Long(1235));
	Class<?> nameClass = (Class<?>)customerClass.getDeclaredField("name").getType();
	Object name = nameClass.newInstance();
	nameClass.getMethod("setFirstName", String.class).invoke(name, "Foo");
	nameClass.getMethod("setLastName", String.class).invoke(name, "Bar");
	nameClass.getMethod("setMiddleName", String.class).invoke(name, "The");
	customerClass.getMethod("setName", nameClass).invoke(customer, name);
	
	Map<String, Object> args = new java.util.HashMap<String, Object>();
	args.put("Customer", new Holder(customer));
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	method.writeRequestPreview(args, bos);
	Assert.assertTrue(bos.toString().contains("<firstName>Foo</firstName>"));
	InvocationResult result = method.invoke(args, null);
	Map<String, Object> res = result.getMapRequestAndResult(null, null);
	Map<String, Object> test = (Map<String, Object>) res.get("results");
	Assert.assertEquals(void.class, test.get("type.result"));
	ParameterizedType returnType = (ParameterizedType)test.get("type.Customer");
	Assert.assertEquals(Holder.class, returnType.getRawType());
	Assert.assertEquals(customerClass, returnType.getActualTypeArguments()[0]);
	Assert.assertNull(test.get("result"));
	Object returnObj = ((Holder<?>)test.get("Customer")).value;
	Object returnNameObj = customerClass.getMethod("getName").invoke(returnObj);
	Assert.assertEquals("Foo", nameClass.getMethod("getFirstName").invoke(returnNameObj));
    }

    @AfterClass
    public static void tearDown() throws Exception {
	try {
	    undeployWS(warUrl);
	} finally {
	    warUrl = null;
	    client.close();
	    client = null;
	}
    }
}
