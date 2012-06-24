/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.wise.core.consumer.impl;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jboss.wise.core.client.SpiLoader;
import org.jboss.wise.core.consumer.WSConsumer;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.wise.core.utils.LoggingOutputStream;
import org.junit.Test;

/**
 * @author stefano.maestri@javalinux.it
 * @author alessio.soldano@jboss.com
 */
public class WSConsumerTest {
    
    private PrintStream testPrintStream = new PrintStream(new LoggingOutputStream(Logger.getLogger(this.getClass()), Level.INFO), true);

    @Test
    public void parseHelloGreetingWSDLShouldWorkWithoutPackage() throws Exception {
	URL url = Thread.currentThread().getContextClassLoader().getResource(".");
	File outputDir = new File(url.getFile());
	URL wsdURL = Thread.currentThread().getContextClassLoader().getResource("./hello_world.wsdl");
	WSConsumer importer = newConsumer();
	importer.importObjectFromWsdl(wsdURL.toExternalForm(), outputDir, outputDir, null, null, testPrintStream, null);
    }

    @Test()
    public void parseHelloWSDLWithBindingFile() throws Exception {
	URL url = Thread.currentThread().getContextClassLoader().getResource(".");
	File outputDir = new File(url.getFile());
	URL bindingURL = Thread.currentThread().getContextClassLoader().getResource("./jaxws-binding.xml");
	URL wsdURL = Thread.currentThread().getContextClassLoader().getResource("./hello_world.wsdl");
	File bindFile = new File(bindingURL.getFile());
	List<File> bindings = new java.util.ArrayList<File>();
	bindings.add(bindFile);
	WSConsumer importer = newConsumer();
	importer.importObjectFromWsdl(wsdURL.toExternalForm(), outputDir, outputDir, null, bindings, testPrintStream, null);
	File generatedClass = new File(url.getFile(), "org/mytest");
	assertTrue(generatedClass.exists());
    }

    @Test(expected = WiseRuntimeException.class)
    public void parseHelloGreetingWSDLShouldFailWithPackageAndNoBindingsForNameDuplication() throws Exception {
	URL url = Thread.currentThread().getContextClassLoader().getResource(".");
	File outputDir = new File(url.getFile());
	URL wsdURL = Thread.currentThread().getContextClassLoader().getResource("./hello_world.wsdl");
	WSConsumer importer = newConsumer();
	importer.importObjectFromWsdl(wsdURL.toExternalForm(), outputDir, outputDir, "org.jboss.wise", null, testPrintStream, null);
    }

    @Test()
    public void getClassNamesShouldFindFooClassPlaceHolder() throws Exception {
	WSConsumer importer = newConsumer();
	URL url = Thread.currentThread().getContextClassLoader().getResource("./placeHolderClasses/");
	File file = new File(url.getFile());
	List<String> list = importer.getClassNames(file);
	assertThat(list.size(), is(1));
	assertThat(list, hasItem("org.jboss.foo.Foo"));

    }

    private WSConsumer newConsumer() {
	WSConsumer consumer = (WSConsumer) SpiLoader.loadService("org.jboss.wise.consumer.WSConsumer", "org.jboss.wise.core.consumer.impl.jbossws.DefaultWSImportImpl");
	consumer.setKeepSource(true); //workaround for [JBWS-3519] to avoid generating in the Wise source tree instead of target/test-classes
	return consumer;
    }
}
