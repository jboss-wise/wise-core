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

package org.jboss.wise.core.client.impl.reflection;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.spy;
import static org.hamcrest.collection.IsCollectionContaining.hasItem;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.ThreadSafe;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.client.WSService;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.client.impl.reflection.builder.ReflectionBasedWSDynamicClientBuilder;
import org.jboss.wise.core.consumer.WSConsumer;
import org.jboss.wise.core.exception.ResourceNotAvailableException;
import org.junit.Before;
import org.junit.Test;
import org.milyn.Smooks;

/**
 * This is the Wise core, i.e. the JAX-WS client that handles wsdl retrieval & parsing, invocations, etc.
 * 
 * @author Stefano Maestri, stefano.maestri@javalinux.it
 * @since
 */
@ThreadSafe
public class WSDynamicClientImplTest {

    WSDynamicClientBuilder builder;

    @Before
    public void before() {
        WSDynamicClientBuilder realBuilder = new ReflectionBasedWSDynamicClientBuilder();
        realBuilder.wsdlURL("foo").tmpDir("target/temp").targetPackage("org.jboss.wise.test.mocks").maxThreadPoolSize(10);
        builder = spy(realBuilder);
        when(builder.getClientSpecificTmpDir()).thenReturn("target/temp/foo");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldInitClassLoader() throws Exception {
        WSConsumer consumerMock = mock(WSConsumer.class);

        when(consumerMock.importObjectFromWsdl(anyString(),
                                               (File)anyObject(),
                                               (File)anyObject(),
                                               anyString(),
                                               (List<File>)anyObject(),
                                               (PrintStream)anyObject(),
                                               (File)anyObject())).thenReturn(new LinkedList<String>());
        WSDynamicClientImpl client = new WSDynamicClientImpl(builder, consumerMock);
        File expectedOutPutDir = new File("target/temp/foo/classes");
        assertThat(client.getClassLoader().getURLs().length, is(1));
        assertThat(client.getClassLoader().getURLs()[0], equalTo(expectedOutPutDir.toURI().toURL()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldProcessServices() throws Exception {
        WSConsumer consumerMock = mock(WSConsumer.class);
        String[] classes = {"org.jboss.wise.test.mocks.Service1", "org.jboss.wise.test.mocks.Service2"};

        when(consumerMock.importObjectFromWsdl(anyString(),
                                               (File)anyObject(),
                                               (File)anyObject(),
                                               anyString(),
                                               (List<File>)anyObject(),
                                               (PrintStream)anyObject(),
                                               (File)anyObject())).thenReturn(Arrays.asList(classes));
        WSDynamicClientImpl client = new WSDynamicClientImpl(builder, consumerMock);

        Map<String, WSService> services = client.processServices();
        assertThat(services.size(), is(2));
        assertThat(services.keySet(), hasItem("ServiceName1"));
        assertThat(services.keySet(), hasItem("ServiceName2"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetMethod() throws Exception {
        WSConsumer consumerMock = mock(WSConsumer.class);
        String[] classes = {"org.jboss.wise.test.mocks.Service1", "org.jboss.wise.test.mocks.Service2"};

        when(consumerMock.importObjectFromWsdl(anyString(),
                                               (File)anyObject(),
                                               (File)anyObject(),
                                               anyString(),
                                               (List<File>)anyObject(),
                                               (PrintStream)anyObject(),
                                               (File)anyObject())).thenReturn(Arrays.asList(classes));
        WSDynamicClientImpl client = new WSDynamicClientImpl(builder, consumerMock);

        WSMethod wsMethod = client.getWSMethod("ServiceName1", "Port1", "testMethod");
        assertNotNull("Should get WsMethod through getWSMethod api", wsMethod);
    }

    @SuppressWarnings("unchecked")
    @Test( expected = ResourceNotAvailableException.class )
    public void missingOperationShouldCaseExceptionThrownOnGetWSMethod() throws Exception {
        WSConsumer consumerMock = mock(WSConsumer.class);
        String[] classes = {"org.jboss.wise.test.mocks.Service1", "org.jboss.wise.test.mocks.Service2"};

        when(consumerMock.importObjectFromWsdl(anyString(),
                                               (File)anyObject(),
                                               (File)anyObject(),
                                               anyString(),
                                               (List<File>)anyObject(),
                                               (PrintStream)anyObject(),
                                               (File)anyObject())).thenReturn(Arrays.asList(classes));
        WSDynamicClientImpl client = new WSDynamicClientImpl(builder, consumerMock);
        client.getWSMethod("ServiceName1", "Port1", "testWrongMethod");
    }

    @SuppressWarnings("unchecked")
    @Test( expected = ResourceNotAvailableException.class )
    public void missingPortShouldCaseExceptionThrownOnGetWSMethod() throws Exception {
        WSConsumer consumerMock = mock(WSConsumer.class);
        String[] classes = {"org.jboss.wise.test.mocks.Service1", "org.jboss.wise.test.mocks.Service2"};

        when(consumerMock.importObjectFromWsdl(anyString(),
                                               (File)anyObject(),
                                               (File)anyObject(),
                                               anyString(),
                                               (List<File>)anyObject(),
                                               (PrintStream)anyObject(),
                                               (File)anyObject())).thenReturn(Arrays.asList(classes));
        WSDynamicClientImpl client = new WSDynamicClientImpl(builder, consumerMock);
        client.getWSMethod("ServiceName1", "Port2", "testMethod");
    }

    @SuppressWarnings("unchecked")
    @Test( expected = ResourceNotAvailableException.class )
    public void missingServiceShouldCaseExceptionThrownOnGetWSMethod() throws Exception {
        WSConsumer consumerMock = mock(WSConsumer.class);
        String[] classes = {"org.jboss.wise.test.mocks.Service1", "org.jboss.wise.test.mocks.Service2"};

        when(consumerMock.importObjectFromWsdl(anyString(),
                                               (File)anyObject(),
                                               (File)anyObject(),
                                               anyString(),
                                               (List<File>)anyObject(),
                                               (PrintStream)anyObject(),
                                               (File)anyObject())).thenReturn(Arrays.asList(classes));
        WSDynamicClientImpl client = new WSDynamicClientImpl(builder, consumerMock);
        client.getWSMethod("ServiceName5", "Port1", "testWrongMethod");
    }

    @Test
    public void shouldRemoveTmpDirAndCloseSMooksInvokingClose() {
        WSConsumer consumerMock = mock(WSConsumer.class);
        Smooks smook = mock(Smooks.class);

        WSDynamicClientImpl client = new WSDynamicClientImpl(builder, consumerMock, smook);
        String tmpDir = client.getTmpDir();
        client.close();
        verify(smook).close();
        assertThat(new File(tmpDir).exists(), is(false));

    }

}
