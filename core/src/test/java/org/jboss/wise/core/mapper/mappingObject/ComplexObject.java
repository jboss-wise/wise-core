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
package org.jboss.wise.core.mapper.mappingObject;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author stefano.maestri@javalinux.it
 */
public class ComplexObject {
    private XMLGregorianCalendar dateField;

    /**
     * @return dateField
     */
    public final XMLGregorianCalendar getDateField() {
        return dateField;
    }

    /**
     * @param dateField Sets dateField to the specified value.
     */
    public final void setDateField(XMLGregorianCalendar dateField) {
        this.dateField = dateField;
    }

    private String textField;

    private Integer numberField;

    /**
     * @return textField
     */
    public final String getTextField() {
        return textField;
    }

    /**
     * @param textField Sets textField to the specified value.
     */
    public final void setTextField(String textField) {
        this.textField = textField;
    }

    /**
     * @return numberField
     */
    public final Integer getNumberField() {
        return numberField;
    }

    /**
     * @param numberField Sets numberField to the specified value.
     */
    public final void setNumberField(Integer numberField) {
        this.numberField = numberField;
    }

}
