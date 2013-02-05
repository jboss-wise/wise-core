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
package org.jboss.wise.core.consumer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import org.jboss.wise.core.exception.WiseRuntimeException;

/**
 * @author stefano.maestri@javalinux.it
 */
public abstract class WSConsumer {

    private boolean keepSource;
    private boolean verbose;

    public abstract List<String> importObjectFromWsdl( String wsdlURL,
                                                       File outputDir,
                                                       File sourceDir,
                                                       String targetPackage,
                                                       List<File> bindingFiles,
                                                       PrintStream messageStream,
                                                       File catalog ) throws MalformedURLException, WiseRuntimeException;

    /**
     * @return verbose
     */
    public final boolean isVerbose() {
        return verbose;
    }

    /**
     * @param verbose Sets verbose to the specified value.
     */
    public final void setVerbose( boolean verbose ) {
        this.verbose = verbose;
    }

    /**
     * @return keepSource
     */
    public final boolean isKeepSource() {
        return keepSource;
    }

    /**
     * @param keepSource Sets keepSource to the specified value.
     */
    public final void setKeepSource( boolean keepSource ) {
        this.keepSource = keepSource;
    }

    /*
     * Gets an array containing the generated class names
     *
     * @param outputDir 
     * @return the List of of generated className qualifiedName
     */
    public List<String> getClassNames( File outputDir,
                                       String targetPackage ) throws WiseRuntimeException {
        if (targetPackage == null || targetPackage.trim().length() == 0) {
            return this.getClassNames(outputDir);
        }

        List<String> classNames = new LinkedList<String>();
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept( File dir,
                                   String name ) {
                return name.endsWith(".class");
            }
        };
        File scanDir = new File(new StringBuilder(outputDir.getAbsolutePath()).append(File.separator)
        	.append(targetPackage.replaceAll("\\.", File.separator)).append(File.separator).toString());
        String[] children = scanDir.list(filter);
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                classNames.add(targetPackage + "." + children[i].substring(0, children[i].length() - 6));
            }
        }
        if (classNames.size() == 0) {
            throw new WiseRuntimeException("No classs found in dir " + outputDir + " for targetPackage " + targetPackage);
        }
        return classNames;

    }

    public List<String> getClassNames( File outputDir ) throws WiseRuntimeException {
        List<String> classNames = this.getClassNames(outputDir, outputDir);
        if (classNames.size() == 0) {
            throw new WiseRuntimeException("No classs found in dir " + outputDir + " for unspecified targetPackage");
        }
        return classNames;
    }

    private List<String> getClassNames( File outputDir,
                                        File parentDir ) {
        LinkedList<String> classNames = new LinkedList<String>();
        File[] files = parentDir.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                classNames.addAll(this.getClassNames(outputDir, file));
            } else {
                String className = file.getPath();
                if (className.endsWith(".class")) {
                    className = className.substring(outputDir.getPath().length() + 1, className.length() - 6);
                    className = className.replace(File.separatorChar, '.');
                    classNames.add(className);
                }
            }

        }

        return classNames;
    }

}
