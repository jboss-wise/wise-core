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
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author alessio.soldano@jboss.com
 */
@RunWith(Arquillian.class)
public class MessagePreviewIntegrationTest extends WiseTest {

    private static WSDynamicClient client;
    private final String registerOpRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
            + "  <soap:Header/>\n"
            + "  <soap:Body>\n"
            + "    <ns2:Register xmlns:ns2=\"http://types.complex.jaxws.ws.test.jboss.org/\">\n"
            + "      <Customer>\n"
            + "        <address>\n"
            + "          <city>?</city>\n"
            + "          <state>?</state>\n"
            + "          <street>?</street>\n"
            + "          <zip>?</zip>\n"
            + "        </address>\n"
            + "        <contactNumbers>\n"
            + "          <areaCode>?</areaCode>\n"
            + "          <exchange>?</exchange>\n"
            + "          <line>?</line>\n"
            + "        </contactNumbers>\n"
            + "        <id>0</id>\n"
            + "        <name>\n"
            + "          <firstName>?</firstName>\n"
            + "          <lastName>?</lastName>\n"
            + "          <middleName>?</middleName>\n"
            + "        </name>\n"
            + "        <referredCustomers>\n"
            + "          <address>\n"
            + "            <city>?</city>\n"
            + "            <state>?</state>\n"
            + "            <street>?</street>\n"
            + "            <zip>?</zip>\n"
            + "          </address>\n"
            + "          <contactNumbers>\n"
            + "            <areaCode>?</areaCode>\n"
            + "            <exchange>?</exchange>\n"
            + "            <line>?</line>\n"
            + "          </contactNumbers>\n"
            + "          <contactNumbers>\n"
            + "            <areaCode>?</areaCode>\n"
            + "            <exchange>?</exchange>\n"
            + "            <line>?</line>\n"
            + "          </contactNumbers>\n"
            + "          <id>0</id>\n"
            + "          <name>\n"
            + "            <firstName>?</firstName>\n"
            + "            <lastName>?</lastName>\n"
            + "            <middleName>?</middleName>\n"
            + "          </name>\n"
            + "          <referredCustomers xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n"
            + "          <referredCustomers xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n"
            + "        </referredCustomers>\n"
            + "      </Customer>\n"
            + "      <When xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xs:string\"/>\n"
            + "    </ns2:Register>\n" + "  </soap:Body>\n" + "</soap:Envelope>\n";

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "complex.war");
        archive.addClass(org.jboss.wise.test.integration.complex.Address.class)
                .addClass(org.jboss.wise.test.integration.complex.AlreadyRegisteredFault_Exception.class)
                .addClass(org.jboss.wise.test.integration.complex.AlreadyRegisteredFault.class)
                .addClass(org.jboss.wise.test.integration.complex.ObjectFactory.class)
                .addClass(org.jboss.wise.test.integration.complex.BulkRegisterResponse.class)
                .addClass(org.jboss.wise.test.integration.complex.BulkRegister.class)
                .addClass(org.jboss.wise.test.integration.complex.Customer.class)
                .addClass(org.jboss.wise.test.integration.complex.Echo.class)
                .addClass(org.jboss.wise.test.integration.complex.EchoResponse.class)
                .addClass(org.jboss.wise.test.integration.complex.GetStatistics.class)
                .addClass(org.jboss.wise.test.integration.complex.GetStatisticsResponse.class)
                .addClass(org.jboss.wise.test.integration.complex.InvoiceCustomer.class)
                .addClass(org.jboss.wise.test.integration.complex.Name.class)
                .addClass(org.jboss.wise.test.integration.complex.PhoneNumber.class)
                .addClass(org.jboss.wise.test.integration.complex.RegisterForInvoice.class)
                .addClass(org.jboss.wise.test.integration.complex.RegisterForInvoiceResponse.class)
                .addClass(org.jboss.wise.test.integration.complex.Register.class)
                .addClass(org.jboss.wise.test.integration.complex.RegisterResponse.class)
                .addClass(org.jboss.wise.test.integration.complex.Registration.class)
                .addClass(org.jboss.wise.test.integration.complex.RegistrationFault.class)
                .addClass(org.jboss.wise.test.integration.complex.RegistrationServiceImpl.class)
                .addClass(org.jboss.wise.test.integration.complex.Statistics.class)
                .addClass(org.jboss.wise.test.integration.complex.ValidationFault.class)
                .addClass(org.jboss.wise.test.integration.complex.ValidationFault_Exception.class)
                .setWebXML(new File(getTestResourcesDir() + "/WEB-INF/complex/web.xml"));
        return archive;
    }

    public static void setUp() throws Exception {
        URL wsdlURL = new URL(getServerHostAndPort() + "/complex/RegistrationService?wsdl");

        WSDynamicClientBuilder clientBuilder = WSDynamicClientFactory.getJAXWSClientBuilder();
        client = clientBuilder.tmpDir("target/temp/wise").verbose(true).keepSource(true).wsdlURL(wsdlURL.toString()).build();
    }

    @Test
    @RunAsClient
    public void shouldPreviewRegisterOperation() throws Exception {
        setUp();
        WSMethod method = client.getWSMethod("RegistrationServiceImplService", "RegistrationServiceImplPort", "Register");
        String messagePreview = previewMessage(method);
        Assert.assertEquals(registerOpRequest, messagePreview); // TODO improve check...
        tearDown();
    }

    @Test
    @RunAsClient
    public void shouldPreviewEchoOperation() throws Exception {
        setUp();
        WSMethod method = client.getWSMethod("RegistrationServiceImplService", "RegistrationServiceImplPort", "Echo");
        String messagePreview = previewMessage(method);
        // System.out.println("--> " + messagePreview); //TODO check...
        tearDown();
    }

    private String previewMessage(WSMethod method) throws InvocationException {
        Map<String, ? extends WebParameter> pars = method.getWebParams();
        ElementBuilder builder = ElementBuilderFactory.getElementBuilder().client(client).request(true)
                .useDefautValuesForNullLeaves(false);
        Map<String, Element> elementsMap = new HashMap<String, Element>();
        for (Entry<String, ? extends WebParameter> par : pars.entrySet()) {
            String parName = par.getKey();
            Element parElement = builder.buildTree(par.getValue().getType(), parName, null, true);
            populateElement(parElement, 1);
            elementsMap.put(parName, parElement);
        }
        Map<String, Object> args = new java.util.HashMap<String, Object>();
        for (Entry<String, Element> elem : elementsMap.entrySet()) {
            args.put(elem.getKey(), elem.getValue().toObject());
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

    public static void tearDown() throws Exception {
        client.close();
        client = null;
    }
}
