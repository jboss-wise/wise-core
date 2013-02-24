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
package org.jboss.wise.tree;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Iterator;

/**
 * @author alessio.soldano@jboss.com
 * 
 */
public interface Element extends Serializable, Cloneable {

    public boolean isLeaf();
    
    public boolean isRemovable();

    public Type getClassType();

    public String getName();

    public boolean isNil();

    public void setNil(boolean nil);

    public String getId();

    public boolean isNillable();
    
    public boolean isGroup();

    public void removeChild(String id);
    
    public Element getChild(String id);
    
    public Element getChildByName(String name);
    
    public Iterator<String> getChildrenIDs();
    
//    public Iterator<String> getChildrenIDs(boolean resolve);
    
    public Iterator<? extends Element> getChildren();
    
//    public Iterator<? extends Element> getChildren(boolean resolve);
    
    public String getValue();

    public void setValue(String value);
    
    public Element getPrototype();

    public Element incrementChildren();
    
    public int getChildrenCount();
    
    public boolean isLazy();
    
    public boolean isResolved();
    
    /**
     * Every WiseTreeElement must be cloneable; this is required to handle
     * element's add and removal into/from arrays and collections.
     */
    public Element clone();

    /**
     * This is required to convert a tree element into the corresponding object
     * instance.
     * 
     * @return The object corresponding to this element
     */
    public Object toObject();

}
