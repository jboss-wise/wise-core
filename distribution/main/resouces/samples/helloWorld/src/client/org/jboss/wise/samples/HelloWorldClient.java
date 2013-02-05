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
package org.jboss.wise.samples;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import org.jboss.wise.core.client.InvocationResult;
import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.client.factories.WSDynamicClientFactory;
import org.jboss.wise.core.exception.InvocationException;
import org.jboss.wise.core.exception.MappingException;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.wise.core.handlers.LoggingHandler;

/**
 * @author stefano.maestri@javalinux.it
 */
public class HelloWorldClient {

    /**
     * @param args
     */
    public static void main(String[] args) {
	try {
	    WSDynamicClientBuilder clientBuilder = WSDynamicClientFactory.getJAXWSClientBuilder();
	    WSDynamicClient client = clientBuilder.tmpDir("target/temp/wise").verbose(true).keepSource(true)
		    .wsdlURL("http://127.0.0.1:8080/HelloWorld/HelloWorldWS?wsdl").build();
	    WSMethod method = client.getWSMethod("HelloWorldWSService", "HelloWorldPort", "sayHello");
	    method.getEndpoint().addHandler(new LoggingHandler());
	    HashMap<String, Object> requestMap = new HashMap<String, Object>();
	    requestMap.put("toWhom", "SpiderMan");
	    InvocationResult result = method.invoke(requestMap, null);
	    System.out.println(result.getMapRequestAndResult(null, null));
	    System.out.println(result.getMapRequestAndResult(null, requestMap));
	    client.close();

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

}
