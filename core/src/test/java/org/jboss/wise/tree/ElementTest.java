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
package org.jboss.wise.tree;

import static org.hamcrest.collection.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.impl.reflection.WSDynamicClientImpl;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.wise.tree.impl.ElementBuilderImpl;
import org.junit.Test;

/**
 * Unit testing of Wise Tree Element
 * 
 * @author alessio.soldano@jboss.com
 */
public class ElementTest {
    
    @Test
    public void shouldBuildTreeOfStringElement() throws Exception {
	ElementBuilderImpl builder = getElementBuilder(true, false);
	final String name = "myString";
	final String value = "foo";
	
	Element el = builder.buildTree(String.class, name, null, true);
	assertElementProps(el, true, false, false, true, true, false, false, 0);
	assertEquals(String.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(null, el.getValue());
	Object obj = el.toObject();
	assertEquals(null, obj);
	
	el = getElementBuilder(true, true).buildTree(String.class, name, null, true);
	assertElementProps(el, true, false, false, false, true, false, false, 0);
	assertEquals(String.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals("", el.getValue());
	obj = el.toObject();
	assertEquals(String.class, obj.getClass());
	assertEquals("", obj);
	
	el = builder.buildTree(String.class, name, value, true);
	assertElementProps(el, true, false, false, false, true, false, false, 0);
	assertEquals(String.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(value, el.getValue());
	obj = el.toObject();
	assertEquals(String.class, obj.getClass());
	assertEquals(value, obj);
	
	el = builder.buildTree(String.class, name, null, false);
	assertElementProps(el, true, false, false, false, false, false, false, 0);
	assertEquals(String.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals("", el.getValue());
	obj = el.toObject();
	assertEquals(String.class, obj.getClass());
	assertEquals("", obj);
	
	el = builder.buildTree(String.class, name, value, false);
	assertElementProps(el, true, false, false, false, false, false, false, 0);
	assertEquals(String.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(value, el.getValue());
	obj = el.toObject();
	assertEquals(String.class, obj.getClass());
	assertEquals(value, obj);
    }
    
    @Test
    public void shouldBuildTreeOfNumberElement() throws Exception {
	shouldBuildTreeOfNumberElement(Integer.class, "myInteger", "234", "0", new Integer(234), new Integer(0));
	shouldBuildTreeOfNumberElement(Long.class, "myLong", "-87532478", "0", new Long(-87532478), new Long(0));
	shouldBuildTreeOfNumberElement(Short.class, "myShort", "27", "0", new Short("27"), new Short("0"));
	shouldBuildTreeOfNumberElement(Float.class, "myFloat", "-234.58", "0.0", new Float(-234.58), new Float(0.0));
	shouldBuildTreeOfNumberElement(Double.class, "myDouble", "87532478.48", "0.0", new Double(87532478.48), new Double("0.0"));
	shouldBuildTreeOfNumberElement(BigDecimal.class, "myBigDecimal", "87532478.48", "0.0", new BigDecimal("87532478.48"), new BigDecimal("0.0"));
	shouldBuildTreeOfNumberElement(BigInteger.class, "myBigInteger", "87532478", "0", new BigInteger("87532478"), new BigInteger("0"));
    }
    
    @Test
    public void shouldBuildTreeOfPrimitiveNumberElement() throws Exception {
	shouldBuildTreeOfPrimitiveNumberElement(int.class, "my-int", "-234", "0", -234, 0);
	shouldBuildTreeOfPrimitiveNumberElement(long.class, "my-long", "87532478", "0", 87532478l, 0l);
	shouldBuildTreeOfPrimitiveNumberElement(short.class, "my-short", "-27", "0", (short)-27, (short)0);
	shouldBuildTreeOfPrimitiveNumberElement(float.class, "my-float", "234.58", "0.0", 234.58f, 0.0f);
	shouldBuildTreeOfPrimitiveNumberElement(double.class, "my-double", "-87532478.48", "0.0", -87532478.48, 0.0);
    }
    
    @Test
    public void shouldBuildTreeOfQNameElement() throws Exception {
	ElementBuilderImpl builder = getElementBuilder(true, false);
	final String name = "myQName";
	final String value = "{org.jboss.wise}foo";
	
	Element el = builder.buildTree(QName.class, name, null, true);
	assertElementProps(el, true, false, false, true, true, false, false, 0);
	assertEquals(QName.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(null, el.getValue());
	Object obj = el.toObject();
	assertEquals(null, obj);
	
	el = getElementBuilder(true, true).buildTree(QName.class, name, null, true);
	assertElementProps(el, true, false, false, false, true, false, false, 0);
	assertEquals(QName.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals("", el.getValue());
	obj = el.toObject();
	assertEquals(QName.class, obj.getClass());
	assertEquals("", ((QName)obj).getNamespaceURI());
	assertEquals("", ((QName)obj).getLocalPart());
	
	el = builder.buildTree(QName.class, name, value, true);
	assertElementProps(el, true, false, false, false, true, false, false, 0);
	assertEquals(QName.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(value, el.getValue());
	obj = el.toObject();
	assertEquals(QName.class, obj.getClass());
	assertEquals("org.jboss.wise", ((QName)obj).getNamespaceURI());
	assertEquals("foo", ((QName)obj).getLocalPart());
	
	el = builder.buildTree(QName.class, name, null, false);
	assertElementProps(el, true, false, false, false, false, false, false, 0);
	assertEquals(QName.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals("", el.getValue());
	obj = el.toObject();
	assertEquals(QName.class, obj.getClass());
	assertEquals("", ((QName)obj).getNamespaceURI());
	assertEquals("", ((QName)obj).getLocalPart());
	
	el = builder.buildTree(QName.class, name, value, false);
	assertElementProps(el, true, false, false, false, false, false, false, 0);
	assertEquals(QName.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(value, el.getValue());
	obj = el.toObject();
	assertEquals(QName.class, obj.getClass());
	assertEquals("org.jboss.wise", ((QName)obj).getNamespaceURI());
	assertEquals("foo", ((QName)obj).getLocalPart());
    }
    
    @Test
    public void shouldBuildTreeOfDurationElement() throws Exception {
	ElementBuilderImpl builder = getElementBuilder(true, false);
	final String name = "myDuration";
	final long longVal = 345678;
	final String value = String.valueOf(DatatypeFactory.newInstance().newDuration(longVal).getTimeInMillis(new GregorianCalendar()));
	
	Element el = builder.buildTree(Duration.class, name, null, true);
	assertElementProps(el, true, false, false, true, true, false, false, 0);
	assertEquals(Duration.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(null, el.getValue());
	Object obj = el.toObject();
	assertEquals(null, obj);
	
	el = getElementBuilder(true, true).buildTree(Duration.class, name, null, true);
	assertElementProps(el, true, false, false, false, true, false, false, 0);
	assertEquals(Duration.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals("0", el.getValue());
	obj = el.toObject();
	assertTrue(Duration.class.isAssignableFrom(obj.getClass()));
	assertEquals(0, ((Duration)obj).getTimeInMillis(new GregorianCalendar()));
	
	el = builder.buildTree(Duration.class, name, value, true);
	assertElementProps(el, true, false, false, false, true, false, false, 0);
	assertEquals(Duration.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(value, el.getValue());
	obj = el.toObject();
	assertTrue(Duration.class.isAssignableFrom(obj.getClass()));
	assertEquals(longVal, ((Duration)obj).getTimeInMillis(new GregorianCalendar()));
	
	el = builder.buildTree(Duration.class, name, null, false);
	assertElementProps(el, true, false, false, false, false, false, false, 0);
	assertEquals(Duration.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals("0", el.getValue());
	obj = el.toObject();
	assertTrue(Duration.class.isAssignableFrom(obj.getClass()));
	assertEquals(0, ((Duration)obj).getTimeInMillis(new GregorianCalendar()));
	
	el = builder.buildTree(Duration.class, name, value, false);
	assertElementProps(el, true, false, false, false, false, false, false, 0);
	assertEquals(Duration.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(value, el.getValue());
	obj = el.toObject();
	assertTrue(Duration.class.isAssignableFrom(obj.getClass()));
	assertEquals(longVal, ((Duration)obj).getTimeInMillis(new GregorianCalendar()));
    }
    
    @Test
    public void shouldBuildTreeOfXMLGregorianCalendarElement() throws Exception {
	ElementBuilderImpl builder = getElementBuilder(true, false);
	final String name = "myXMLGregorianCalendar";
	final String value = "2013-02-13T12:12:10.000Z";
	final String refCal = "1970-01-01T00:00:00.000Z";
	
	Element el = builder.buildTree(XMLGregorianCalendar.class, name, null, true);
	assertElementProps(el, true, false, false, true, true, false, false, 0);
	assertEquals(XMLGregorianCalendar.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(null, el.getValue());
	Object obj = el.toObject();
	assertEquals(null, obj);
	
	el = getElementBuilder(true, true).buildTree(XMLGregorianCalendar.class, name, null, true);
	assertElementProps(el, true, false, false, false, true, false, false, 0);
	assertEquals(XMLGregorianCalendar.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(refCal, el.getValue());
	obj = el.toObject();
	assertTrue(XMLGregorianCalendar.class.isAssignableFrom(obj.getClass()));
	assertEquals(refCal, obj.toString());
	
	el = builder.buildTree(XMLGregorianCalendar.class, name, value, true);
	assertElementProps(el, true, false, false, false, true, false, false, 0);
	assertEquals(XMLGregorianCalendar.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(value, el.getValue());
	obj = el.toObject();
	assertTrue(XMLGregorianCalendar.class.isAssignableFrom(obj.getClass()));
	assertEquals(value, obj.toString());
	
	el = builder.buildTree(XMLGregorianCalendar.class, name, null, false);
	assertElementProps(el, true, false, false, false, false, false, false, 0);
	assertEquals(XMLGregorianCalendar.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(refCal, el.getValue());
	obj = el.toObject();
	assertTrue(XMLGregorianCalendar.class.isAssignableFrom(obj.getClass()));
	assertEquals(refCal, obj.toString());
	
	el = builder.buildTree(XMLGregorianCalendar.class, name, value, false);
	assertElementProps(el, true, false, false, false, false, false, false, 0);
	assertEquals(XMLGregorianCalendar.class, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(value, el.getValue());
	obj = el.toObject();
	assertTrue(XMLGregorianCalendar.class.isAssignableFrom(obj.getClass()));
	assertEquals(value, obj.toString());
    }
    
    private static <T> void shouldBuildTreeOfNumberElement(Class<T> clazz, String name, String value, String expDefValue, T expObj, T expDefObj) throws Exception {
	ElementBuilderImpl builder = getElementBuilder(true, false);
	Element el = builder.buildTree(clazz, name, null, true);
	assertElementProps(el, true, false, false, true, true, false, false, 0);
	assertEquals(clazz, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(null, el.getValue());
	Object obj = el.toObject();
	assertEquals(null, obj);
	
	el = getElementBuilder(true, true).buildTree(clazz, name, null, true);
	assertElementProps(el, true, false, false, false, true, false, false, 0);
	assertEquals(clazz, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(expDefValue, el.getValue());
	obj = el.toObject();
	assertEquals(clazz, obj.getClass());
	assertEquals(expDefObj, obj);
	
	el = builder.buildTree(clazz, name, value, true);
	assertElementProps(el, true, false, false, false, true, false, false, 0);
	assertEquals(clazz, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(value, el.getValue());
	obj = el.toObject();
	assertEquals(clazz, obj.getClass());
	assertEquals(expObj, obj);
	
	el = builder.buildTree(clazz, name, null, false);
	assertElementProps(el, true, false, false, false, false, false, false, 0);
	assertEquals(clazz, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(expDefValue, el.getValue());
	obj = el.toObject();
	assertEquals(clazz, obj.getClass());
	assertEquals(expDefObj, obj);
	
	el = builder.buildTree(clazz, name, value, false);
	assertElementProps(el, true, false, false, false, false, false, false, 0);
	assertEquals(clazz, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(value, el.getValue());
	obj = el.toObject();
	assertEquals(clazz, obj.getClass());
	assertEquals(expObj, obj);
    }
    
    private static <T> void shouldBuildTreeOfPrimitiveNumberElement(Class<T> clazz, String name, String value, String expDefValue, T expObj, T expDefObj) throws Exception {
	ElementBuilderImpl builder = getElementBuilder(true, false);
	Element el = builder.buildTree(clazz, name, null, true);
	assertElementProps(el, true, false, false, false, false, false, false, 0);
	assertEquals(clazz, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(expDefValue, el.getValue());
	Object obj = el.toObject();
	assertEquals(expDefObj, obj);
	
	el = getElementBuilder(true, true).buildTree(clazz, name, null, true);
	assertElementProps(el, true, false, false, false, false, false, false, 0);
	assertEquals(clazz, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(expDefValue, el.getValue());
	obj = el.toObject();
	assertEquals(expDefObj, obj);
	
	el = builder.buildTree(clazz, name, value, true);
	assertElementProps(el, true, false, false, false, false, false, false, 0);
	assertEquals(clazz, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(value, el.getValue());
	obj = el.toObject();
	assertEquals(expObj, obj);
	
	el = builder.buildTree(clazz, name, null, false);
	assertElementProps(el, true, false, false, false, false, false, false, 0);
	assertEquals(clazz, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(expDefValue, el.getValue());
	obj = el.toObject();
	assertEquals(expDefObj, obj);
	
	el = builder.buildTree(clazz, name, value, false);
	assertElementProps(el, true, false, false, false, false, false, false, 0);
	assertEquals(clazz, el.getClassType());
	assertEquals(name, el.getName());
	assertEquals(value, el.getValue());
	obj = el.toObject();
	assertEquals(expObj, obj);
    }
    
    @Test
    public void shouldBuildTreeOfComplexElement() throws Exception {
	ElementBuilderImpl builder = getElementBuilder(true, false);
	Element el = builder.buildTree(MyTest.class, "myTest", null, true);
	assertElementProps(el, false, false, false, false, true, false, false, 1);
	assertEquals(MyTest.class, el.getClassType());
	assertEquals("myTest", el.getName());
	assertEquals(null, el.getValue());
	Object obj = el.toObject();
	assertNotNull(obj);
	assertTrue(obj instanceof MyTest);
	assertEquals(null, ((MyTest)obj).getPar());
	
	el = getElementBuilder(true, true).buildTree(MyTest.class, "myTest", null, true);
	assertElementProps(el, false, false, false, false, true, false, false, 1);
	assertEquals(MyTest.class, el.getClassType());
	assertEquals("myTest", el.getName());
	assertEquals(null, el.getValue());
	obj = el.toObject();
	assertNotNull(obj);
	assertTrue(obj instanceof MyTest);
	assertEquals("", ((MyTest)obj).getPar());
	
	el = builder.buildTree(MyTest.class, "myTest", null, false);
	assertElementProps(el, false, false, false, false, false, false, false, 1);
	assertEquals(MyTest.class, el.getClassType());
	assertEquals("myTest", el.getName());
	assertEquals(null, el.getValue());
	obj = el.toObject();
	assertNotNull(obj);
	assertTrue(obj instanceof MyTest);
	assertEquals(null, ((MyTest)obj).getPar());
	
	MyTest t = new MyTest();
	t.setPar("myPar");
	el = builder.buildTree(MyTest.class, "myTest", t, true);
	assertElementProps(el, false, false, false, false, true, false, false, 1);
	assertEquals(MyTest.class, el.getClassType());
	assertEquals("myTest", el.getName());
	assertEquals(null, el.getValue());
	obj = el.toObject();
	assertNotNull(obj);
	assertTrue(obj instanceof MyTest);
	assertEquals("myPar", ((MyTest)obj).getPar());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void shouldBuildTreeOfGroupElement() throws Exception {
	ElementBuilderImpl builder = getElementBuilder(true, false);
	ParameterizedType type = getTestCollectionType();
	
	Element el = builder.buildTree(type, "myTests", null, true);
	assertElementProps(el, false, true, false, false, true, false, false, 0);
	assertEquals(type, el.getClassType());
	assertEquals("myTests", el.getName());
	assertEquals(null, el.getValue());
	assertEquals(0, el.getChildrenCount());
	assertFalse(el.getChildren().hasNext());
	assertFalse(el.getChildrenIDs().hasNext());
	Element prototype = el.getPrototype();
	assertEquals(MyTest.class, prototype.getClassType());
	assertElementProps(prototype, false, false, false, false, true, false, false, 1);
	Object obj = el.toObject();
	assertNotNull(obj);
	assertTrue(obj instanceof Collection);
	assertTrue(((Collection<?>)obj).isEmpty());
	Element addedChild = el.incrementChildren();
	assertEquals(1, el.getChildrenCount());
	assertTrue(el.getChildren().hasNext());
	assertTrue(el.getChildrenIDs().hasNext());
	assertEquals(addedChild, el.getChild(addedChild.getId()));
	assertElementProps(addedChild, false, false, false, false, true, true, false, 1);
	obj = el.toObject();
	assertFalse(((Collection<?>)obj).isEmpty());
	assertNull(((Collection<MyTest>)obj).iterator().next().getPar());
	Element addedChild2 = el.incrementChildren();
	assertEquals(2, el.getChildrenCount());
	assertEquals(addedChild, el.getChild(addedChild.getId()));
	assertEquals(addedChild2, el.getChild(addedChild2.getId()));
	assertNotSame(addedChild.getId(), addedChild2.getId());
	assertNotSame(addedChild, addedChild2);
	Collection<String> ids = new LinkedList<String>();
	for (Iterator<String> it = el.getChildrenIDs(); it.hasNext(); ) {
	    ids.add(it.next());
	}
	assertThat(ids, hasItems(addedChild.getId(), addedChild2.getId()));
	obj = el.toObject();
	assertEquals(2, ((Collection<?>)obj).size());
	el.removeChild(addedChild.getId());
	assertEquals(1, el.getChildrenCount());
	assertNull(el.getChild(addedChild.getId()));
	assertNotNull(el.getChild(addedChild2.getId()));
	el.removeChild(addedChild2.getId());
	assertEquals(0, el.getChildrenCount());
	obj = el.toObject();
	assertNotNull(obj);
	assertTrue(obj instanceof Collection);
	assertTrue(((Collection<?>)obj).isEmpty());
	
	List<MyTest> value = new LinkedList<MyTest>();
	MyTest mt1 = new MyTest();
	MyTest mt2 = new MyTest();
	mt1.setPar("p1");
	mt2.setPar("p2");
	value.add(mt1);
	value.add(mt2);
	el = builder.buildTree(type, "myTests", value, true);
	assertElementProps(el, false, true, false, false, true, false, false, 2);
	assertEquals(type, el.getClassType());
	assertEquals("myTests", el.getName());
	assertEquals(null, el.getValue());
	for (Iterator<? extends Element> it = el.getChildren(); it.hasNext(); ) {
	    assertElementProps(it.next(), false, false, false, false, true, true, false, 1);
	}
	Object res = el.toObject();
	assertTrue(obj instanceof Collection);
	Collection<String> pars = new LinkedList<String>();
	for (MyTest mt : (Collection<MyTest>)res) {
	    pars.add(mt.getPar());
	}
	assertThat(pars, hasItems("p1", "p2"));
    }
    
    @Test
    public void shouldBuildTreeOfLazyElement() throws Exception {
	ElementBuilderImpl builder = getElementBuilder(true, false);
	Element el = builder.buildTree(MyLazyTest.class, "myLazyTest", null, true);
	assertElementProps(el, false, false, false, false, true, false, false, 2);
	assertEquals(MyLazyTest.class, el.getClassType());
	assertEquals("myLazyTest", el.getName());
	assertEquals(null, el.getValue());
	List<String> tmpList = new LinkedList<String>();
	for (Iterator<? extends Element> it = el.getChildren(); it.hasNext(); ) {
	    tmpList.add(it.next().getName());
	}
	assertThat(tmpList, hasItems("par", "ref"));
	Element refEl = el.getChildByName("ref");
	assertNotNull(refEl);
	assertElementProps(refEl, false, false, true, false, true, false, false, 1);
	assertEquals("ref", refEl.getName());
	Element resolvedRefEl = refEl.getChildren().next();
	assertElementProps(resolvedRefEl, false, false, false, false, true, false, false, 2);
	assertEquals("ref", resolvedRefEl.getName());
	Object obj = el.toObject();
	assertNotNull(obj);
	assertTrue(obj instanceof MyLazyTest);
	assertEquals(null, ((MyLazyTest)obj).getPar());
	Object refObj = ((MyLazyTest)obj).getRef();
	assertNotNull(refObj);
	assertTrue(refObj instanceof MyLazyTest);
	assertEquals(null, ((MyLazyTest)refObj).getPar());
	
	MyLazyTest value = new MyLazyTest();
	MyLazyTest refValue = new MyLazyTest();
	value.setPar("myPar");
	refValue.setPar("myRefPar");
	value.setRef(refValue);
	el = builder.buildTree(MyLazyTest.class, "myLazyTest", value, true);
	assertElementProps(el, false, false, false, false, true, false, false, 2);
	assertEquals(MyLazyTest.class, el.getClassType());
	assertEquals("myLazyTest", el.getName());
	assertEquals(null, el.getValue());
	obj = el.toObject();
	assertNotNull(obj);
	assertTrue(obj instanceof MyLazyTest);
	assertEquals("myPar", ((MyLazyTest)obj).getPar());
	refObj = ((MyLazyTest)obj).getRef();
	assertNotNull(refObj);
	assertTrue(refObj instanceof MyLazyTest);
	assertEquals("myRefPar", ((MyLazyTest)refObj).getPar());
    }
    
    private ParameterizedType getTestCollectionType() throws Exception {
	Method m = this.getClass().getMethod("helperListOfMyTestParMethod", List.class);
	Type[] types = m.getGenericParameterTypes();
	ParameterizedType pt = (ParameterizedType)types[0];
	assertTrue(Collection.class.isAssignableFrom((Class<?>)pt.getRawType()));
	assertTrue(MyTest.class.isAssignableFrom((Class<?>)pt.getActualTypeArguments()[0]));
	return pt;
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void incrementChildOperationOnNotGroupElementShouldCauseExceptionThrown() {
	ElementBuilderImpl builder = getElementBuilder(true, false);
	Element el = builder.buildTree(MyTest.class, "myTest", null, true);
	el.incrementChildren();
    }
    
    @Test(expected = WiseRuntimeException.class)
    public void removeChildOperationOfUnremovableElementShouldCauseExceptionThrown() {
	ElementBuilderImpl builder = getElementBuilder(true, false);
	Element el = builder.buildTree(MyTest.class, "myTest", null, true);
	Element child = el.getChildren().next();
	el.removeChild(child.getId());
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void incrementChildOperationOnLeafElementShouldCauseExceptionThrown() {
	ElementBuilderImpl builder = getElementBuilder(true, false);
	Element el = builder.buildTree(String.class, "myString", null, true);
	el.incrementChildren();
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void setValueOperationsOnNotLeafElementShouldCauseExceptionThrown() {
	ElementBuilderImpl builder = getElementBuilder(true, false);
	Element el = builder.buildTree(MyTest.class, "myTest", null, true);
	el.setValue("Foo");
    }
    
    private static ElementBuilderImpl getElementBuilder(boolean request, boolean useDefaults) {
	WSDynamicClientImpl mock = mock(WSDynamicClientImpl.class);
	mock.setClassLoader(ElementTest.class.getClassLoader());
//	when(mock.getClassLoaderInternal()).thenReturn();
	return new TestElementBuilder(mock, request, useDefaults);
    }
    
    private static void assertElementProps(Element el, boolean leaf, boolean group, boolean lazy, boolean nil, boolean nillable, boolean removable, boolean resolved, int childrenCount) {
	assertNotNull(el);
	assertEquals(leaf, el.isLeaf());
	assertEquals(group, el.isGroup());
	assertEquals(lazy, el.isLazy());
	assertEquals(nil, el.isNil());
	assertEquals(nillable, el.isNillable());
	assertEquals(removable, el.isRemovable());
	assertEquals(resolved, el.isResolved());
	assertNotNull(el.getId());
	assertEquals(group, el.getPrototype() != null);
	assertEquals(childrenCount, el.getChildrenCount());
    }
    
    private static class TestElementBuilder extends ElementBuilderImpl {
	
	public TestElementBuilder(WSDynamicClient client, boolean request, boolean useDefautValuesForNulls) {
	    super(client, request, useDefautValuesForNulls);
	}

	protected boolean isSimpleType(Class<?> cl, WSDynamicClient client) {
	    if (cl.isEnum() || cl.isPrimitive()) {
		return true;
	    }
	    final String pn = cl.getPackage().getName();
	    return  (pn.startsWith("java.") || pn.startsWith("javax."));
	}

    }
    
    public static class MyTest {
	private String par;
	
	public MyTest() {
	}
	public String getPar() {
	    return par;
	}
	public void setPar(String par) {
	    this.par = par;
	}
    }
    
    public static class MyLazyTest {
	private MyLazyTest ref;
	private String par;
	
	public MyLazyTest() {
	}
	public String getPar() {
	    return par;
	}
	public void setPar(String par) {
	    this.par = par;
	}
	public MyLazyTest getRef() {
	    return ref;
	}

	public void setRef(MyLazyTest ref) {
	    this.ref = ref;
	}
    }
    
    public void helperListOfMyTestParMethod(List<MyTest> parameter) {
	//NOOP, required for testing of group elements below
    }
}
