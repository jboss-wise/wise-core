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
package org.jboss.wise.test.integration.wsaddressing;

import javax.jws.WebService;
import javax.xml.ws.soap.Addressing;

/**
 * 
 * @author alessio.soldano@jboss.com
 * @since 20-Dic-2010
 *
 */

@WebService( endpointInterface = "org.jboss.wise.test.integration.wsaddressing.Hello",
             targetNamespace = "http://org.jboss/wise/wsa",
             serviceName = "HelloService",
             wsdlLocation = "WEB-INF/test.wsdl" ) //this is added to force the server on publishing a WSDL without policies; this way we're sure WS-Addressing
             					  //on client side is controlled by features only, not by WS-Policy engine.
@Addressing(enabled = true, required = false)
public class HelloImpl implements Hello {
    
    public String echoUserType( String user ) {
        return "Hello WSAddressing";
    }
}
