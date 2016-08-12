package org.jboss.wise.core.client.jaxrs;

import javax.xml.ws.WebFault;

@WebFault
public class BookNotFoundFault extends Exception {
    private static final long serialVersionUID = -4129900823842490013L;

    private BookNotFoundDetails details;

    public BookNotFoundFault(BookNotFoundDetails details) {
        super();
        this.details = details;
    }

    public BookNotFoundDetails getFaultInfo() {
        return details;
    }
}
