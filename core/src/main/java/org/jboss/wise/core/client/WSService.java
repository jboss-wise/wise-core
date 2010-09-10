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

package org.jboss.wise.core.client;

import java.net.URLClassLoader;
import java.util.Map;
import net.jcip.annotations.ThreadSafe;

/**
 * It represents a sigle service. I can be used to retrieve endpoints (Ports) of this services.
 * 
 * @author Stefano Maestri, stefano.maestri@javalinux.it
 */
@ThreadSafe
public interface WSService {

    /**
     * Create the endpoints' map and gives it back.
     * 
     * @return The Map of WSEndpoint with symbolic names as keys
     */
    public Map<String, WSEndpoint> processEndpoints();

    /**
     * @return The classLoader used to load generated class.
     */
    public URLClassLoader getClassLoader();

}
