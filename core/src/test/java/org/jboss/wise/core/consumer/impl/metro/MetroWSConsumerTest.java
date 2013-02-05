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
package org.jboss.wise.core.consumer.impl.metro;

import java.io.File;
import java.net.URL;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.junit.Test;

public class MetroWSConsumerTest {
    @Test( expected = WiseRuntimeException.class )
    public void shouldThrowsWiseRuntimeExceptionIMetroHomeNotSet() throws Exception {
        MetroWSConsumer consumer = new MetroWSConsumer();
        // consumer.setMetroHome("/home/jimma/java/metro/1.2");
        URL url = Thread.currentThread().getContextClassLoader().getResource(".");
        File outputDir = new File(url.getFile());
        URL wsdURL = Thread.currentThread().getContextClassLoader().getResource("./AddNumbers.wsdl");
        consumer.importObjectFromWsdl(wsdURL.toExternalForm(), outputDir, outputDir, "org.jimma", null, System.out, null);

        // Uncomment it when Metro jars are available
        // File codeGenDir = new File(outputDir, "org/jimma/");
        // Assert.assertEquals(18, codeGenDir.list().length);

    }

}
