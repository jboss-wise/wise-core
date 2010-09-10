/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.wise.core.consumer.impl.jbosswsnative;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.junit.Test;

/**
 * @author oracle
 * 
 */
public class WiseForceToolsProviderClassLoaderTest {

    /**
     * Test method for
     * {@link org.jboss.wise.core.consumer.impl.jbosswsnative.WiseForceToolsProviderClassLoader#getResourceAsStream(java.lang.String)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public void getResourceAsStreamShouldGetToolsProvider() throws Exception {
	URL[] urls = {};
	WiseForceToolsProviderClassLoader cl = new WiseForceToolsProviderClassLoader(urls, Thread.currentThread()
		.getContextClassLoader());

	InputStream inStream = cl.getResourceAsStream("META-INF/services/javax.xml.ws.spi.Provider");
	String factoryName = null;
	if (inStream != null) {
	    BufferedReader br = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
	    factoryName = br.readLine();
	    br.close();
	}
	assertThat(factoryName, is("com.sun.xml.ws.spi.ProviderImpl"));

    }
}
