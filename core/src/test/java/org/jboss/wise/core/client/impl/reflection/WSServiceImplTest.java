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

package org.jboss.wise.core.client.impl.reflection;

import static org.hamcrest.collection.IsCollectionContaining.hasItem;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import javax.xml.ws.WebEndpoint;
import net.jcip.annotations.Immutable;
import org.jboss.wise.core.client.WSEndpoint;
import org.jboss.wise.core.client.WSService;
import org.junit.Test;

/**
 * @author stefano.maestri@javalinux.it
 */
@Immutable
public class WSServiceImplTest {

    @WebEndpoint( name = "EndPoint1" )
    public String getEndPointForTest1() {
        return " ";
    }

    @WebEndpoint( name = "EndPoint2" )
    public Integer getEndPointForTes2() {
        return Integer.valueOf(3);
    }

    @Test
    public void shouldProcessEndPoint() throws Exception {
        URLClassLoader loader = new URLClassLoader(new URL[] {}, Thread.currentThread().getContextClassLoader());
        WSService service = new WSServiceImpl(WSServiceImplTest.class, loader, this, null, null, 10);
        Map<String, WSEndpoint> endpoints = service.processEndpoints();
        assertThat(endpoints.keySet(), hasItem("EndPoint1"));
        assertThat(endpoints.keySet(), hasItem("EndPoint2"));

        WSEndpoint endPoint1 = endpoints.get("EndPoint1");
        assertThat((URLClassLoader)endPoint1.getClassLoader(), is(loader));
        assertThat(endPoint1.getUnderlyingObjectClass().getCanonicalName(), equalTo(String.class.getCanonicalName()));
        assertThat((String)endPoint1.createInstance(), equalTo(" "));
        WSEndpoint endPoint2 = endpoints.get("EndPoint2");
        assertThat((URLClassLoader)endPoint2.getClassLoader(), is(loader));
        assertThat(endPoint2.getUnderlyingObjectClass().getCanonicalName(), equalTo(Integer.class.getCanonicalName()));
        assertThat((Integer)endPoint2.createInstance(), equalTo(Integer.valueOf(3)));

    }
}
