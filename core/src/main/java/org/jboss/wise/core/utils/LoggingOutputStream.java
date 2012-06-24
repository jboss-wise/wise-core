package org.jboss.wise.core.utils;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * An OutputStream that flushes out to a Logger
 */
public class LoggingOutputStream extends OutputStream {

    public static final int DEFAULT_BUFFER_LENGTH = 2048;
    
    protected boolean hasBeenClosed = false;
    protected byte[] buf;
    protected int count;
    private int bufLength;
    protected Logger logger;
    protected Level level;

    public LoggingOutputStream(Logger log, Level level) throws IllegalArgumentException {
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
