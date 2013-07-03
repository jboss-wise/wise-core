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

import java.lang.reflect.Array;
import java.util.HashMap;

/**
 * Java utilities
 * 
 * @author stefano.maestri@javalinux.it class imported from JBossWS.
 */
public class JavaUtils {

    private static HashMap<String, Class<?>> primitiveNames = new HashMap<String, Class<?>>(12);

    static {
	primitiveNames.put("int", int.class);
	primitiveNames.put("short", short.class);
	primitiveNames.put("boolean", boolean.class);
	primitiveNames.put("byte", byte.class);
	primitiveNames.put("long", long.class);
	primitiveNames.put("double", double.class);
	primitiveNames.put("float", float.class);
	primitiveNames.put("char", char.class);
    }

    /**
     * Load a Java type from a given class loader.
     * 
     * @param typeName
     * @param classLoader
     * @return Class
     * @throws ClassNotFoundException
     */
    public static Class<?> loadJavaType(String typeName, ClassLoader classLoader) throws ClassNotFoundException {
	if (classLoader == null)
	    classLoader = Thread.currentThread().getContextClassLoader();

	Class<?> javaType = primitiveNames.get(typeName);
	if (javaType == null)
	    javaType = getArray(typeName, classLoader);

	if (javaType == null)
	    javaType = classLoader.loadClass(typeName);

	return javaType;
    }

    /**
     * True if the given class is a primitive or array of which.
     * 
     * @param javaType
     *            boolean
     * @return boolean
     */
    public static boolean isPrimitive(Class<?> javaType) {
	return javaType.isPrimitive() || (javaType.isArray() && isPrimitive(javaType.getComponentType()));
    }

    private static Class<?> getArray(String javaType, ClassLoader loader) throws ClassNotFoundException {
	if (javaType.charAt(0) == '[')
	    return getArrayFromJVMName(javaType, loader);

	if (javaType.endsWith("[]"))
	    return getArrayFromSourceName(javaType, loader);

	return null;
    }

    private static Class<?> getArrayFromJVMName(String javaType, ClassLoader loader) throws ClassNotFoundException {
	Class<?> componentType;
	int componentStart = javaType.lastIndexOf('[') + 1;
	switch (javaType.charAt(componentStart)) {
	    case 'I':
		componentType = int.class;
		break;
	    case 'S':
		componentType = short.class;
		break;
	    case 'Z':
		componentType = boolean.class;
		break;
	    case 'B':
		componentType = byte.class;
		break;
	    case 'J':
		componentType = long.class;
		break;
	    case 'D':
		componentType = double.class;
		break;
	    case 'F':
		componentType = float.class;
		break;
	    case 'C':
		componentType = char.class;
		break;
	    case 'L':
		if (loader == null)
		    return null;
		String name = javaType.substring(componentStart + 1, javaType.length() - 1);
		componentType = loader.loadClass(name);
		break;
	    default:
		throw new IllegalArgumentException("Invalid binary component for array: " + javaType.charAt(componentStart));
	}

	// componentStart doubles as the number of '['s which is the number of
	// dimensions
	return Array.newInstance(componentType, new int[componentStart]).getClass();
    }

    private static Class<?> getArrayFromSourceName(String javaType, ClassLoader loader) throws ClassNotFoundException {
	int arrayStart = javaType.indexOf('[');
	String componentName = javaType.substring(0, arrayStart);

	Class<?> componentType = primitiveNames.get(componentName);
	if (componentType == null) {
	    if (loader == null)
		return null;

	    componentType = loader.loadClass(componentName);
	}

	// [][][][] divided by 2
	int dimensions = (javaType.length() - arrayStart) >> 1;

	return Array.newInstance(componentType, new int[dimensions]).getClass();
    }

    /**
     * Get the corresponding wrapper type for a give primitive. Also handles
     * arrays of which.
     * 
     * @param javaType
     * @return Class
     */
    public static Class<?> getWrapperType(Class<?> javaType) {
	if (javaType == int.class)
	    return Integer.class;
	if (javaType == short.class)
	    return Short.class;
	if (javaType == boolean.class)
	    return Boolean.class;
	if (javaType == byte.class)
	    return Byte.class;
	if (javaType == long.class)
	    return Long.class;
	if (javaType == double.class)
	    return Double.class;
	if (javaType == float.class)
	    return Float.class;
	if (javaType == char.class)
	    return Character.class;

	if (javaType == int[].class)
	    return Integer[].class;
	if (javaType == short[].class)
	    return Short[].class;
	if (javaType == boolean[].class)
	    return Boolean[].class;
	if (javaType == byte[].class)
	    return Byte[].class;
	if (javaType == long[].class)
	    return Long[].class;
	if (javaType == double[].class)
	    return Double[].class;
	if (javaType == float[].class)
	    return Float[].class;
	if (javaType == char[].class)
	    return Character[].class;

	if (javaType.isArray() && javaType.getComponentType().isArray()) {
	    Class<?> compType = getWrapperType(javaType.getComponentType());
	    return Array.newInstance(compType, 0).getClass();
	}

	return javaType;
    }

    public static String capitalize(String source) {
	if (source == null)
	    return null;

	if (source.length() == 0)
	    return source;

	if (Character.isUpperCase(source.charAt(0)))
	    return source;

	char c = Character.toUpperCase(source.charAt(0));

	return c + source.substring(1);
    }

}
