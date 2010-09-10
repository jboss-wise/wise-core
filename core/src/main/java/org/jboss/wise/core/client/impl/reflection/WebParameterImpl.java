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

import java.lang.reflect.Type;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import net.jcip.annotations.Immutable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jboss.wise.core.client.WebParameter;

/**
 * Holds single parameter's data required for an invocation
 * 
 * @author stefano.maestri@javalinux.it
 */
@Immutable
public class WebParameterImpl implements WebParameter {

    private final Type type;

    private final String name;

    private final int position;

    private final Enum<WebParam.Mode> mode;

    /**
     * @param type
     * @param name
     * @param position
     * @param mode
     */
    public WebParameterImpl( Type type,
                             String name,
                             int position,
                             Enum<Mode> mode ) {
        super();
        this.type = type;
        this.name = name;
        this.position = position;
        this.mode = mode;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.WebParameter#getMode()
     */
    public Enum<Mode> getMode() {
        return mode;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.WebParameter#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.WebParameter#getPosition()
     */
    public int getPosition() {
        return position;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.WebParameter#getType()
     */
    public Type getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

}
