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
package org.jboss.wise.core.client;

import java.util.List;
import java.util.Map;
import javax.xml.ws.handler.Handler;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.jboss.wise.core.wsextensions.WSExtensionEnabler;

/**
 * This represent a Endpoint(Port) and has utility methods to edit username, password, endpoint address, attach handlers
 *
 * @author Stefano Maestri, stefano.maestri@javalinux.it
 * @author <a href="ema@redhat.com">Jim Ma</a>
 */
@ThreadSafe
public interface WSEndpoint {

    /**
     * create the underlying instance of the endpoint generated class. Useful to create a thread pool invoking endpoint methods.
     *
     * @return the created underlying instance of the endpoint generated class
     */
    Object createInstance();

    /**
     * Set username for endpoint authentication
     *
     * @param username string
     */
    @GuardedBy("this")
    void setUsername(String username);

    /**
     * Set password for endpoint authentication
     *
     * @param password string
     */
    @GuardedBy("this")
    void setPassword(String password);

    @GuardedBy("this")
    Class<?> getUnderlyingObjectClass();

    /**
     *
     * @return endpoint name as defined in wsdl
     */
    @GuardedBy("this")
    String getName();

    /**
     * Add an Handler to this endpoint. Handler will apply on all endpoint method called
     *
     * @see #getWSMethods()
     * @param handler handler
     */
    @GuardedBy("this")
    void addHandler(Handler<?> handler);

    /**
     * Create the webmethods' map and it back. This maps would be used by clients to get a method to call and invoke it All
     * calls of this method apply all handlers added with {@link #addHandler(Handler)} method
     *
     * @return The list of WebMethod names
     */
    Map<String, WSMethod> getWSMethods();

    /**
     * @return classLoader used to load JAXWS generated object see also {@link #getUnderlyingObjectClass()}
     */
    ClassLoader getClassLoader();

    /**
     * Use this method to add WSExtension you would enable on this endpoint. Of course extension have to be enabled before you
     * cal method associated to action you are going to invoke. Not necessary before you build WSMethods object associated to
     * this endpoint {@link #getWSMethods()} see also {@link WSExtensionEnabler} for more information on how to enable
     * WSExtensions
     *
     * @param enabler it is an implementation of {@link WSExtensionEnabler}
     */
    @GuardedBy("this")
    void addWSExtension(WSExtensionEnabler enabler);

    /**
     * @return handlers added to handler chain of this endpoint.
     */
    @GuardedBy("this")
    List<Handler<?>> getHandlers();

    /**
     * @return extensions enabled on this endpoint
     */
    @GuardedBy("this")
    List<WSExtensionEnabler> getExtensions();

    /**
     *
     * @return the target url to invoke for this endpoint
     */
    @GuardedBy("this")
    String getTargetUrl();

    @GuardedBy("this")
    String getUsername();

    @GuardedBy("this")
    String getPassword();

    /**
     * it give the opportunity to change target url of the endpoint defined in the wsdl
     *
     * @param targetUrl string
     */
    @GuardedBy("this")
    void setTargetUrl(String targetUrl);

}
