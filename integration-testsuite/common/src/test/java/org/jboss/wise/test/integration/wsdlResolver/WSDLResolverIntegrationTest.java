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
package org.jboss.wise.test.integration.wsdlResolver;

import net.jcip.annotations.Immutable;
import org.jboss.logging.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.wise.core.client.impl.wsdlResolver.Connection;
import org.jboss.wise.core.client.impl.wsdlResolver.WSDLResolver;
import org.jboss.wise.core.test.WiseTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * @author alessio.soldano@jboss.com
 * @since 13-May-2009
 */
@RunWith(Arquillian.class)
@Immutable
public class WSDLResolverIntegrationTest extends WiseTest {

    private static final File tmpDir = new File("target/temp/WSDLResolverTest");

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "wsdlResolver.war");
        archive.addClass(org.jboss.wise.test.integration.wsdlResolver.ProtectedSchemaAndWsdlImportProvider.class)
                .addClass(org.jboss.wise.test.integration.wsdlResolver.ProtectedSchemaImportProvider.class)
                .addClass(org.jboss.wise.test.integration.wsdlResolver.ProtectedSimpleWsdlProvider.class)
                .addClass(org.jboss.wise.test.integration.wsdlResolver.ProtectedWsdlImportProvider.class)
                .addClass(org.jboss.wise.test.integration.wsdlResolver.SchemaAndWsdlImportProvider.class)
                .addClass(org.jboss.wise.test.integration.wsdlResolver.SchemaImportProvider.class)
                .addClass(org.jboss.wise.test.integration.wsdlResolver.SimpleWsdlProvider.class)
                .addClass(org.jboss.wise.test.integration.wsdlResolver.WsdlImportProvider.class)
                .addAsWebInfResource(
                        new File(getTestResourcesDir() + "/WEB-INF/wsdlResolver/wsdl/SchemaAndWsdlImport/AnotherSchema.xsd"),
                        "/wsdl/SchemaAndWsdlImport/AnotherSchema.xsd")
                .addAsWebInfResource(
                        new File(getTestResourcesDir() + "/WEB-INF/wsdlResolver/wsdl/SchemaAndWsdlImport/Bindings.wsdl"),
                        "/wsdl/SchemaAndWsdlImport/Bindings.wsdl")
                .addAsWebInfResource(
                        new File(getTestResourcesDir() + "/WEB-INF/wsdlResolver/wsdl/SchemaAndWsdlImport/SchemaImport.xsd"),
                        "/wsdl/SchemaAndWsdlImport/SchemaImport.xsd")
                .addAsWebInfResource(
                        new File(getTestResourcesDir()
                                + "/WEB-INF/wsdlResolver/wsdl/SchemaAndWsdlImport/WsdlWithSchemaAndWsdlImport.wsdl"),
                        "/wsdl/SchemaAndWsdlImport/WsdlWithSchemaAndWsdlImport.wsdl")
                .addAsWebInfResource(
                        new File(getTestResourcesDir() + "/WEB-INF/wsdlResolver/wsdl/SchemaAndWsdlImport/bar/BarImport.wsdl"),
                        "/wsdl/SchemaAndWsdlImport/bar/BarImport.wsdl")
                .addAsWebInfResource(
                        new File(getTestResourcesDir() + "/WEB-INF/wsdlResolver/wsdl/SchemaAndWsdlImport/bar/BarSchema.xsd"),
                        "/wsdl/SchemaAndWsdlImport/bar/BarSchema.xsd")
                .addAsWebInfResource(
                        new File(getTestResourcesDir()
                                + "/WEB-INF/wsdlResolver/wsdl/SchemaAndWsdlImport/foo/AdditionalSchema.xsd"),
                        "/wsdl/SchemaAndWsdlImport/foo/AdditionalSchema.xsd")
                .addAsWebInfResource(
                        new File(getTestResourcesDir()
                                + "/WEB-INF/wsdlResolver/wsdl/SchemaAndWsdlImport/foo/AnotherImport.wsdl"),
                        "/wsdl/SchemaAndWsdlImport/foo/AnotherImport.wsdl")
                .addAsWebInfResource(
                        new File(getTestResourcesDir() + "/WEB-INF/wsdlResolver/wsdl/SchemaImport/SchemaImport.xsd"),
                        "/wsdl/SchemaImport/SchemaImport.xsd")
                .addAsWebInfResource(
                        new File(getTestResourcesDir() + "/WEB-INF/wsdlResolver/wsdl/SchemaImport/WsdlWithSchemaImport.wsdl"),
                        "/wsdl/SchemaImport/WsdlWithSchemaImport.wsdl")
                .addAsWebInfResource(new File(getTestResourcesDir() + "/WEB-INF/wsdlResolver/wsdl/Simple/SimpleWsdl.wsdl"),
                        "/wsdl/Simple/SimpleWsdl.wsdl")
                .addAsWebInfResource(new File(getTestResourcesDir() + "/WEB-INF/wsdlResolver/wsdl/WsdlImport/Bindings.wsdl"),
                        "/wsdl/WsdlImport/Bindings.wsdl")
                .addAsWebInfResource(
                        new File(getTestResourcesDir() + "/WEB-INF/wsdlResolver/wsdl/WsdlImport/WsdlWithWsdlImport.wsdl"),
                        "/wsdl/WsdlImport/WsdlWithWsdlImport.wsdl")
                .addAsWebInfResource(new File(getTestResourcesDir() + "/WEB-INF/wsdlResolver/jboss-web.xml"), "jboss-web.xml")
                .setWebXML(new File(getTestResourcesDir() + "/WEB-INF/wsdlResolver/web.xml"));

        return archive;
    }

    @Test
    @RunAsClient
    public void shouldSaveRemoteSimpleWsdl() throws Exception {
        createTmpDir();
        performTest(tmpDir.getAbsolutePath() + File.separator + "RemoteSimpleWsdl", new URL(getServerHostAndPort()
                + "/wsdlResolver/SimpleWsdl?wsdl"), false);
    }

    @Test
    @RunAsClient
    public void shouldSaveRemoteWsdlWithWsdlImport() throws Exception {
        createTmpDir();
        performTest(tmpDir.getAbsolutePath() + File.separator + "RemoteWsdlWithWsdlImport", new URL(getServerHostAndPort()
                + "/wsdlResolver/WsdlImport?wsdl"), false);
    }

    @Test
    @RunAsClient
    public void shouldSaveRemoteWsdlWithSchemaImport() throws Exception {
        createTmpDir();
        performTest(tmpDir.getAbsolutePath() + File.separator + "RemoteWsdlWithSchemaImport", new URL(getServerHostAndPort()
                + "/wsdlResolver/SchemaImport?wsdl"), false);
    }

    @Test
    @RunAsClient
    public void shouldSaveRemoteWsdlWithSchemaAndWsdlImports() throws Exception {
        createTmpDir();
        performTest(tmpDir.getAbsolutePath() + File.separator + "RemoteWsdlWithSchemaAndWsdlImport", new URL(
                getServerHostAndPort() + "/wsdlResolver/SchemaAndWsdlImport?wsdl"), false);
    }

    @Test
    @RunAsClient
    public void shouldSaveProtectedRemoteSimpleWsdl() throws Exception {
        createTmpDir();
        performTest(tmpDir.getAbsolutePath() + File.separator + "ProtectedRemoteSimpleWsdl", new URL(getServerHostAndPort()
                + "/wsdlResolver/ProtectedSimpleWsdl?wsdl"), true);
    }

    @Test
    @RunAsClient
    public void shouldSaveProtectedRemoteWsdlWithWsdlImport() throws Exception {
        createTmpDir();
        performTest(tmpDir.getAbsolutePath() + File.separator + "ProtectedRemoteWsdlWithWsdlImport", new URL(
                getServerHostAndPort() + "/wsdlResolver/ProtectedWsdlImport?wsdl"), true);
    }

    @Test
    @RunAsClient
    public void shouldSaveProtectedRemoteWsdlWithSchemaImport() throws Exception {
        createTmpDir();
        performTest(tmpDir.getAbsolutePath() + File.separator + "ProtectedRemoteWsdlWithSchemaImport", new URL(
                getServerHostAndPort() + "/wsdlResolver/ProtectedSchemaImport?wsdl"), true);
    }

    @Test
    @RunAsClient
    public void shouldSaveProtectedRemoteWsdlWithSchemaAndWsdlImports() throws Exception {
        createTmpDir();
        performTest(tmpDir.getAbsolutePath() + File.separator + "ProtectedRemoteWsdlWithSchemaAndWsdlImport", new URL(
                getServerHostAndPort() + "/wsdlResolver/ProtectedSchemaAndWsdlImport?wsdl"), true);
    }

    private void performTest(String testTempDir, URL wsdlURL, boolean auth) throws Exception {
        WSDLResolver resolver = auth ? new WSDLResolver(testTempDir, new Connection("kermit", "thefrog")) : new WSDLResolver(
                testTempDir);
        File wsdlFile = resolver.retrieveWsdlFile(wsdlURL);
        assertTrue(wsdlFile.exists());
        assertTrue(wsdlFile.length() > 0);
    }

    public void createTmpDir() throws IOException {
        Logger.getLogger(this.getClass()).debug("Creating tmp dir: " + tmpDir.getPath());
        if (tmpDir.exists()) {
            tmpDir.delete();
        }
        tmpDir.mkdir();
    }
}
