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
package org.jboss.wise.core.wsextensions.impl.jbosswsnative;

import java.io.Serializable;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Contains configuration needed by jbossws-native to enable WS-Security Intended to be used by IOC (jboss-beans.xml) injected in
 * {@link ReflectionEnablerDelegate}
 * 
 * @author stefano.maestri@javalinux.it
 */
@ThreadSafe
public class NativeSecurityConfig implements Serializable {

    @GuardedBy( "this" )
    private String configFileURL;
    @GuardedBy( "this" )
    private String configName;

    public NativeSecurityConfig() {
        super();
    }

    /**
     * @param configFileURL
     * @param configName
     */
    public NativeSecurityConfig( String configFileURL,
                                 String configName ) {
        super();
        this.configFileURL = configFileURL;
        this.configName = configName;
    }

    /**
     * @return configFileURL
     */
    public final synchronized String getConfigFileURL() {
        return configFileURL;
    }

    /**
     * @param configFileURL Sets configFileURL to the specified value.
     */
    public final synchronized void setConfigFileURL( String configFileURL ) {
        this.configFileURL = configFileURL;
    }

    /**
     * @return configName
     */
    public final synchronized String getConfigName() {
        return configName;
    }

    /**
     * @param configName Sets configName to the specified value.
     */
    public final synchronized void setConfigName( String configName ) {
        this.configName = configName;
    }

}
