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
package org.jboss.wise.core.consumer.impl.metro;

import java.net.URL;
import java.net.URLClassLoader;

public class LocalFirstClassLoader extends URLClassLoader {
    public LocalFirstClassLoader( URL[] urls,
                                  ClassLoader parent ) {
        super(urls, parent);
    }

    @Override
    public synchronized Class<?> loadClass( String name,
                                            boolean resolve ) throws ClassNotFoundException {
        Class<?> claz = findLoadedClass(name);
        if (claz == null) {
            try {
                claz = findClass(name);
            } catch (ClassNotFoundException e) {
                if (getParent() != null) {
                    claz = getParent().loadClass(name);
                } else {
                    throw e;
                }

            }
        }
        if (resolve) {
            resolveClass(claz);
        }
        return claz;
    }

}
