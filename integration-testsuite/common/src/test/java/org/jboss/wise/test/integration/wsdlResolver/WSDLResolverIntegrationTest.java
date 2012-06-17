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
package org.jboss.wise.test.integration.wsdlResolver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.jcip.annotations.Immutable;

import org.jboss.wise.core.client.impl.wsdlResolver.Connection;
import org.jboss.wise.core.client.impl.wsdlResolver.WSDLResolver;
import org.jboss.wise.core.test.WiseTest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * @author alessio.soldano@jboss.com
 * @since 13-May-2009
 */
@Immutable
public class WSDLResolverIntegrationTest extends WiseTest {
    
    private static final File tmpDir = new File("target/temp/WSDLResolverTest");
    private static URL warUrl = null;
    
    @Test
    public void shouldSaveRemoteSimpleWsdl() throws Exception {
        performTest(tmpDir.getAbsolutePath() + File.separator + "RemoteSimpleWsdl", new URL(getServerHostAndPort() + "/wsdlResolver/SimpleWsdl?wsdl"), false);
    }
    
    @Test
    public void shouldSaveRemoteWsdlWithWsdlImport() throws Exception {
        performTest(tmpDir.getAbsolutePath() + File.separator + "RemoteWsdlWithWsdlImport", new URL(getServerHostAndPort() + "/wsdlResolver/WsdlImport?wsdl"), false);
    }
    
    @Test
    public void shouldSaveRemoteWsdlWithSchemaImport() throws Exception {
        performTest(tmpDir.getAbsolutePath() + File.separator + "RemoteWsdlWithSchemaImport", new URL(getServerHostAndPort() + "/wsdlResolver/SchemaImport?wsdl"), false);
    }
    
    @Test
    public void shouldSaveRemoteWsdlWithSchemaAndWsdlImports() throws Exception {
        performTest(tmpDir.getAbsolutePath() + File.separator + "RemoteWsdlWithSchemaAndWsdlImport", new URL(getServerHostAndPort() + "/wsdlResolver/SchemaAndWsdlImport?wsdl"), false);
    }
    
    @Test
    public void shouldSaveProtectedRemoteSimpleWsdl() throws Exception {
        performTest(tmpDir.getAbsolutePath() + File.separator + "ProtectedRemoteSimpleWsdl", new URL(getServerHostAndPort() + "/wsdlResolver/ProtectedSimpleWsdl?wsdl"), true);
    }
    
    @Test
    public void shouldSaveProtectedRemoteWsdlWithWsdlImport() throws Exception {
        performTest(tmpDir.getAbsolutePath() + File.separator + "ProtectedRemoteWsdlWithWsdlImport", new URL(getServerHostAndPort() + "/wsdlResolver/ProtectedWsdlImport?wsdl"), true);
    }
    
    @Test
    public void shouldSaveProtectedRemoteWsdlWithSchemaImport() throws Exception {
        performTest(tmpDir.getAbsolutePath() + File.separator + "ProtectedRemoteWsdlWithSchemaImport", new URL(getServerHostAndPort() + "/wsdlResolver/ProtectedSchemaImport?wsdl"), true);
    }
    
    @Test
    public void shouldSaveProtectedRemoteWsdlWithSchemaAndWsdlImports() throws Exception {
        performTest(tmpDir.getAbsolutePath() + File.separator + "ProtectedRemoteWsdlWithSchemaAndWsdlImport", new URL(getServerHostAndPort() + "/wsdlResolver/ProtectedSchemaAndWsdlImport?wsdl"), true);
    }
    
    private void performTest(String testTempDir, URL wsdlURL, boolean auth) throws Exception {
        WSDLResolver resolver = auth ? new WSDLResolver(testTempDir, new Connection("kermit", "thefrog")) : new WSDLResolver(testTempDir);
        File wsdlFile = resolver.retrieveWsdlFile(wsdlURL);
        assertTrue(wsdlFile.exists());
        assertTrue(wsdlFile.length() > 0);
    }
    
    @BeforeClass
    public static void setUp() throws Exception {
	final ClassLoader classloader = WSDLResolverIntegrationTest.class.getClassLoader();
	Map<String, String> authenticationOptions = new HashMap<String, String>();
	authenticationOptions.put("usersProperties", classloader.getResource("org/jboss/wise/test/integration/wsdlResolver/jbossws-users.properties").toString());
	authenticationOptions.put("rolesProperties", classloader.getResource("org/jboss/wise/test/integration/wsdlResolver/jbossws-roles.properties").toString());
	authenticationOptions.put("unauthenticatedIdentity", "anonymous");
	addSecurityDomain("JBossWS", authenticationOptions);
	warUrl = classloader.getResource("wsdlResolver.war");
	deployWS(warUrl);
    }
    
    @Before
    public void createTmpDir() throws IOException
    {
	System.out.println("Creating tmp dir: " + tmpDir.getPath());
	if (tmpDir.exists()) {
	    tmpDir.delete();
	}
	tmpDir.mkdir();
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
	try {
	    undeployWS(warUrl);
	    removeSecurityDomain("JBossWS");
	} finally {
	    warUrl = null;
	}
    }

}
