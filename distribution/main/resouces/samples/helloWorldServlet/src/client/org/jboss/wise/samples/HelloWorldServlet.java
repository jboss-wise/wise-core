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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jboss.wise.core.client.builder.WSDynamicClientBuilder;
import org.jboss.wise.core.client.InvocationResult;
import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.client.factories.WSDynamicClientFactory;

public class HelloWorldServlet extends HttpServlet {
    @Override
    public void doGet( HttpServletRequest theRequest,
                       HttpServletResponse theResponse ) throws IOException {
        PrintWriter pw = theResponse.getWriter();

        theResponse.setContentType("text/html");

        String name = theRequest.getParameter("NAME");

        pw.print("<html><head/><body><h1>WS return: " + invokeWS(name) + "</h1></body></html>");
    }

    public String invokeWS( String name ) {
	String returnString = null;
        try {
	    WSDynamicClientBuilder clientBuilder = WSDynamicClientFactory.getJAXWSClientBuilder();
	    WSDynamicClient client = clientBuilder.tmpDir("target/temp/wise").verbose(true).keepSource(true).wsdlURL("http://127.0.0.1:8080/HelloWorld/HelloWorldServletWS?wsdl").build();
	    WSMethod method = client.getWSMethod("HelloWorldServletWSService", "HelloWorldPort", "sayHello");

            HashMap<String, Object> requestMap = new HashMap<String, Object>();
            requestMap.put("toWhom", name);
            InvocationResult result = method.invoke(requestMap, null);
            returnString= result.getMapRequestAndResult(null,null).toString();
        } catch (Exception e) {
            e.printStackTrace();

        }

        return returnString;

    }
}
