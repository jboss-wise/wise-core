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
package org.jboss.wise.test.integration.jbide14739;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.wise.core.test.WiseTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.URL;

@RunWith(Arquillian.class)
public class JBIDE14739BIntegrationTest extends WiseTest {

    private static final String WARB = "jbide14739B";

    @Deployment
    public static WebArchive createDeploymentB() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, WARB + ".war");
        archive.addClass(org.jboss.wise.test.integration.jbide14739.HelloWorldBInterface.class)
                .addClass(org.jboss.wise.test.integration.jbide14739.HelloWorldBBean.class)
                .setWebXML(new File(getTestResourcesDir() + "/WEB-INF/jbide14739B/web.xml"));
        return archive;
    }

    @Test
    @RunAsClient
    public void shouldConsumeNewWsdlAfterEndpointRefreshB() throws Exception {
        URL wsdlURL = new URL(getServerHostAndPort() + "/jbide14739/HelloWorld?wsdl");
        JBIDE14739IntegrationTest.runWise(wsdlURL, "target/temp/wise/jbide14739B", "echoB");
    }
}
