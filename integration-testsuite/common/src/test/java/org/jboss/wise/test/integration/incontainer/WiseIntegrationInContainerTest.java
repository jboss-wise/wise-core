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
package org.jboss.wise.test.integration.incontainer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.jboss.wise.core.test.WiseTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class WiseIntegrationInContainerTest extends WiseTest {
    private static URL warUrl = null;
    private static URL servletWarUrl = null;

    @BeforeClass
    public static void setUp() throws Exception {
	warUrl = WiseIntegrationInContainerTest.class.getClassLoader().getResource("basic.war");
	servletWarUrl = WiseIntegrationInContainerTest.class.getClassLoader().getResource("incontainer.war");
	deployWS(warUrl);
	deployWS(servletWarUrl);
    }

    @Test
    public void test() throws Exception {
	URL url = new URL(getServerHostAndPort() + "/incontainer/HelloWorldServlet?name=foo");
	BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
	String result = br.readLine();
	if (result.startsWith("[FIXME]")) {
	    System.out.println(result);
	} else {
	    Assert.assertEquals("WS return: foo", br.readLine());
	}
    }

    @AfterClass
    public static void tearDown() throws Exception {
	try {
	    undeployWS(warUrl);
	} finally {
	    warUrl = null;
	}
	try {
	    undeployWS(servletWarUrl);
	} finally {
	    servletWarUrl = null;
	}
    }
}
