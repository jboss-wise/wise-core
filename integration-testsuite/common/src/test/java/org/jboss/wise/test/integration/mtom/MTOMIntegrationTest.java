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
package org.jboss.wise.test.integration.mtom;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.wise.core.client.InvocationResult;
import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.client.factories.WSDynamicClientFactory;
import org.jboss.wise.core.test.WiseTest;
import org.jboss.wise.core.wsextensions.impl.MTOMEnabler;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author stefano.maestri@javalinux.it
 * @author alessio.soldano@jboss.com
 */
@RunWith(Arquillian.class)
public class MTOMIntegrationTest extends WiseTest {

    private URL warUrl = null;

    @Deployment
    public static JavaArchive createDeployment() {
        // archive is empty by design
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "mtom-tests.jar");
        return archive;
    }

    @Test
    @RunAsClient
    public void test() {
        System.out.println("[FIXME] [WISE-45] MTOMSample fails returning empty binaries");
    }

    public void shouldRunWithoutMK() throws Exception {
        URL wsdlURL = new URL(getServerHostAndPort() + "/mtom-tests/MTOMWS?wsdl");
        WSDynamicClientBuilder clientBuilder = WSDynamicClientFactory.getJAXWSClientBuilder();
        WSDynamicClient client = clientBuilder.tmpDir("target/temp/wise").verbose(true).keepSource(true)
                .wsdlURL(wsdlURL.toString()).build();
        try {
            WSMethod method = client.getWSMethod("MTOMWSService", "MTOMPort", "sayHello");
            method.getEndpoint().addWSExtension(new MTOMEnabler(client));
            HashMap<String, Object> requestMap = new HashMap<String, Object>();
            requestMap.put("toWhom", "SpiderMan");
            InvocationResult result = method.invoke(requestMap, null);
            Map map = (Map) result.getMapRequestAndResult(null, null).get("results");
            byte[] bytes = (byte[]) map.get("result");
            System.out.println("bytes: " + bytes);
            Assert.assertNotNull(bytes);
        } finally {
            client.close();
        }
    }
}
