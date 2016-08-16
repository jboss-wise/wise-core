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
package org.jboss.wise.test.integration.basic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.Map;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.wise.core.client.InvocationResult;
import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.client.factories.WSDynamicClientFactory;
import org.jboss.wise.core.exception.WiseWebServiceException;
import org.jboss.wise.core.test.WiseTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class WiseIntegrationAuthTest extends WiseTest {

    private static final String WAR = "basic-auth";

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, WAR + ".war");
        archive.addClass(org.jboss.wise.test.integration.basic.HelloWorldInterface.class)
                .addClass(org.jboss.wise.test.integration.basic.HelloWorldBean.class)
                .addAsWebInfResource(new File(getTestResourcesDir() + "/WEB-INF/basic-auth/jboss-web.xml"))
                .setWebXML(new File(getTestResourcesDir() + "/WEB-INF/basic-auth/web.xml"));
        return archive;
    }

    @Test
    @RunAsClient
    public void shouldRun() throws Exception {

        URL wsdlURL = new URL(getServerHostAndPort() + "/basic-auth/HelloWorld?wsdl");

        WSDynamicClientBuilder clientBuilder = WSDynamicClientFactory.getJAXWSClientBuilder();
        WSDynamicClient client = clientBuilder.tmpDir("target/temp/wise").verbose(true).keepSource(true)
                .wsdlURL(wsdlURL.toString()).userName("kermit").password("thefrog").build();
        WSMethod method = client.getWSMethod("HelloService", "HelloWorldBeanPort", "echo");
        Map<String, Object> args = new java.util.HashMap<String, Object>();
        args.put("arg0", "from-wise-client");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        method.writeRequestPreview(args, bos);
        Assert.assertTrue(bos.toString().contains("<arg0>from-wise-client</arg0>"));
        InvocationResult result = method.invoke(args, null);
        Map<String, Object> res = result.getMapRequestAndResult(null, null);
        @SuppressWarnings("unchecked")
        Map<String, Object> test = (Map<String, Object>) res.get("results");
        client.close();
        Assert.assertEquals("from-wise-client", test.get("result"));
    }

    @Test
    @RunAsClient
    public void shouldFailBecauseOfAuthenticationException() throws Exception {

        URL wsdlURL = new URL(getServerHostAndPort() + "/basic-auth/HelloWorld?wsdl");

        WSDynamicClientBuilder clientBuilder = WSDynamicClientFactory.getJAXWSClientBuilder();
        WSDynamicClient client = clientBuilder.tmpDir("target/temp/wise").verbose(true).keepSource(true)
                .wsdlURL(wsdlURL.toString()).build();
        WSMethod method = client.getWSMethod("HelloService", "HelloWorldBeanPort", "echo");
        Map<String, Object> args = new java.util.HashMap<String, Object>();
        args.put("arg0", "from-wise-client");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        method.writeRequestPreview(args, bos);
        Assert.assertTrue(bos.toString().contains("<arg0>from-wise-client</arg0>"));
        try {
            method.invoke(args, null);
            Assert.fail("Exception expected");
        } catch (WiseWebServiceException wwse) {
            Assert.assertTrue(wwse.getMessage().contains("Authentication exception"));
        } catch (Throwable t) {
            Assert.fail("Authentication exception expected, but got " + t);
        }
    }
}
