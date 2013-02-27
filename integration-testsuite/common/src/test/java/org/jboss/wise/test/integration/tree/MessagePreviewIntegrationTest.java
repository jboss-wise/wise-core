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
package org.jboss.wise.test.integration.tree;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.client.WebParameter;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.client.factories.WSDynamicClientFactory;
import org.jboss.wise.core.exception.InvocationException;
import org.jboss.wise.core.test.WiseTest;
import org.jboss.wise.core.utils.JavaUtils;
import org.jboss.wise.tree.Element;
import org.jboss.wise.tree.ElementBuilder;
import org.jboss.wise.tree.ElementBuilderFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author alessio.soldano@jboss.com
 *
 */
public class MessagePreviewIntegrationTest extends WiseTest {
    
    private static URL warUrl = null;
    private static WSDynamicClient client;
    private final String registerOpRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "  <soap:Header/>\n" +
            "  <soap:Body>\n" +
            "    <ns2:Register xmlns=\"http://complex.jaxws.ws.test.jboss.org/\" xmlns:ns2=\"http://types.complex.jaxws.ws.test.jboss.org/\" xmlns:ns3=\"http://extra.complex.jaxws.ws.test.jboss.org/\">\n" +
            "      <ns2:Customer>\n" +
            "        <address>\n" +
            "          <city>?</city>\n" +
            "          <state>?</state>\n" +
            "          <street>?</street>\n" +
            "          <zip>?</zip>\n" +
            "        </address>\n" +
            "        <contactNumbers>\n" +
            "          <areaCode>?</areaCode>\n" +
            "          <exchange>?</exchange>\n" +
            "          <line>?</line>\n" +
            "        </contactNumbers>\n" +
            "        <id>0</id>\n" +
            "        <name>\n" +
            "          <firstName>?</firstName>\n" +
            "          <lastName>?</lastName>\n" +
            "          <middleName>?</middleName>\n" +
            "        </name>\n" +
            "        <referredCustomers>\n" +
            "          <address>\n" +
            "            <city>?</city>\n" +
            "            <state>?</state>\n" +
            "            <street>?</street>\n" +
            "            <zip>?</zip>\n" +
            "          </address>\n" +
            "          <contactNumbers>\n" +
            "            <areaCode>?</areaCode>\n" +
            "            <exchange>?</exchange>\n" +
            "            <line>?</line>\n" +
            "          </contactNumbers>\n" +
            "          <contactNumbers>\n" +
            "            <areaCode>?</areaCode>\n" +
            "            <exchange>?</exchange>\n" +
            "            <line>?</line>\n" +
            "          </contactNumbers>\n" +
            "          <id>0</id>\n" +
            "          <name>\n" +
            "            <firstName>?</firstName>\n" +
            "            <lastName>?</lastName>\n" +
            "            <middleName>?</middleName>\n" +
            "          </name>\n" +
            "          <referredCustomers xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n" +
            "          <referredCustomers xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n" +
            "        </referredCustomers>\n" +
            "      </ns2:Customer>\n" +
            "      <ns2:When xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xs:string\"/>\n" +
            "    </ns2:Register>\n" +
            "  </soap:Body>\n" +
            "</soap:Envelope>\n";

    @BeforeClass
    public static void setUp() throws Exception {
	warUrl = MessagePreviewIntegrationTest.class.getClassLoader().getResource("complex.war");
	deployWS(warUrl);
	
	URL wsdlURL = new URL(getServerHostAndPort() + "/complex/RegistrationService?wsdl");

	WSDynamicClientBuilder clientBuilder = WSDynamicClientFactory.getJAXWSClientBuilder();
	client = clientBuilder.tmpDir("target/temp/wise").verbose(true).keepSource(true).wsdlURL(wsdlURL
		.toString()).build();
    }

    @Test
    public void shouldPreviewRegisterOperation() throws Exception {
	WSMethod method = client.getWSMethod("RegistrationServiceImplService", "RegistrationServiceImplPort", "Register");
	String messagePreview = previewMessage(method);
	Assert.assertEquals(registerOpRequest, messagePreview); //TODO improve check...
    }
    
    @Test
    public void shouldPreviewEchoOperation() throws Exception {
	WSMethod method = client.getWSMethod("RegistrationServiceImplService", "RegistrationServiceImplPort", "Echo");
	String messagePreview = previewMessage(method);
//	System.out.println("--> " + messagePreview); //TODO check...
    }

    private String previewMessage(WSMethod method) throws InvocationException {
	Map<String, ? extends WebParameter> pars = method.getWebParams();
	ElementBuilder builder = ElementBuilderFactory.getElementBuilder().client(client).request(true).useDefautValuesForNullLeaves(false);
	Map<String, Element> elementsMap = new HashMap<String, Element>();
	for (Entry<String, ? extends WebParameter> par : pars.entrySet()) {
	    String parName = par.getKey();
	    WebParameter parameter = par.getValue();
	    Element parElement = builder.buildTree(parameter.getType(), parName, null, true);
	    populateElement(parElement, 1);
	    elementsMap.put(parName, parElement);
	}
	Map<String, Object> args = new java.util.HashMap<String, Object>();
	for (Entry<String, Element> elem : elementsMap.entrySet()) {
	    String parName = elem.getKey();
	    Element element = elem.getValue();
	    args.put(parName, element.toObject());
	}
	
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	method.writeRequestPreview(args, bos);
	return bos.toString();
    }
    
    private void populateElement(Element element, int remainingLazyExpansions) {
	if (element.isLazy()) {
	    if (!element.isResolved()) {
		if (remainingLazyExpansions > 0) {
		    element.getChildrenCount(); // force resolution
		    populateElement(element, --remainingLazyExpansions);
		} else {
		    return;
		}
	    }
	}
	element.setNil(false);
	if (element.isLeaf()) {
	    element.setValue(getDefaultValue((Class<?>) element.getClassType()));
	} else {
	    if (element.isGroup()) {
		element.incrementChildren();
	    }
	    for (Iterator<? extends Element> it = element.getChildren(); it.hasNext();) {
		populateElement(it.next(), remainingLazyExpansions);
	    }
	}
    }
    
    protected static String getDefaultValue(Class<?> cl) {
	if (cl.isPrimitive()) {
	    cl = JavaUtils.getWrapperType(cl);
	}
	String cn = cl.getName();
	if ("java.lang.Boolean".equals(cn)) {
	    return "false";
	} else if ("java.lang.String".equals(cn)) {
	    return "?";
	} else if ("java.lang.Byte".equals(cn)) {
	    return "0";
	} else if ("java.lang.Double".equals(cn)) {
	    return "0.0";
	} else if ("java.lang.Float".equals(cn)) {
	    return "0.0";
	} else if ("java.lang.Integer".equals(cn)) {
	    return "0";
	} else if ("java.lang.Long".equals(cn)) {
	    return "0";
	} else if ("java.lang.Short".equals(cn)) {
	    return "0";
	} else if ("java.math.BigDecimal".equals(cn)) {
	    return "0.0";
	} else if ("java.math.BigInteger".equals(cn)) {
	    return "0";
	} else if ("javax.xml.datatype.Duration".equals(cn)) {
	    return "0";
	} else if ("javax.xml.datatype.XMLGregorianCalendar".equals(cn)) {
	    return "1970-01-01T00:00:00.000Z";
	} else {
	    return "";
	}
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
