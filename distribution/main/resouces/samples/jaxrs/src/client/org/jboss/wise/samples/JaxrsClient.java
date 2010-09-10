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

package org.jboss.wise.samples;

import java.net.ConnectException;
import java.io.InputStream;

import org.jboss.wise.core.client.InvocationResult;
import org.jboss.wise.core.client.jaxrs.RSDynamicClient;
import org.jboss.wise.core.client.factories.WSDynamicClientFactory;
import org.jboss.wise.core.exception.InvocationException;
import org.jboss.wise.core.exception.MCKernelUnavailableException;
import org.jboss.wise.core.exception.MappingException;
import org.jboss.wise.core.exception.WiseRuntimeException;

public class JaxrsClient {

	public static void main(String[] args) {
		try {

			// Sent HTTP GET request to query customer info
			System.out.println("Sent HTTP GET request to query customer info");
			RSDynamicClient client = WSDynamicClientFactory.getInstance().getJAXRSClient(
							"http://localhost:9000/customerservice/customers/123",
							RSDynamicClient.HttpMethod.GET, null,
							"application/xml");
			InvocationResult result = client.invoke();
			String response = (String) result.getResult().get(InvocationResult.RESPONSE);
			System.out.println(response);

			// Sent HTTP GET request to query sub resource product info
			System.out.println("\n");
			System.out.println("Sent HTTP GET request to query sub resource product info");
			client = WSDynamicClientFactory.getInstance().getJAXRSClient(
							"http://localhost:9000/customerservice/orders/223/products/323",
							RSDynamicClient.HttpMethod.GET, null,
							"application/xml");
			result = client.invoke();
			response = (String) result.getResult().get(InvocationResult.RESPONSE);
			System.out.println(response);

			// Sent HTTP PUT request to update customer info
			System.out.println("\n");
			System.out.println("Sent HTTP PUT request to update customer info");
			client = WSDynamicClientFactory.getInstance().getJAXRSClient(
					"http://localhost:9000/customerservice/customers",
					RSDynamicClient.HttpMethod.PUT, "application/xml",
					"application/xml");
			JaxrsClient jaxrsClient = new JaxrsClient();
			InputStream request = jaxrsClient.getClass().getResourceAsStream("resources/update_customer.xml");
			result = client.invoke(request, null);
			response = (String) result.getResult().get(InvocationResult.RESPONSE);
			int statusCode = ((Integer) result.getResult().get(InvocationResult.STATUS)).intValue();
			System.out.println("Response status code: " + statusCode);
			System.out.println("Response body: ");
			System.out.println(response);

			// Sent HTTP POST request to add customer
			System.out.println("\n");
			System.out.println("Sent HTTP POST request to add customer");
			client = WSDynamicClientFactory.getInstance().getJAXRSClient(
					"http://localhost:9000/customerservice/customers",
					RSDynamicClient.HttpMethod.POST, "application/xml",
					"application/xml");
			request = jaxrsClient.getClass().getResourceAsStream("resources/add_customer.xml");
			result = client.invoke(request, null);
			response = (String) result.getResult().get(InvocationResult.RESPONSE);
			statusCode = ((Integer) result.getResult().get(InvocationResult.STATUS)).intValue();
			System.out.println("Response status code: " + statusCode);
			System.out.println("Response body: ");
			System.out.println(response);


			System.out.println("\n");
			System.exit(0);

		} catch (WiseRuntimeException e) {
			e.printStackTrace();
		} catch (MCKernelUnavailableException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

}
