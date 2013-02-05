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

import java.io.IOException;
import java.io.OutputStream;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

public class JBossLoggingOutputStream extends OutputStream {

    public static final int DEFAULT_BUFFER_LENGTH = 2048;
    
    protected boolean hasBeenClosed = false;
    protected byte[] buf;
    protected int count;
    private int bufLength;
    protected Logger logger;
    protected Level level;

    public JBossLoggingOutputStream(Logger log, Level level) throws IllegalArgumentException {
	if (log == null) {
	    throw new IllegalArgumentException("Null category!");
	}
	if (level == null) {
	    throw new IllegalArgumentException("Null priority!");
	}
	this.level = level;
	logger = log;
	bufLength = DEFAULT_BUFFER_LENGTH;
	buf = new byte[DEFAULT_BUFFER_LENGTH];
	count = 0;
    }

    public void close() {
	flush();
	hasBeenClosed = true;
    }

    public void write(final int b) throws IOException {
	if (hasBeenClosed) {
	    throw new IOException("The stream has been closed.");
	}
	// would this be writing past the buffer?
	if (count == bufLength) {
	    // grow the buffer
	    final int newBufLength = bufLength + DEFAULT_BUFFER_LENGTH;
	    final byte[] newBuf = new byte[newBufLength];
	    System.arraycopy(buf, 0, newBuf, 0, bufLength);
	    buf = newBuf;
	    bufLength = newBufLength;
	}
	buf[count] = (byte) b;
	count++;
    }

    public void flush() {
	if (count == 0) {
	    return;
	}
	// don't print out blank lines; flushing from PrintStream puts
	// out these
	// For linux system
	if (count == 1 && ((char) buf[0]) == '\n') {
	    reset();
	    return;
	}
	// For mac system
	if (count == 1 && ((char) buf[0]) == '\r') {
	    reset();
	    return;
	}
	// On windows system
	if (count == 2 && (char) buf[0] == '\r' && (char) buf[1] == '\n') {
	    reset();
	    return;
	}
	final byte[] theBytes = new byte[count];
	System.arraycopy(buf, 0, theBytes, 0, count);
	logger.log(level, new String(theBytes));
	reset();
    }

    private void reset() {
	// not resetting the buffer -- assuming that if it grew then it
	// will likely grow similarly again
	count = 0;
    }
}
