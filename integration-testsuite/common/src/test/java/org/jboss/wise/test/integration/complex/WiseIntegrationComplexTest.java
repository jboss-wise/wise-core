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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.wise.core.client.InvocationResult;
import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.client.WebParameter;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.client.factories.WSDynamicClientFactory;
import org.jboss.wise.core.test.WiseTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.jws.WebParam;
import javax.xml.ws.Holder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.util.Map;

/**
 * @author alessio.soldano@jboss.com
 */
@RunWith(Arquillian.class)
public class WiseIntegrationComplexTest extends WiseTest {

    private static WSDynamicClient client;

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
    public void shouldInvokeRegisterOperation() throws Exception {
        setUp();
        WSMethod method = client.getWSMethod("RegistrationServiceImplService", "RegistrationServiceImplPort", "Register");
        Map<String, ? extends WebParameter> pars = method.getWebParams();
        WebParameter customerPar = pars.get("Customer");
        Class<?> customerClass = (Class<?>) customerPar.getType();
        Object customer = customerClass.newInstance();
        customerClass.getMethod("setId", long.class).invoke(customer, new Long(1234));
        Class<?> nameClass = (Class<?>) customerClass.getDeclaredField("name").getType();
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
        tearDown();
    }

    @Test
    @RunAsClient
    public void shouldInvokeRegisterOperationWithFault() throws Exception {
        setUp();
        WSMethod method = client.getWSMethod("RegistrationServiceImplService", "RegistrationServiceImplPort", "Register");
        Map<String, ? extends WebParameter> pars = method.getWebParams();
        WebParameter customerPar = pars.get("Customer");
        Class<?> customerClass = (Class<?>) customerPar.getType();
        Object customer = customerClass.newInstance();
        customerClass.getMethod("setId", long.class).invoke(customer, new Long(1234));

        Map<String, Object> args = new java.util.HashMap<String, Object>();
        args.put("Customer", customer);
        args.put("When", null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        method.writeRequestPreview(args, bos);
        Assert.assertTrue(bos.toString().contains("<id>1234</id>"));
        InvocationResult result = method.invoke(args, null);
        Map<String, Object> res = result.getMapRequestAndResult(null, null);
        Map<String, Object> test = (Map<String, Object>) res.get("results");
        Assert.assertNotNull(test.get("exception"));
        Assert.assertTrue(test.get("exception").toString().contains("No name!"));
        Assert.assertEquals(Class.class, test.get("type.result").getClass());
        Assert.assertEquals("org.jboss.test.ws.jaxws.complex.types.ValidationFaultException",
                ((Class) (test.get("type.result"))).getName());
        tearDown();
    }

    @Test
    @RunAsClient
    public void shouldInvokeEchoOperation() throws Exception {
        setUp();
        WSMethod method = client.getWSMethod("RegistrationServiceImplService", "RegistrationServiceImplPort", "Echo");
        Map<String, ? extends WebParameter> pars = method.getWebParams();
        WebParameter customerPar = pars.get("Customer");
        Assert.assertEquals(WebParam.Mode.INOUT, customerPar.getMode());
        Assert.assertEquals(Holder.class, (Class<?>) ((ParameterizedType) customerPar.getType()).getRawType());
        Class<?> customerClass = (Class<?>) ((ParameterizedType) customerPar.getType()).getActualTypeArguments()[0];
        Object customer = customerClass.newInstance();
        customerClass.getMethod("setId", long.class).invoke(customer, new Long(1235));
        Class<?> nameClass = (Class<?>) customerClass.getDeclaredField("name").getType();
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
        ParameterizedType returnType = (ParameterizedType) test.get("type.Customer");
        Assert.assertEquals(Holder.class, returnType.getRawType());
        Assert.assertEquals(customerClass, returnType.getActualTypeArguments()[0]);
        Assert.assertNull(test.get("result"));
        Object returnObj = ((Holder<?>) test.get("Customer")).value;
        Object returnNameObj = customerClass.getMethod("getName").invoke(returnObj);
        Assert.assertEquals("Foo", nameClass.getMethod("getFirstName").invoke(returnNameObj));
        tearDown();
    }

    public static void tearDown() throws Exception {
        client.close();
        client = null;
    }
}
