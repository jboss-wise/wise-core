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

import net.jcip.annotations.ThreadSafe;

import org.jboss.wise.core.wsextensions.EnablerDelegate;
import org.milyn.Smooks;

/**
 * This is the Wise core class responsible to invoke the JAX-WS tools that handles wsdl retrieval &amp; parsing. It is used to
 * build the list of WSService representing the services available in parsed wsdl.
 *
 * @author Stefano Maestri, stefano.maestri@javalinux.it
 */
@ThreadSafe
public interface WSDynamicClient extends BasicWSDynamicClient {

    /**
     *
     * @return the {@link EnablerDelegate} used to enable the WS-* for all endpoint attached all serivices attached to this
     *         {@link WSDynamicClient}.
     */
    EnablerDelegate getWSExtensionEnablerDelegate();

    /**
     *
     * @return the single smooks instance attached to this {@link WSDynamicClient}
     */
    Smooks getSmooksInstance();

}
