package org.jboss.wise.core.client.jaxrs;

import javax.xml.ws.WebFault;

@WebFault
public class BookNotFoundFault extends Exception {
    private BookNotFoundDetails details;

    public BookNotFoundFault(BookNotFoundDetails details) {
        super();
        this.details = details;
    }

    public BookNotFoundDetails getFaultInfo() {
        return details;
    }
}
