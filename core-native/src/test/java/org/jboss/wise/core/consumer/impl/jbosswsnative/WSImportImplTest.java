/*
 * JBoss, Home of Professional Open Source Copyright 2006, JBoss Inc., and
 * individual contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of individual
 * contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package org.jboss.wise.core.consumer.impl.jbosswsnative;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.junit.Test;

/**
 * @author stefano.maestri@javalinux.it
 */
public class WSImportImplTest {

    @Test
    public void importObjectFromWsdlShouldSetProviderProperties() throws Exception {
	URL url = Thread.currentThread().getContextClassLoader().getResource(".");
	File outputDir = new File(url.getFile());
	URL wsdURL = Thread.currentThread().getContextClassLoader().getResource("./hello_world.wsdl");
	WSImportImpl.ProviderChanger providerChanger = mock(WSImportImpl.ProviderChanger.class);
	WSImportImpl importer = new WSImportImpl(providerChanger);
	importer.importObjectFromWsdl(wsdURL.toExternalForm(), outputDir, outputDir, null, null, System.out, null);
	verify(providerChanger).changeProvider();
	verify(providerChanger).restoreDefaultProvider();

    }

    @Test
    public void importObjectFromWsdlShouldRestoreDefaultProviderProperties() throws Exception {
	URL url = Thread.currentThread().getContextClassLoader().getResource(".");
	File outputDir = new File(url.getFile());
	URL wsdURL = Thread.currentThread().getContextClassLoader().getResource("./hello_world.wsdl");
	WSImportImpl importer = new WSImportImpl();
	String defaultProvider = System.getProperty("javax.xml.ws.spi.Provider");
	importer.importObjectFromWsdl(wsdURL.toExternalForm(), outputDir, outputDir, null, null, System.out, null);
	assertThat(System.getProperty("javax.xml.ws.spi.Provider"), equalTo(defaultProvider));

    }

    @SuppressWarnings("unchecked")
    @Test()
    public void defineAdditionalCompilerClassPathShouldReturnRightJars() {

	WSImportImpl importer = new WSImportImpl();
	List<String> jars = importer.defineAdditionalCompilerClassPath();
	assertThat(jars, hasItems(containsString("jaxb-impl")));
    }
}
