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
package org.jboss.wise.core.utils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides some utility methods useful to deal with classes through reflection.
 * 
 * @author stefano.maestri@javalinux.it
 * @author alessio.soldano@javalinux.it
 * @since 21-Aug-2007
 */
public class ReflectionUtils {

    /**
     * Return all fields of a given class
     * 
     * @param cl the class to reflect to.
     * @return all fields of the provided class
     */
    public static List<Field> getAllFields( Class<?> cl ) {
        List<Field> list = new LinkedList<Field>();
        for (Field field : cl.getDeclaredFields()) {
            if (!"serialVersionUID".equals(field.getName()) && !field.getName().startsWith("this$")) {
                list.add(field);
            }
        }
        if (cl.getSuperclass() != null && !cl.getSuperclass().isAssignableFrom(Throwable.class)) {
            list.addAll(ReflectionUtils.getAllFields(cl.getSuperclass()));
        }
        return list;
    }

    /**
     * Get setter method name of given fieldName
     * 
     * @param fieldName
     * @param isBoolean
     * @return String
     */
    public static String setterMethodName( String fieldName,
                                           boolean isBoolean ) {
        if (!isBoolean) {
            return "set" + JavaUtils.capitalize(fieldName);
        } else {
            return "set" + (fieldName.startsWith("is") ? fieldName.substring(2) : JavaUtils.capitalize(fieldName));
        }
    }

    /**
     * Get getter method name of given fieldName
     * 
     * @param fieldName
     * @param isBoolean
     * @return String
     */
    public static String getterMethodName( String fieldName,
                                           boolean isBoolean ) {
	if (!isBoolean) {
	    return "get" + JavaUtils.capitalize(fieldName);
	} else {
	    return fieldName.startsWith("is") ? fieldName : "is" + JavaUtils.capitalize(fieldName);
	}
    }

    /**
     * Get getter method name of given field
     * 
     * @param field
     * @return String
     */
    public static String getGetter( Field field, String xmlName ) {
        Class<?> cl = field.getType();
        if (cl.isPrimitive()) {
            cl = JavaUtils.getWrapperType(cl);
        }
        final String m = xmlName != null ? xmlName : JavaUtils.capitalize(field.getName());
        return cl.getName().equalsIgnoreCase("java.lang.Boolean") ? "is" + m : "get" + m;
    }
}
