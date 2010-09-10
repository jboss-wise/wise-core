package org.jboss.wise.core.client.jaxrs;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BookNotFoundDetails {
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
