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
package org.jboss.wise.core.client.jaxrs.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.jcip.annotations.ThreadSafe;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.HttpRequestBase;
import org.jboss.wise.core.client.InvocationResult;
import org.jboss.wise.core.client.impl.reflection.InvocationResultImpl;
import org.jboss.wise.core.client.jaxrs.RSDynamicClient;
import org.jboss.wise.core.mapper.WiseMapper;
import org.jboss.logging.Logger;

/*
 * TODO:
 * 1. return headers
 * 2. Support MultipartRequestEntity
 * 3. Support HttpClient properties
 * 4. Support setting headers
 * 5. Return the result in formats other than InputStream
 * 6. Exception handling
 * 7. Using jax-rs providers
 */
@ThreadSafe
public class RSDynamicClientImpl implements RSDynamicClient {

    private static Logger log = Logger.getLogger(RSDynamicClientImpl.class);

    private final String resourceURI;
    private String user;
    private String password;
    private final String produceMediaTypes;
    private final String consumeMediaTypes;
    private final HttpMethod httpMethod;
    private final HttpClient httpClient;
    private Map<String, String> requestHeaders = new HashMap<String, String>();

    /**
     * Invoke JAXRS service.
     * 
     * @param resourceURI   string
     * @param produceMediaTypes default to "* / *"
     * @param consumeMediaTypes default to "* / *"
     * @param httpMethod    http method
     */
    public RSDynamicClientImpl( String resourceURI,
                                String produceMediaTypes,
                                String consumeMediaTypes,
                                HttpMethod httpMethod ) {
        this.resourceURI = resourceURI;
        this.produceMediaTypes = produceMediaTypes;
        this.consumeMediaTypes = consumeMediaTypes;
        this.httpMethod = httpMethod;

        this.httpClient = HttpClientBuilder.create().build();
    }

    public RSDynamicClientImpl( String resourceURI,
                                String produceMediaTypes,
                                String consumeMediaTypes,
                                HttpMethod httpMethod,
                                Map<String, String> requestHeaders ) {
        this.resourceURI = resourceURI;
        this.produceMediaTypes = produceMediaTypes;
        this.consumeMediaTypes = consumeMediaTypes;
        this.httpMethod = httpMethod;
        this.requestHeaders = requestHeaders;

        this.httpClient = HttpClientBuilder.create().build();
    }

    public String getResourceURI() {
        return resourceURI;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getProduceMediaTypes() {
        return produceMediaTypes;
    }

    public String getConsumeMediaTypes() {
        return consumeMediaTypes;
    }

    public InvocationResult invoke( Map<String, Object> inputObjects,
                                    WiseMapper mapper ) {
        // NOT SUPPORTED
        // transform inputObjects to String or InputStream using WiseMapper?
        return null;
    }

    public InvocationResult invoke( InputStream request,
                                    WiseMapper mapper ) {
        return invoke(new InputStreamEntity(request), mapper);
    }

    public InvocationResult invoke( String request,
                                    WiseMapper mapper ) {
        StringEntity requestEntity = null;
        requestEntity = new StringEntity(request, "UTF-8");
        requestEntity.setContentType(produceMediaTypes);
        return invoke(requestEntity, mapper);
    }

    public InvocationResult invoke( byte[] request,
                                    WiseMapper mapper ) {
        return invoke(new ByteArrayEntity(request), mapper);
    }

    public InvocationResult invoke( File request,
                                    WiseMapper mapper ) {
        return invoke(new FileEntity(request, ContentType.create(produceMediaTypes)), mapper);
    }

    public InvocationResult invoke() {
        HttpEntity requestEntity = null;
        return invoke(requestEntity, null);
    }

    public InvocationResult invoke( HttpEntity requestEntity,
                                    WiseMapper mapper ) {
        InvocationResult result = null;
        Map<String, Object> responseHolder = new HashMap<String, Object>();

        if (HttpMethod.GET == httpMethod) {
            HttpGet get = new HttpGet(resourceURI);
            setRequestHeaders(get);

            try {
                URL url = get.getURI().toURL();
                HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
                HttpResponse hResponse = httpClient.execute(targetHost, get);
                int statusCode = hResponse.getStatusLine().getStatusCode();
                // TODO: Use InputStream
                String response = getResponseBodyAsString(hResponse);
                responseHolder.put(InvocationResult.STATUS, Integer.valueOf(statusCode));

                result = new InvocationResultImpl(InvocationResult.RESPONSE, null, response, responseHolder);

                // System.out.print(response);
            } catch (IOException e) {
                // TODO:
            } finally {
                get.releaseConnection();
            }
        } else if (HttpMethod.POST == httpMethod) {
            HttpPost post = new HttpPost(resourceURI);
            setRequestHeaders(post);

            post.setEntity(requestEntity);

            try {
                URL url = post.getURI().toURL();
                HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
                HttpResponse hResponse = httpClient.execute(targetHost, post);
                int statusCode = hResponse.getStatusLine().getStatusCode();
                String response = getResponseBodyAsString(hResponse);
                responseHolder.put(InvocationResult.STATUS, Integer.valueOf(statusCode));

                result = new InvocationResultImpl(InvocationResult.RESPONSE, null, response, responseHolder);

                // System.out.print(response);
            } catch (IOException e) {
                // TODO:
            } finally {
                post.releaseConnection();
            }
        } else if (HttpMethod.PUT == httpMethod) {
            HttpPut put = new HttpPut(resourceURI);
            setRequestHeaders(put);

            put.setEntity(requestEntity);

            try {
                URL url = put.getURI().toURL();
                HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
                HttpResponse hResponse = httpClient.execute(targetHost, put);
                int statusCode = hResponse.getStatusLine().getStatusCode();
                String response = getResponseBodyAsString(hResponse);
                responseHolder.put(InvocationResult.STATUS, Integer.valueOf(statusCode));

                result = new InvocationResultImpl(InvocationResult.RESPONSE, null, response, responseHolder);

                // System.out.print(response);
            } catch (IOException e) {
                // TODO:
            } finally {
                put.releaseConnection();
            }
        } else if (HttpMethod.DELETE == httpMethod) {
            HttpDelete delete = new HttpDelete(resourceURI);
            setRequestHeaders(delete);

            try {
                URL url = delete.getURI().toURL();
                HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
                HttpResponse hResponse = httpClient.execute(targetHost, delete);
                int statusCode = hResponse.getStatusLine().getStatusCode();
                String response = getResponseBodyAsString(hResponse);
                responseHolder.put(InvocationResult.STATUS, Integer.valueOf(statusCode));

                result = new InvocationResultImpl(InvocationResult.RESPONSE, null, response, responseHolder);

                // System.out.print(response);
            } catch (IOException e) {
                // TODO:
            } finally {
                delete.releaseConnection();
            }
        }

        return result;
    }

    private void setRequestHeaders( HttpRequestBase method ) {
        if (produceMediaTypes != null) {
            method.addHeader("Content-Type", produceMediaTypes);
        }

        if (consumeMediaTypes != null) {
            method.addHeader("Accept", consumeMediaTypes);
        }

        for (String headerName : requestHeaders.keySet()) {
            String headerValue = requestHeaders.get(headerName);

            method.addHeader(headerName, headerValue);
        }
    }

    private String getResponseBodyAsString(HttpResponse httpResponse) {
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(httpResponse.getEntity().getContent()));
            String dataLine = null;

            while ((dataLine = reader.readLine()) != null) {
                buffer.append(dataLine);
            }
        } catch (IOException e) {
          log.error(e);
        }

        return buffer.toString();
    }

}
