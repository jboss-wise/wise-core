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
import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.wise.core.exception.WiseRuntimeException;

/**
 * IO utilities
 * 
 * @author stefano.maestri@javalinux.it
 */
public final class IOUtils {
    
    /**
     * True if the given type name is the source notation of a primitive or array of which.
     * 
     * @param outs
     * @param ins
     * @throws WiseRuntimeException
     */
    public static void copyStreamAndClose( OutputStream outs,
                                    InputStream ins ) throws WiseRuntimeException {
        try {
            byte[] bytes = new byte[1024];
            int r = ins.read(bytes);
            while (r > 0) {
                outs.write(bytes, 0, r);
                r = ins.read(bytes);
            }
        } catch (Exception e) {
            throw new WiseRuntimeException(e);
        } finally {
            try {
                if (outs != null) {
                    outs.close();
                }
            } catch (IOException e) {
                // suppressed
            }

            try {
                if (ins != null) {
                    ins.close();
                }
            } catch (IOException e) {
            }

        }
    }
}
