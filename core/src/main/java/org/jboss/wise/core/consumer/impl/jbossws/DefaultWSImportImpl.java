/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.wise.core.consumer.impl.jbossws;

import java.io.File;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import net.jcip.annotations.ThreadSafe;

import org.jboss.wise.core.consumer.WSConsumer;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.ws.api.tools.WSContractConsumer;

/**
 * @author alessio.soldano@jboss.com
 * @author stefano.maestri@javalinux.it
 * 
 */
@ThreadSafe
public class DefaultWSImportImpl extends WSConsumer {

    public DefaultWSImportImpl() {
	
    }

    public DefaultWSImportImpl(boolean keepSource, boolean verbose) {
	super();
	this.setKeepSource(keepSource);
	this.setVerbose(verbose);
    }

    @Override
    public synchronized List<String> importObjectFromWsdl(String wsdlURL, File outputDir, File sourceDir, String targetPackage, List<File> bindingFiles, PrintStream messageStream, File catelog) throws MalformedURLException, WiseRuntimeException {
	WSContractConsumer wsImporter = WSContractConsumer.newInstance(getContextClassLoader());

	if (targetPackage != null && targetPackage.trim().length() > 0) {
	    wsImporter.setTargetPackage(targetPackage);
	}

	wsImporter.setGenerateSource(this.isKeepSource());
	wsImporter.setOutputDirectory(outputDir);
	wsImporter.setSourceDirectory(sourceDir);
	
	if (this.isVerbose()) {
	    wsImporter.setMessageStream(System.out);
	}
	
	if (messageStream != null) {
	    wsImporter.setMessageStream(messageStream);
	}

	if (bindingFiles != null && bindingFiles.size() > 0) {
	    wsImporter.setBindingFiles(bindingFiles);
	}

	if (catelog != null) {
	    wsImporter.setCatalog(catelog);
	}
	
	wsImporter.setTarget("2.1"); //workaround for WISE-179
	
	runWSConsume(wsImporter, wsdlURL);
	return this.getClassNames(outputDir, targetPackage);
    }
    
    protected void runWSConsume(WSContractConsumer wsImporter, String wsdlURL) throws MalformedURLException {
	wsImporter.consume(wsdlURL);
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
