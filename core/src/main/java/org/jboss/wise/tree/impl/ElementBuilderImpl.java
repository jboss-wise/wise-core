/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.wise.tree.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.ws.Holder;

import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.wise.core.utils.IDGenerator;
import org.jboss.wise.core.utils.JavaUtils;
import org.jboss.wise.core.utils.ReflectionUtils;
import org.jboss.wise.tree.Element;
import org.jboss.wise.tree.ElementBuilder;


/**
 * @author alessio.soldano@jboss.com
 * 
 */
public class ElementBuilderImpl implements ElementBuilder {
    
    private WSDynamicClient client;
    private boolean request = true;
    private boolean useDefautValuesForNullLeaves = true;
    
    public ElementBuilderImpl() {
    }
    
    @Override
    public ElementBuilder client(WSDynamicClient client) {
	this.client = client;
	return this;
    }

    @Override
    public ElementBuilder request(boolean request) {
	this.request = request;
	return this;
    }

    @Override
    public ElementBuilder useDefautValuesForNullLeaves(boolean useDefautValuesForNullLeaves) {
	this.useDefautValuesForNullLeaves = useDefautValuesForNullLeaves;
	return this;
    }
    
    @Override
    public Element buildTree(Type type, String name, Object value, boolean nillable) {
	if (client == null) {
	    throw new IllegalStateException("WSDynamicClient reference is not set!");
	}
	return buildTree(type, name, value, nillable, null, null, Collections.synchronizedMap(new HashMap<Type, ElementImpl>()), new HashSet<Type>());
    }

    private ElementImpl buildTree(Type type, String name, Object obj, boolean nillable, Class<?> scope, String namespace, Map<Type, ElementImpl> typeMap, Set<Type> stack) {
	if (type instanceof ParameterizedType) {
	    ParameterizedType pt = (ParameterizedType) type;
	    return this.buildParameterizedType(pt, name, obj, scope, namespace, typeMap, stack);
	} else {
	    return this.buildFromClass((Class<?>) type, name, obj, nillable, typeMap, stack);

	}
    }
    
    @SuppressWarnings("rawtypes")
    private ElementImpl buildParameterizedType(ParameterizedType pt,
	                                           String name,
	                                           Object obj,
	                                           Class<?> scope,
	                                           String namespace,
	                                           Map<Type, ElementImpl> typeMap,
		    				   Set<Type> stack) {
	Type firstTypeArg = pt.getActualTypeArguments()[0];
	if (Collection.class.isAssignableFrom((Class<?>) pt.getRawType())) {
	    ElementImpl group;
	    if (obj != null || request) {
		ElementImpl prototype = this.buildTree(firstTypeArg, name, null, true, null, null, typeMap, stack);
		group = newElement(pt, name, false, prototype, false, typeMap);
		if (obj != null) {
		    for (Object o : (Collection) obj) {
			ElementImpl childElement = this.buildTree(firstTypeArg, name, o, true, null, null, typeMap, stack);
			childElement.setRemovable(true);
			group.addChild(childElement);
		    }
		}
	    } else {
		group = newElement(pt, name, false, null, false, typeMap);
	    }
	    return group;
	} else {
	    if (obj != null && obj instanceof JAXBElement) {
		obj = ((JAXBElement)obj).getValue();
	    } else if (obj != null && obj instanceof Holder) {
		obj = ((Holder)obj).value;
	    }
	    ElementImpl element = this.buildTree(firstTypeArg, name, obj, true, null, null, typeMap, stack);
	    ElementImpl parameterized = newElement(pt, name, false, null, false, typeMap);
	    parameterized.addChild(element);
	    return parameterized;
	}
    }

    private ElementImpl buildFromClass(Class<?> cl, String name, Object obj, boolean nillable, Map<Type, ElementImpl> typeMap, Set<Type> stack) {

	if (cl.isArray()) {
	    if (byte.class.equals(cl.getComponentType())) {
		return newLeafElement(cl, name, obj, !nillable);
	    }
	    throw new WiseRuntimeException("Converter doesn't support this Object[] yet.");
	}

	if (isSimpleType(cl, client)) {
	    return newLeafElement(cl, name, obj, !nillable);
	} else { // complex
	    final boolean recursionCheck = (request && obj == null); //lazy element disable till we have a value object (an actual SOAP request won't have cycles)
	    if (recursionCheck && stack.contains(cl)) {
		return newElement(cl, name, false, null, true, typeMap);
	    }

	    ElementImpl complex = newElement(cl, name, !nillable, null, false, typeMap);
	    if (recursionCheck) {
		stack.add(cl);
	    }
	    for (Field field : ReflectionUtils.getAllFields(cl)) {
		XmlElement elemAnnotation = field.getAnnotation(XmlElement.class);
		XmlElementRef refAnnotation = field.getAnnotation(XmlElementRef.class);
		String fieldName = null;
		String namespace = null;
		if (elemAnnotation != null && !elemAnnotation.name().startsWith("#")) {
		    fieldName = elemAnnotation.name();
		}
		if (refAnnotation != null) {
		    fieldName = refAnnotation.name();
		    namespace = refAnnotation.namespace();
		}
		if (fieldName == null) {
		    fieldName = field.getName();
		}
		// String fieldName = (annotation != null &&
		// !annotation.name().startsWith("#")) ? annotation.name() :
		// field.getName();
		Object fieldValue = null;
		if (obj != null) {
		    try {
			Method getter = cl.getMethod(ReflectionUtils.getGetter(field), (Class[]) null);
			fieldValue = getter.invoke(obj, (Object[]) null);
		    } catch (Exception e) {
			throw new WiseRuntimeException("Error calling getter method for field " + field, e);
		    }
		}
		ElementImpl element = this.buildTree(field.getGenericType(), fieldName, fieldValue, true, cl, namespace, typeMap, stack);
		complex.addChild(element);
	    }
	    if (recursionCheck) {
		stack.remove(cl);
		if (!typeMap.containsKey(cl)) {
		    typeMap.put(cl, complex.cloneInternal());
		}
	    }
	    return complex;
	}
    }
    
    protected boolean isSimpleType(Class<?> cl, WSDynamicClient client) {
	return cl.isEnum() || cl.isPrimitive() || client.getClassLoader() != cl.getClassLoader();
    }
    
    private ElementImpl newLeafElement(Class<?> classType, String name, Object value, boolean forceNotNillable) {
	ElementImpl element = new ElementImpl(true);
	element.setClassType(classType);
	element.setName(name);
	element.setId(generateNewID());
	if (forceNotNillable) {
	    element.enforceNotNillable();
	} else {
	    // primitive are not nillable, thus they can't be nil or have a null value
	    final boolean primitive = classType.isPrimitive();
	    element.setNillable(!primitive);
	    element.setNil(!primitive);
	    if (value == null && (useDefautValuesForNullLeaves || primitive)) {
		element.setValue(getDefaultValue(classType));
		element.setNil(false);
	    }
	}
	if (value != null) {
	    element.parseObject(value);
	}
	return element;
    }
    
    private ElementImpl newElement(Type classType, String name, boolean forceNotNillable, ElementImpl prototype, boolean lazy, Map<Type, ElementImpl> treeTypesMap) {
	ElementImpl element = new ElementImpl(false);
	element.setClassType(classType);
	element.setName(name);
	element.setId(generateNewID());
	if (classType instanceof ParameterizedType && Collection.class.isAssignableFrom((Class<?>) ((ParameterizedType) classType).getRawType())) {
	    element.setPrototype(prototype);
	    element.setGroup(true);
	}
	element.setClient(client);
	element.setLazy(lazy);
	element.setTreeTypesMap(treeTypesMap);
	if (forceNotNillable) {
	    element.enforceNotNillable();
	}
	return element;
    }
    
    protected static String getDefaultValue(Class<?> cl) {
	if (cl.isPrimitive()) {
	    cl = JavaUtils.getWrapperType(cl);
	}
	String cn = cl.getName();
	if ("java.lang.Boolean".equals(cn)) {
	    return "false";
	} else if ("java.lang.Byte".equals(cn)) {
	    return "0";
	} else if ("java.lang.Double".equals(cn)) {
	    return "0.0";
	} else if ("java.lang.Float".equals(cn)) {
	    return "0.0";
	} else if ("java.lang.Integer".equals(cn)) {
	    return "0";
	} else if ("java.lang.Long".equals(cn)) {
	    return "0";
	} else if ("java.lang.Short".equals(cn)) {
	    return "0";
	} else if ("java.math.BigDecimal".equals(cn)) {
	    return "0.0";
	} else if ("java.math.BigInteger".equals(cn)) {
	    return "0";
	} else if ("javax.xml.datatype.Duration".equals(cn)) {
	    return "0";
	} else if ("javax.xml.datatype.XMLGregorianCalendar".equals(cn)) {
	    return "1970-01-01T00:00:00.000Z";
	} else if (cl.isEnum()) {
	    return getValidEnumValues(cl).iterator().next();
	} else {
	    return "";
	}
    }
    
    public static List<String> getValidEnumValues(Class<?> classType) {
	List<String> list = new ArrayList<String>();
	for (Object obj : classType.getEnumConstants()) {
	    try {
		list.add((String) obj.getClass().getMethod("value").invoke(obj));
	    } catch (Exception e) {
		throw new WiseRuntimeException("Could not get enum values for " + classType, e);
	    }
	}
	return list;
    }
    
    protected String generateNewID() {
	return IDGenerator.nextVal();
    }



}
