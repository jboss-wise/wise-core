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
package org.jboss.wise.core.handlers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 * This simple SOAPHandler will output the contents of incoming and outgoing
 * messages. Check the MESSAGE_OUTBOUND_PROPERTY in the context to see if this
 * is an outgoing or incoming message. Write a brief message to the print stream
 * and output the message.
 * 
 * @author Stefano Maestri, stefano.maestri@javalinux.it
 */
@ThreadSafe
@Immutable
public class LoggingHandler implements SOAPHandler<SOAPMessageContext> {

    private final PrintStream outputStream;

    private final Logger logger;

    private final Level level;

    /**
     * Default constructor using default System.out PrintStream to print message
     */
    public LoggingHandler() {
	outputStream = System.out;
	logger = null;
	level = Level.ALL;
    }

    /**
     * Constructor for custom PrintStream outputter
     * 
     * @param outStream
     *            the PrintStream to use to print messages.
     */
    public LoggingHandler(PrintStream outStream) {
	this.outputStream = outStream;
	logger = null;
	level = Level.ALL;
    }

    public LoggingHandler(Logger logger, Level level) {
	this.outputStream = null;
	this.logger = logger;
	this.level = level;
    }

    public Set<QName> getHeaders() {
	return new HashSet<QName>(); // empty set
    }

    public boolean handleMessage(SOAPMessageContext smc) {
	logToSystemOut(smc);
	return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
	logToSystemOut(smc);
	return true;
    }

    // nothing to clean up
    public void close(MessageContext messageContext) {
    }

    private void logToSystemOut(SOAPMessageContext smc) {
	Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

	if (outboundProperty.booleanValue()) {
	    if (outputStream != null) {
		outputStream.println("\nOutbound message:");
	    }
	    if (logger != null) {
		logger.log(level, "\nOutbound message:");
	    }
	} else {
	    if (outputStream != null) {
		outputStream.println("\nInbound message:");
	    }
	    if (logger != null) {
		logger.log(level, "\nInbound message:");
	    }
	}
	SOAPMessage message = smc.getMessage();
	try {
	    if (outputStream != null) {
		message.writeTo(outputStream);
		outputStream.println(""); // just to add a newline
	    }
	    if (logger != null) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		message.writeTo(baos);
		logger.log(level, baos);
	    }
	} catch (Exception e) {
	    if (outputStream != null) {
		outputStream.println("Exception in handler: " + e);
	    }
	    if (logger != null) {
		logger.log(level, "Exception in handler: " + e);
	    }
	}
    }

}
