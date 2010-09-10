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

import java.lang.reflect.Type;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;

import net.jcip.annotations.Immutable;

/**
 * Holds single parameter's data required for an invocation
 * 
 * @author stefano.maestri@javalinux.it
 * @since 23-Aug-2007
 */
@Immutable
public interface WebParameter {

    /**
     * 
     * @return The name defined in the wsdl and {@link WebParam} annotation of
     *         generated classses
     */
    public String getName();

    /**
     * 
     * @return The {@link Type} defined in generated classses
     */
    public Type getType();

    /**
     * 
     * @return the position (starting from zero) of the parameter in method
     *         signature
     */
    public int getPosition();

    /**
     * 
     * @return The {@link Mode} defined in the wsdl and {@link WebParam}
     *         annotation of generated classses
     */
    public Enum<WebParam.Mode> getMode();

}
