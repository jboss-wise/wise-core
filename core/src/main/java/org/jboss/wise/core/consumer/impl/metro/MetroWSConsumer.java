/*
 * JBoss, Home of Professional Open Source Copyright 2006, JBoss Inc., and
 * individual contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of individual
 * contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package org.jboss.wise.core.consumer.impl.metro;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import org.apache.log4j.Logger;
import org.jboss.wise.core.consumer.WSConsumer;
import org.jboss.wise.core.exception.WiseRuntimeException;

public class MetroWSConsumer extends WSConsumer {
    private String metroHome;
    private final Logger log = Logger.getLogger(MetroWSConsumer.class);

    @Override
    public List<String> importObjectFromWsdl( String wsdlURL,
                                              File outputDir,
                                              File sourceDir,
                                              String targetPackage,
                                              List<File> bindingFiles,
                                              PrintStream messageStream,
                                              File catelog) throws MalformedURLException, WiseRuntimeException {
        ClassLoader oldClassLoader = getContextClassLoader();
        LocalFirstClassLoader metroClassLoader = new LocalFirstClassLoader(getMertroJars(), oldClassLoader);
        try {

            Class<?> wsImportClaz = metroClassLoader.loadClass("com.sun.tools.ws.WsImport");
            Method mainMethod = wsImportClaz.getDeclaredMethod("doMain", new Class[] {String[].class});
            List<String> args = new java.util.ArrayList<String>();
            args.add("-keep");
            if (targetPackage != null && targetPackage.trim().length() > 0) {
                args.add("-p");
                args.add(targetPackage);
            }
            
            if (bindingFiles != null) {
                for (File bindingFile : bindingFiles) {
                    args.add("-b");
                    args.add(bindingFile.getAbsolutePath());
                }
            }
            
            if (catelog != null) {
                args.add("-catelog");
                args.add(catelog.getAbsolutePath());
            }
            
            args.add("-d");
            args.add(outputDir.getAbsolutePath());
            args.add("-s");
            args.add(outputDir.getAbsolutePath());
            if (this.isVerbose()) {
                args.add("-verbose");
            }
            args.add(wsdlURL);
            mainMethod.invoke(null, new Object[] {args.toArray(new String[] {})});
        } catch (Exception e) {
            log.error("Failed to load metro wsimport to generate jaxws classes for wsdl " + wsdlURL, e);
            throw new WiseRuntimeException("Failed to load metro wsimport to generate jaxws classes for wsdl" + wsdlURL, e);
        }
        return this.getClassNames(outputDir, targetPackage);

    }

    public void setMetroHome( String value ) {
        this.metroHome = value;
    }

    public String getMetroHome() {
        return this.metroHome;
    }

    public URL[] getMertroJars() throws WiseRuntimeException {

        List<URL> urls = new java.util.ArrayList<URL>();
        if (this.getMetroHome() != null) {
            String metroLibPath = getMetroHome().endsWith(File.separator) ? getMetroHome() + "lib" : getMetroHome() + "/lib";
            File metroLib = new File(metroLibPath);
            String[] jars = metroLib.list(new FilenameFilter() {
                public boolean accept( File dir,
                                       String name ) {
                    if (name.endsWith(".jar")) {
                        return true;
                    }
                    return false;
                }

            });
            if (jars == null) {
                log.error("Not found the metro jar files, plese check the metroLibPath setting");
                throw new WiseRuntimeException("Not found the metro jar files, plese check the metroLibPath setting");
            }
            for (String jar : jars) {
                File jarFile = new File(metroLibPath + "/" + jar);
                try {
                    urls.add(jarFile.toURI().toURL());
                } catch (MalformedURLException e) {
                    log.error("Failed to getURL from the metro jar file ", e);
                }
            }
        } else {
            throw new WiseRuntimeException("Metro home is not set.");
        }
        return urls.toArray(new URL[] {});

    }

    private static ClassLoader getContextClassLoader()
    {
       SecurityManager sm = System.getSecurityManager();
       if (sm == null)
       {
          return Thread.currentThread().getContextClassLoader();
       }
       else
       {
          return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
             public ClassLoader run()
             {
                return Thread.currentThread().getContextClassLoader();
             }
          });
       }
    }
}

