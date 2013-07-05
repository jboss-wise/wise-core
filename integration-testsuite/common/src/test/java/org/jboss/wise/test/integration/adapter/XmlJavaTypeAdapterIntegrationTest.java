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
package org.jboss.wise.test.integration.adapter;

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
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author alessio.soldano@jboss.com
 *
 */
public class XmlJavaTypeAdapterIntegrationTest extends WiseTest {
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @Ignore("[CXF-5110] Wrong processing of @XmlJavaTypeAdapter with RPC style endpoints")
    public void shouldPreviewRequest() throws Exception {
	URL wsdlUrl = XmlJavaTypeAdapterIntegrationTest.class.getResource("EndpointService.wsdl");
	WSDynamicClientBuilder clientBuilder = WSDynamicClientFactory.getJAXWSClientBuilder();
	WSDynamicClient client = null;
	try {
	    client = clientBuilder.tmpDir("target/temp/wise").verbose(true).keepSource(true).wsdlURL(wsdlUrl.toString()).build();
	    WSMethod method = client.getWSMethod("EndpointService", "EndpointPort", "getData");
	    Map<String, ? extends WebParameter> pars = method.getWebParams();
	    WebParameter customerPar = pars.get("dataQuery");
	    Assert.assertEquals(WebParam.Mode.INOUT, customerPar.getMode());
	    Assert.assertEquals(Holder.class, (Class<?>) ((ParameterizedType) customerPar.getType()).getRawType());
	    Class<?> dataQueryClass = (Class<?>) ((ParameterizedType) customerPar.getType()).getActualTypeArguments()[0];
	    Object dataQuery = dataQueryClass.newInstance();
	    dataQueryClass.getMethod("setQuery", String.class).invoke(dataQuery, "test this now");
	    Map<String, Object> args = new java.util.HashMap<String, Object>();
	    args.put("dataQuery", new Holder(dataQuery));
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    method.writeRequestPreview(args, bos);
	    Assert.assertTrue(bos.toString().contains("test this now"));
	} finally {
	    if (client != null) {
		client.close();
	    }
	}
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @Ignore("[CXF-5110] Wrong processing of @XmlJavaTypeAdapter with RPC style endpoints")
    public void shouldPreviewAndInvoke() throws Exception {
	URL warUrl = XmlJavaTypeAdapterIntegrationTest.class.getClassLoader().getResource("adapter.war");
	WSDynamicClientBuilder clientBuilder = WSDynamicClientFactory.getJAXWSClientBuilder();
	WSDynamicClient client = null;
	try {
	    deployWS(warUrl);
	    URL wsdlUrl = new URL(getServerHostAndPort() + "/adapter/Endpoint?wsdl");
	    client = clientBuilder.tmpDir("target/temp/wise").verbose(true).keepSource(true).wsdlURL(wsdlUrl.toString()).build();
	    WSMethod method = client.getWSMethod("EndpointService", "EndpointPort", "getData");
	    Map<String, ? extends WebParameter> pars = method.getWebParams();
	    WebParameter customerPar = pars.get("dataQuery");
	    Assert.assertEquals(WebParam.Mode.INOUT, customerPar.getMode());
	    Assert.assertEquals(Holder.class, (Class<?>) ((ParameterizedType) customerPar.getType()).getRawType());
	    Class<?> dataQueryClass = (Class<?>) ((ParameterizedType) customerPar.getType()).getActualTypeArguments()[0];
	    Object dataQuery = dataQueryClass.newInstance();
	    dataQueryClass.getMethod("setQuery", String.class).invoke(dataQuery, "test this now");
	    Map<String, Object> args = new java.util.HashMap<String, Object>();
	    args.put("dataQuery", new Holder(dataQuery));
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    method.writeRequestPreview(args, bos);
	    Assert.assertTrue(bos.toString().contains("test this now"));
	    
	    InvocationResult result = method.invoke(args);
	    Map<String, Object> res = result.getMapRequestAndResult(null, null);
	    Map<String, Object> test = (Map<String, Object>) res.get("results");
	    Assert.assertEquals(byte[].class, test.get("type.result"));
	    byte[] bytes = (byte[])test.get("result");
	    Assert.assertNotNull(bytes);
	    Assert.assertEquals(2, bytes.length);
	    Assert.assertEquals(0, bytes[0]);
	    Assert.assertEquals(1, bytes[1]);
	} finally {
	    if (client != null) {
		client.close();
	    }
	}
    }
}
