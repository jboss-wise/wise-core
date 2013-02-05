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
package org.jboss.wise.core.client.impl.wsdlResolver;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.jcip.annotations.Immutable;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * @author alessio.soldano@jboss.com
 * @since 13-May-2009
 */
@Immutable
public class WSDLResolverTest {
    
    private static final File tmpDir = new File("target/temp/WSDLResolverTest");
    
    @Test
    public void shouldSaveSimpleWsdl() throws Exception {
        performTest(tmpDir.getAbsolutePath() + File.separator + "SimpleWsdl", new File("./src/test/resources/testWsdls/Simple/SimpleWsdl.wsdl").toURI().toURL());
    }
    
    @Test
    public void shouldSaveWsdlWithWsdlImport() throws Exception {
	performTest(tmpDir.getAbsolutePath() + File.separator + "WsdlWsdl", new File("./src/test/resources/testWsdls/WsdlImport/WsdlWithWsdlImport.wsdl").toURI().toURL());
    }
    
    @Test
    public void shouldSaveWsdlWithSchemaImport() throws Exception {
	performTest(tmpDir.getAbsolutePath() + File.separator + "SchemaWsdl", new File("./src/test/resources/testWsdls/SchemaImport/WsdlWithSchemaImport.wsdl").toURI().toURL());
    }
    
    @Test
    public void shouldSaveWsdlWithSchemaAndWsdlImports() throws Exception {
	performTest(tmpDir.getAbsolutePath() + File.separator + "SchemaAndWsdlWsdl", new File("./src/test/resources/testWsdls/SchemaAndWsdlImport/WsdlWithSchemaAndWsdlImport.wsdl").toURI().toURL());
    }
    
    private void performTest(String testTempDir, URL wsdlURL) throws Exception {
        WSDLResolver resolver = new WSDLResolver(testTempDir);
        File wsdlFile = resolver.retrieveWsdlFile(wsdlURL);
        assertTrue(wsdlFile.exists());
        assertTrue(wsdlFile.length() > 0);
    }
    
    @Before
    public void createTmpDir() throws IOException
    {
	if (tmpDir.exists()) {
	    tmpDir.delete();
	}
	tmpDir.mkdir();
    }

}
