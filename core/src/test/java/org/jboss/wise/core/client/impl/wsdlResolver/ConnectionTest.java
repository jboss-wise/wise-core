/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

import sun.misc.BASE64Encoder;
import org.jboss.wise.core.client.impl.reflection.builder.ReflectionBasedWSDynamicClientBuilder;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.junit.Test;

/**
 * Class to test {@link Connection}
 * 
 * @author stefano.maestri@javalinux.it
 * @author alessio.soldano@jboss.com
 */
public class ConnectionTest {
    
    private Connection connection = new Connection();
    
    @Test
    public void userNameAndPasswordForBasicAuthenticationShouldReturnNullForNullUserOrPassword() throws Exception {
	connection.setUsername(null);
	connection.setPassword("password");
        assertThat(connection.getUserNameAndPasswordForBasicAuthentication(), is((String)null));
        connection.setUsername("user");
        connection.setPassword(null);
        assertThat(connection.getUserNameAndPasswordForBasicAuthentication(), is((String)null));
    }

    @Test
    public void userNameAndPasswordForBasicAuthenticationShouldReturnValidBaseAuthEncoding() throws Exception {
	connection.setUsername("username");
	connection.setPassword("password");
        assertThat(connection.getUserNameAndPasswordForBasicAuthentication(), is("username:password"));
    }
    
    @Test( expected = WiseRuntimeException.class )
    public void getWsdlInputStreamShouldThrowConnectExceptionIfHttpResultIsnt200() throws Exception {
        HttpURLConnection conn = mock(HttpURLConnection.class);
        when(conn.getResponseCode()).thenReturn(401);
        connection.getInputStream(conn);
    }

    @Test
    public void getWsdlInputStreamShouldReturnInputStreamIfHttpResultIs200() throws Exception {
        HttpURLConnection conn = mock(HttpURLConnection.class);
        when(conn.getResponseCode()).thenReturn(200);
        InputStream stream = mock(InputStream.class);
        when(conn.getInputStream()).thenReturn(stream);
        assertThat(connection.getInputStream(conn), is(stream));
    }

    @Test
    public void initConnectionShouldReturnWellInitializedConnectionWithUserNameAndPassword() throws Exception {
        HttpURLConnection conn = mock(HttpURLConnection.class);
        connection.setUsername("username");
        connection.setPassword("password");
        connection.initConnection(conn);
        verify(conn).setDoOutput(false);
        verify(conn).setDoInput(true);
        verify(conn).setUseCaches(false);
        verify(conn).setRequestMethod("GET");
        verify(conn).setRequestProperty("Accept",
                                        "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
        verify(conn).setRequestProperty("Connection", "close");
        verify(conn).setRequestProperty("Authorization", "Basic " + (new BASE64Encoder()).encode("username:password".getBytes()));
    }

    @Test
    public void initConnectionShouldReturnWellInitializedConnectionWithEmptyUserNameAndPassword() throws Exception {
        HttpURLConnection conn = mock(HttpURLConnection.class);
        connection.setUsername("");
        connection.setPassword("");
        connection.initConnection(conn);
        verify(conn).setDoOutput(false);
        verify(conn).setDoInput(true);
        verify(conn).setUseCaches(false);
        verify(conn).setRequestMethod("GET");
        verify(conn).setRequestProperty("Accept",
                                        "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
        verify(conn).setRequestProperty("Connection", "close");
    }

    @Test
    public void initConnectionShouldReturnWellInitializedConnectionWithoutUserNameAndPassword() throws Exception {
        HttpURLConnection conn = mock(HttpURLConnection.class);
        connection.initConnection(conn);
        verify(conn).setDoOutput(false);
        verify(conn).setDoInput(true);
        verify(conn).setUseCaches(false);
        verify(conn).setRequestMethod("GET");
        verify(conn).setRequestProperty("Accept",
                                        "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
        verify(conn).setRequestProperty("Connection", "close");
    }

    @Test( expected = WiseRuntimeException.class )
    public void initConnectionShouldThrowWiseRuntimeExceptionWhenGotException() throws Exception {
        HttpURLConnection conn = mock(HttpURLConnection.class);
        doThrow(new ProtocolException()).when(conn).setRequestMethod(anyString());
        connection.initConnection(conn);
    }
    
}
