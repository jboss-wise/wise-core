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
package org.jboss.wise.test.integration.incontainer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.wise.core.client.InvocationResult;
import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.client.factories.WSDynamicClientFactory;
import org.jboss.ws.api.tools.WSContractConsumer;

public class HelloWorldServlet extends HttpServlet {
    @Override
    public void doGet( HttpServletRequest theRequest,
                       HttpServletResponse theResponse ) throws IOException {
        PrintWriter pw = theResponse.getWriter();
        String jbwsVersion = WSContractConsumer.newInstance().getClass().getPackage().getImplementationVersion();
        if (jbwsVersion.startsWith("4.0") || jbwsVersion.startsWith("4.1.0") || jbwsVersion.startsWith("4.1.1") ) {
            pw.print("[FIXME][JBWS-3589] This test is meant to be run properly on JBossWS 4.1.2.Final or greater");
        } else {
            String name = theRequest.getParameter("name");
            pw.print("WS return: " + invokeWS(name));
        }
    }

    @SuppressWarnings("unchecked")
    public Object invokeWS(String name) {
	WSDynamicClient client = null;
	try {
	    URL wsdlURL = new URL("http://127.0.0.1:8080/basic/HelloWorld?wsdl");

	    WSDynamicClientBuilder clientBuilder = WSDynamicClientFactory.getJAXWSClientBuilder();
	    client = clientBuilder.verbose(true).keepSource(true).wsdlURL(wsdlURL.toString()).build();
	    WSMethod method = client.getWSMethod("HelloService", "HelloWorldBeanPort", "echo");
	    Map<String, Object> args = new java.util.HashMap<String, Object>();
	    args.put("arg0", name);
	    InvocationResult result = method.invoke(args, null);
	    Map<String, Object> res = result.getMapRequestAndResult(null, null);
	    Map<String, Object> test = (Map<String, Object>) res.get("results");
	    client.close();
	    return test.get("result");
	} catch (Exception e) {
	    throw new RuntimeException(e);
	} finally {
	    if (client != null) {
		client.close();
	    }
	}
    }
}
