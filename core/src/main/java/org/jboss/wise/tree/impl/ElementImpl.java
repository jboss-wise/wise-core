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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;

import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.wise.core.utils.IDGenerator;
import org.jboss.wise.core.utils.JavaUtils;
import org.jboss.wise.core.utils.ReflectionUtils;
import org.jboss.wise.tree.Element;

/**
 * @author alessio.soldano@jboss.com
 */
public class ElementImpl implements Element {

    private static final long serialVersionUID = -2831618351948874761L;
    
    protected String id;
    protected String name;
    protected String value;

    protected boolean nil; //whether this elements has the attribute xsi:nil set to "true"
    protected boolean nillable = true; //for primitives and explicitly not nillable elements
    protected boolean removable; // to be used on array elements
    
    private Element parent;
    protected Type classType;
    protected boolean isLeaf;
    protected boolean lazy;
    protected boolean resolved;
    protected boolean group;
    
    protected Map<String, ElementImpl> children;
    protected ElementImpl prototype;
    
    protected WSDynamicClient client;
    protected Map<Type, ElementImpl> treeTypesMap;

    protected ElementImpl(boolean isLeaf) {
	this.isLeaf = isLeaf;
	if (isLeaf) {
	    children = Collections.emptyMap();
	} else {
	    children = new HashMap<String, ElementImpl>(8);
	}
    }
    
    @Override
    public boolean isLeaf() {
	return isLeaf;
    }
    
    @Override
    public boolean isRemovable() {
	return removable;
    }

//    @Override
    protected void setRemovable(boolean removable) {
	this.removable = removable;
    }

    @Override
    public Type getClassType() {
	return classType;
    }
    
    public void setClassType(Type classType) {
	this.classType = classType;
    }

    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public boolean isNil() {
	return nil;
    }

    @Override
    public void setNil(boolean nil) {
	this.nil = nil;
    }

    @Override
    public String getId() {
	return id;
    }
    
    protected void setId(String id) {
	this.id = id;
    }

    @Override
    public boolean isNillable() {
	return nillable;
    }

    public void setNillable(boolean nillable) {
	this.nillable = nillable;
    }
    
    @Override
    public boolean isGroup() {
	return group;
    }
    
    protected void setGroup(boolean group) {
	this.group = group;
    }

    public Element getParent() {
        return parent;
    }

    protected void setParent(Element parent) {
        this.parent = parent;
    }
    
    @Override
    public String getValue() {
	return value;
    }

    @Override
    public void setValue(String value) {
	checkLeaf();
	this.value = value;
    }
    
    public void parseObject(Object obj) {
	checkLeaf();
	if (obj == null) {
	    this.setValue(null);
	}
	if (obj instanceof byte[]) {
	    this.setValue(DatatypeConverter.printBase64Binary((byte[])obj));
	} else {
	    this.setValue(obj.toString());
	}
	this.nil = (obj == null && nillable);
    }
    
    protected void checkLeaf() {
	if (!isLeaf) {
	    throw new UnsupportedOperationException("Element is not leaf");
	}
    }
    
    protected void setClient(WSDynamicClient client) {
	this.client = client;
    }
    
    protected void setTreeTypesMap(Map<Type, ElementImpl> treeTypesMap) {
        this.treeTypesMap = treeTypesMap;
    }

    public void addChild(ElementImpl child) {
	if (isLeaf) {
	    throw new UnsupportedOperationException("Element is leaf");
	}
	children.put(child.getId(), child);
	child.setParent(this);
    }

    @Override
    public void removeChild(String id) {
	ElementImpl child = children.remove(id);
	if (child != null) {
	    if (!child.isRemovable()) {
		children.put(id, child);
		throw new WiseRuntimeException("Element for id=" + id + " is not removable!");
	    }
	    child.setParent(null);
	}
    }
    
    @Override
    public Element getChild(String id) {
	resolveReference();
	return children.get(id);
    }
    
    @Override
    public Element getChildByName(String name) {
	if (group) {
	    throw new UnsupportedOperationException("Cannot get child by name for a group element!");
	}
	resolveReference();
	for (ElementImpl el : children.values()) {
	    if (name.equals(el.getName())) {
		return el;
	    }
	}
	return null;
    }
    
    @Override
    public Iterator<String> getChildrenIDs() {
	return this.getChildrenIDs(true);
    }
    
    @Override
    public Iterator<? extends Element> getChildren() {
	return this.getChildren(true);
    }
    
//    @Override
    protected Iterator<String> getChildrenIDs(boolean resolve) {
	if (resolve) {
	    resolveReference();
	}
	return unmodifiableIterator(children.keySet().iterator());
    }
    
//    @Override
    protected Iterator<? extends Element> getChildren(boolean resolve) {
	return this.getChildrenInternal(resolve);
    }
    
    protected Iterator<ElementImpl> getChildrenInternal(boolean resolve) {
	if (resolve) {
	    resolveReference();
	}
	return unmodifiableIterator(children.values().iterator());
    }
    
    protected void resolveReference() {
	if (isLazy() && !isResolved()) {
	    ElementImpl ref = treeTypesMap.get(this.classType);
            ElementImpl component = ref.cloneInternal();
            component.setName(this.getName());
            addChild(component);
            setResolved(true);
	}
    }
    
    @Override
    public boolean isLazy() {
	return lazy;
    }
    
    protected void setLazy(boolean lazy) {
	this.lazy = lazy;
    }
    
    @Override
    public boolean isResolved() {
	return resolved;
    }
    
    protected void setResolved(boolean resolved) {
	this.resolved = resolved;
    }
    
    @Override
    public Element getPrototype() {
	return prototype;
    }

    protected void setPrototype(ElementImpl prototype) {
	this.prototype = prototype;
    }
    
    @Override
    public Element incrementChildren() {
	if (!group) {
	    throw new UnsupportedOperationException("Element is not a group!");
	}
	resolveReference();
	ElementImpl component = prototype.cloneInternal();
	component.setRemovable(true);
	this.addChild(component);
	return component;
    }
    
    @Override
    public int getChildrenCount() {
	resolveReference();
	return children.size();
    }
    
    /**
     * Every Element must be cloneable; this is required to handle
     * element's add and removal into/from arrays and collections.
     */
    @Override
    public Element clone() {
	return this.cloneInternal();
    }
    
    protected ElementImpl cloneInternal() {
	ElementImpl element = new ElementImpl(isLeaf);
	element.setId(IDGenerator.nextVal());
	element.setName(this.name);
	element.setNil(this.nil);
	element.setClassType(this.classType);
	element.setRemovable(this.isRemovable());
	element.setNillable(this.isNillable());
	element.setLazy(this.lazy);
	if (isLeaf) { //simple
	    Class<?> clazz = (Class<?>)this.classType;
	    element.setValue(clazz.isPrimitive() ? ElementBuilderImpl.getDefaultValue(clazz) : null);
	    element.setNil(this.isNillable()); //default to nil on simple elements
	} else if (lazy) { //lazy
	    element.setResolved(false); //copy into an unresolved element and do not copy child
	} else { //complex, group
	    for (Iterator<ElementImpl> it = this.getChildrenInternal(false); it.hasNext(); ) {
		ElementImpl child = it.next();
		element.addChild(child.cloneInternal());
	    }
	    if (this.prototype != null) {
		element.setPrototype(this.prototype.cloneInternal());
	    }
	}
	element.setGroup(this.group);
	element.setClient(this.client);
	element.setTreeTypesMap(this.treeTypesMap);
	return element;
    }
    
    /**
     * This is required to convert a tree element into the corresponding object
     * instance.
     * 
     * @return The object corresponding to this element
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object toObject() {
	if (nil) {
	    return null;
	}
	if (isLeaf) { //simple
	    if (value == null) {
		return null;
	    }
	    Class<?> cl = (Class<?>) classType;
	    if (cl.isArray() && byte.class.equals(cl.getComponentType())) {
		return DatatypeConverter.parseBase64Binary(value);
	    }
	    if (cl.isPrimitive()) {
		cl = JavaUtils.getWrapperType(cl);
	    }
	    final String n = cl.getName();
	    if ("java.lang.String".equals(n)) {
		return new String(value);
	    } else if ("java.lang.Boolean".equals(n)) {
		return new Boolean(value);
	    } else if ("java.lang.Byte".equals(n)) {
		return new Byte(value);
	    } else if ("java.lang.Character".equals(n)) {
		return new Character(value.charAt(0));
	    } else if ("java.lang.Double".equals(n)) {
		return new Double(value);
	    } else if ("java.lang.Float".equals(n)) {
		return new Float(value);
	    } else if ("java.lang.Integer".equals(n)) {
		return new Integer(value);
	    } else if ("java.lang.Long".equals(n)) {
		return new Long(value);
	    } else if ("java.lang.Short".equals(n)) {
		return new Short(value);
	    } else if ("java.math.BigDecimal".equals(n)) {
		return BigDecimal.valueOf(Double.parseDouble(value));
	    } else if ("java.math.BigInteger".equals(n)) {
		return BigInteger.valueOf(Long.parseLong(value));
	    } else if ("java.lang.Object".equalsIgnoreCase(n)) {
		return (Object) value;
	    } else if ("javax.xml.namespace.QName".equals(n)) {
		return QName.valueOf(value);
	    } else if ("javax.xml.datatype.Duration".equals(n)) {
		try {
		    return DatatypeFactory.newInstance().newDuration(Long.parseLong(value));
		} catch (DatatypeConfigurationException e) {
		    throw new WiseRuntimeException("Error converting element to object, type format error?", e);
		}
	    } else if ("javax.xml.datatype.XMLGregorianCalendar".equals(n)) {
		try {
		    return DatatypeFactory.newInstance().newXMLGregorianCalendar(value);
		} catch (DatatypeConfigurationException e) {
		    throw new WiseRuntimeException("Type format error", e);
		}
	    } else {
		throw new WiseRuntimeException("Class type not supported: " + cl);
	    }
	} else if (group) { //group
	    LinkedList<Object> returnList = new LinkedList<Object>();
	    for (Iterator<ElementImpl> it = this.getChildrenInternal(false); it.hasNext();) {
		returnList.add(it.next().toObject());
	    }
	    return returnList;
	} else if (lazy) { //lazy
	    Iterator<ElementImpl> it = getChildrenInternal(false);
	    return it.hasNext() ? it.next().toObject() : null;
	} else if (classType instanceof ParameterizedType) { //parameterized
	    Class<?> parameterizedClass = (Class<?>)((ParameterizedType) classType).getRawType();
	    Object child = getChildren(false).next().toObject();
	    if (parameterizedClass.isAssignableFrom(JAXBElement.class)) {
		return instanceXmlElementDecl(this.name, (Class<?>)this.parent.getClassType(), child);
	    } else if (parameterizedClass.isAssignableFrom(Holder.class)) {
		return instanceHolder(child);
	    } else {
		throw new WiseRuntimeException("Unsupported parameterized class: " + parameterizedClass);
	    }
	} else { //complex
	    try {
		Class<?> cl = (Class<?>) classType;
		final Object obj = cl.newInstance();
		for (Iterator<ElementImpl> it = this.getChildrenInternal(false); it.hasNext(); ) {
		    Element child = it.next();
		    final boolean isBoolean = isBoolean(child.getClassType());
		    final Object childObject = child.toObject();
		    if (childObject != null) {
			if (child.isGroup()) {
			    final String getter = ReflectionUtils.getterMethodName(child.getName(), isBoolean);
			    final Method method = cl.getMethod(getter, (Class[]) null);
			    Collection<?> col = (Collection<?>) method.invoke(obj, (Object[]) null);
			    col.addAll((List) childObject);
			} else {
			    final String setter = ReflectionUtils.setterMethodName(child.getName(), isBoolean);
			    final Type t = child.getClassType();
			    final Method method;
			    if (t instanceof ParameterizedType) {
				method = cl.getMethod(setter, (Class<?>) ((ParameterizedType) t).getRawType());
			    } else {
				Class<?> fieldClass = (Class<?>) t;
				if (Duration.class.isAssignableFrom(fieldClass)) {
				    method = cl.getMethod(setter, Duration.class);
				} else if (XMLGregorianCalendar.class.isAssignableFrom(fieldClass)) {
				    method = cl.getMethod(setter, XMLGregorianCalendar.class);
				} else {
				    method = cl.getMethod(setter, fieldClass);
				}
			    }
			    method.invoke(obj, childObject);
			}
		    }
		}
		return obj;
	    } catch (Exception e) {
		throw new WiseRuntimeException("Error converting element to object", e);
	    }
	}
    }
    
    /**
     * Make sure this element can't be nill and set the default value
     * (this is to be used e.g. for main RPC/Lit parameters)
     */
    protected void enforceNotNillable() {
	this.setNillable(false);
	if (isLeaf) {
	    this.setNil(false);
	    this.setValue(ElementBuilderImpl.getDefaultValue((Class<?>) classType));
	}
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object instanceHolder(Object obj) {
	return new Holder(obj);
    }
    
    private Object instanceXmlElementDecl(String name, Class<?> scope, Object value) {
	try {
	    Class<?> objectFactoryClass = null;
	    Method methodToUse = null;
	    boolean done = false;
	    final List<Class<?>> objectFactories = this.client.getObjectFactories();
	    if (objectFactories != null) {
		Method m = value instanceof Boolean ? scope.getMethod("is" + JavaUtils.capitalize(name)) : scope.getMethod("get" + JavaUtils.capitalize(name));
		XmlElementRef ann = m.getAnnotation(XmlElementRef.class);
		String namespace = ann != null ? ann.namespace() : null;
		for (Iterator<Class<?>> it = objectFactories.iterator(); it.hasNext() && !done; ) {
		    objectFactoryClass = it.next();
		    Method[] methods = objectFactoryClass.getMethods();
		    for (int i = 0; i < methods.length; i++) {
			XmlElementDecl annotation = methods[i].getAnnotation(XmlElementDecl.class);
			if (annotation != null && name.equals(annotation.name()) && (annotation.namespace() == null || annotation.namespace().equals(namespace)) && (annotation
				.scope() == null || annotation.scope().equals(scope))) {
			    methodToUse = methods[i];
			    break;
			}
		    }
		    if (methodToUse != null) {
			done = true;
		    }
		}
	    }
	    if (methodToUse != null) {
		return methodToUse.invoke(objectFactoryClass.newInstance(), new Object[] { value });
	    } else {
		return null;
	    }
	} catch (Exception e) {
	    throw new WiseRuntimeException(e);
	}
    }
    
    private static boolean isBoolean(Type type) {
	String sn;
	if (type instanceof ParameterizedType) {
	    sn = ((Class<?>) ((ParameterizedType) type).getRawType()).getSimpleName();
	} else {
	    sn = ((Class<?>) type).getSimpleName();
	}
	return "Boolean".equalsIgnoreCase(sn);
    }

    protected <T> UnmodifiableIterator<T> unmodifiableIterator(final Iterator<T> iterator) {
	if (iterator instanceof UnmodifiableIterator) {
	    return (UnmodifiableIterator<T>) iterator;
	}
	return new UnmodifiableIterator<T>() {
	    @Override
	    public boolean hasNext() {
		return iterator.hasNext();
	    }

	    @Override
	    public T next() {
		return iterator.next();
	    }
	};
    }

    private static abstract class UnmodifiableIterator<E> implements Iterator<E> {
	public UnmodifiableIterator() {
	}

	@Override
	public final void remove() {
	    throw new UnsupportedOperationException();
	}
    }
}
