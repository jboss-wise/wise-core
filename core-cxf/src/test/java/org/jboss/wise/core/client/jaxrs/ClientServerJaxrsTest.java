package org.jboss.wise.core.client.jaxrs;

import java.io.InputStream;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.testutil.common.AbstractClientServerTestBase;
import org.apache.log4j.Logger;
import org.jboss.wise.core.client.InvocationResult;
import org.jboss.wise.core.client.factories.WSDynamicClientFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class ClientServerJaxrsTest extends AbstractClientServerTestBase {

    private Logger logger = Logger.getLogger(ClientServerJaxrsTest.class);
    
    @BeforeClass
    public static void startServers() throws Exception {
        assertTrue("server did not launch correctly", launchServer(JaxrsServer.class));
    }

    @Test
    public void testGetBook() throws Exception {
        RSDynamicClient client = WSDynamicClientFactory.getJAXRSClient("http://localhost:9080/bookstore/books/123",
                                                                       RSDynamicClient.HttpMethod.GET,
                                                                       null,
                                                                       "application/xml");
        InvocationResult result = client.invoke();
        String response = (String)result.getResult().get(InvocationResult.RESPONSE);

        String expected = getStringFromInputStream(getClass().getResourceAsStream("/jaxrs/expected_get_book123.txt"));

        assertEquals(response, expected);
    }

    @Test
    public void testAddBook() throws Exception {
        RSDynamicClient client = WSDynamicClientFactory.getJAXRSClient("http://localhost:9080/bookstore/books",
                                                                       RSDynamicClient.HttpMethod.POST,
                                                                       "application/xml",
                                                                       "application/xml");

        InputStream request = getClass().getResourceAsStream("/jaxrs/add_book.txt");
        InvocationResult result = client.invoke(request, null);

        String response = (String)result.getResult().get(InvocationResult.RESPONSE);
        logger.debug("-------------" + response);

        String expected = getStringFromInputStream(getClass().getResourceAsStream("/jaxrs/expected_add_book.txt"));

        assertEquals(response, expected);
    }

    @Test
    public void testUpdateBook() throws Exception {
        RSDynamicClient client = WSDynamicClientFactory.getJAXRSClient("http://localhost:9080/bookstore/books",
                                                                       RSDynamicClient.HttpMethod.PUT,
                                                                       "application/xml",
                                                                       "application/xml");

        InputStream request = getClass().getResourceAsStream("/jaxrs/update_book.txt");
        InvocationResult result = client.invoke(request, null);

        String response = (String)result.getResult().get(InvocationResult.RESPONSE);
        int statusCode = ((Integer)result.getResult().get(InvocationResult.STATUS)).intValue();
        assertEquals(200, statusCode);

        // verify result
        client = WSDynamicClientFactory.getJAXRSClient("http://localhost:9080/bookstore/books/123",
                                                       RSDynamicClient.HttpMethod.GET,
                                                       null,
                                                       "application/xml");
        result = client.invoke();
        response = (String)result.getResult().get(InvocationResult.RESPONSE);

        String expected = getStringFromInputStream(getClass().getResourceAsStream("/jaxrs/expected_update_book.txt"));

        assertEquals(response, expected);

        // Roll back changes:
        client = WSDynamicClientFactory.getJAXRSClient("http://localhost:9080/bookstore/books",
                                                       RSDynamicClient.HttpMethod.PUT,
                                                       "application/xml",
                                                       "application/xml");
        request = getClass().getResourceAsStream("/jaxrs/expected_get_book123.txt");
        result = client.invoke(request, null);
    }

    @Test
    public void testDeleteBook() throws Exception {
        RSDynamicClient client = WSDynamicClientFactory.getJAXRSClient("http://localhost:9080/bookstore/books/123",
                                                                       RSDynamicClient.HttpMethod.DELETE,
                                                                       "application/xml",
                                                                       "application/xml");

        InvocationResult result = client.invoke();
        int statusCode = ((Integer)result.getResult().get(InvocationResult.STATUS)).intValue();
        assertEquals(200, statusCode);
    }

    private String getStringFromInputStream( InputStream in ) throws Exception {
        CachedOutputStream bos = new CachedOutputStream();
        IOUtils.copy(in, bos);
        in.close();
        bos.close();
        return bos.getOut().toString();
    }

}
