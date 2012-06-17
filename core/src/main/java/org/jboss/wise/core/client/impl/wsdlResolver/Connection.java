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
package org.jboss.wise.core.client.impl.wsdlResolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.ws.common.utils.JarUrlConnection;

/**
 * 
 * @author alessio.soldano@jboss.com
 * @since 13-May-2009
 * 
 */
public class Connection {

    private String username;

    private String password;

    Connection() {
	// nothing to do
    }

    public Connection(String username, String password) {
	setUsername(username);
	setPassword(password);
    }

    /**
     * Open an inputStream from the given URL.
     * 
     * @param url
     * @return the inputStream of this url
     * @throws IOException
     * 
     */
    public InputStream open(URL url) throws IOException {
	String protocol = url.getProtocol();
	if ("http".equalsIgnoreCase(protocol)) {
	    return getInputStream(openAndInitConnection(url));
	} else if ("https".equalsIgnoreCase(protocol)) {
	    throw new WiseRuntimeException("https not supported yet");
	} else if ("jar".equalsIgnoreCase(protocol)) {
	    return new JarUrlConnection(url).getInputStream();
	} else {
	    return url.openStream();
	}
    }

    HttpURLConnection openAndInitConnection(URL url) throws WiseRuntimeException {
	HttpURLConnection conn;
	try {
	    conn = (HttpURLConnection) url.openConnection();
	} catch (IOException e) {
	    throw new WiseRuntimeException(e);
	}
	return this.initConnection(conn);
    }

    HttpURLConnection initConnection(HttpURLConnection conn) throws WiseRuntimeException {
	try {

	    conn.setDoOutput(false);
	    conn.setDoInput(true);
	    conn.setUseCaches(false);
	    conn.setRequestMethod("GET");
	    conn.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
	    // set Connection close, otherwise we get a keep-alive
	    // connection that gives us fragmented answers.
	    conn.setRequestProperty("Connection", "close");
	    // BASIC AUTH
	    String usernamePassword = getUserNameAndPasswordForBasicAuthentication();
	    if (usernamePassword != null && usernamePassword.length() != 0) {
		conn.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64(usernamePassword.getBytes())));
	    }
	    return conn;
	} catch (Exception e) {
	    throw new WiseRuntimeException(e);
	}
    }

    InputStream getInputStream(HttpURLConnection conn) throws WiseRuntimeException {
	try {
	    InputStream is = null;
	    if (conn.getResponseCode() == 200) {
		is = conn.getInputStream();
	    } else {

		throw new ConnectException("Remote server's response is an error: " + conn.getResponseCode());
	    }
	    return is;
	} catch (Exception e) {
	    throw new WiseRuntimeException(e);
	}
    }

    public static boolean isLocalAddress(String addr) throws IllegalArgumentException {
	try {
	    URL url = new URL(addr);
	    return ("file".equals(url.getProtocol()));
	} catch (Exception e) {
	    throw new IllegalArgumentException("Cannot process provided address: " + addr, e);
	}
    }

    void setUsername(String username) {
	this.username = username;
    }

    void setPassword(String password) {
	this.password = password;
    }

    String getUserNameAndPasswordForBasicAuthentication() {
	// BASIC Auth support; further auth not supported yet
	if (StringUtils.trimToNull(username) != null && StringUtils.trimToNull(password) != null) {
	    return new StringBuffer(username).append(":").append(password).toString();
	} else {
	    return null;
	}
    }

}
