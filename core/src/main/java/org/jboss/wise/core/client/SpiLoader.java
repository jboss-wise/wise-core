/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author stefano.maestri@javalinux.it
 * 
 */
public class SpiLoader {

    /**
     * This method uses the algorithm below
     * 
     * 1. If a system property with the name
     * org.jboss.wise.client.builder.WSDynamicClientBuilder is defined, then its
     * value is used as the name of the implementation class. If a system
     * property with the name org.jboss.wise.consumer.WSConsumer then its value
     * is used as the name of the implementation class
     * 
     * 2. If a resource with the name of
     * META-INF/services/org.jboss.wise.client.builder.WSDynamicClientBuilder
     * exists, then its first line, if present, is used as the UTF-8 encoded
     * name of the implementation class. If a resource with the name of
     * META-INF/services/org.jboss.wise.consumer.WSConsumer exists, then its
     * first line, if present, is used as the UTF-8 encoded name of the
     * implementation class.
     * 
     * 3. Finally, a default implementation class name is used.
     * 
     * @param propertyName
     * @param defaultFactory
     * @return the instance
     */
    public static Object loadService(String propertyName, String defaultFactory) {
	Object factory = loadFromSystemProperty(propertyName);

	if (factory == null) {
	    factory = loadFromServices(propertyName);
	}
	if (factory == null) {
	    factory = loadDefault(defaultFactory);
	}

	return factory;
    }

    /**
     * Use the Services API (as detailed in the JAR specification), if
     * available, to determine the classname.
     * 
     * @param propertyName
     * @return the instance
     */
    public static Object loadFromServices(String propertyName) {
	Object factory = null;
	String factoryName = null;
	ClassLoader loader = getContextClassLoader();

	// Use the Services API (as detailed in the JAR specification), if
	// available, to determine the classname.
	String filename = "META-INF/services/" + propertyName;
	InputStream inStream = loader.getResourceAsStream(filename);
	if (inStream != null) {
	    try {
		BufferedReader br = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
		factoryName = br.readLine();
		br.close();
		if (factoryName != null) {
		    Class<?> factoryClass = loader.loadClass(factoryName);
		    factory = factoryClass.newInstance();
		}
	    } catch (Throwable t) {
		throw new IllegalStateException("Failed to load " + propertyName + ": " + factoryName, t);
	    }
	}

	return factory;
    }

    /**
     * Use the system property
     * 
     * @param propertyName
     * @return the instance
     */
    public static Object loadFromSystemProperty(String propertyName) {
	Object factory = null;
	ClassLoader loader = getContextClassLoader();

	String factoryName = AccessController.doPrivileged(new PropertyAccessAction(propertyName));
	if (factoryName != null) {
	    try {
		Class<?> factoryClass = loader.loadClass(factoryName);
		factory = factoryClass.newInstance();
	    } catch (Throwable t) {
		throw new IllegalStateException("Failed to load " + propertyName + ": " + factoryName, t);
	    }
	}

	return factory;
    }

    private static Object loadDefault(String defaultFactory) {
	Object factory = null;
	ClassLoader loader = getContextClassLoader();

	// Use the default factory implementation class.
	if (defaultFactory != null) {
	    try {
		Class<?> factoryClass = loader.loadClass(defaultFactory);
		factory = factoryClass.newInstance();
	    } catch (Throwable t) {
		throw new IllegalStateException("Failed to load: " + defaultFactory, t);
	    }
	}

	return factory;
    }

    private static class PropertyAccessAction implements PrivilegedAction<String> {

	private final String name;

	PropertyAccessAction(String name) {
	    this.name = name;
	}

	public String run() {
	    return System.getProperty(name);
	}
    }
    
    private static ClassLoader getContextClassLoader()
    {
       SecurityManager sm = System.getSecurityManager();
       if (sm == null)
       {
          return Thread.currentThread().getContextClassLoader();
       }
       else
       {
          return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
             public ClassLoader run()
             {
                return Thread.currentThread().getContextClassLoader();
             }
          });
       }
    }
}
