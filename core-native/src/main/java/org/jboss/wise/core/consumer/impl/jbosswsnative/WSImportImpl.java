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

package org.jboss.wise.core.consumer.impl.jbosswsnative;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.jcip.annotations.ThreadSafe;

import org.jboss.wise.core.consumer.impl.jbossws.DefaultWSImportImpl;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.wsf.spi.tools.WSContractConsumer;

/**
 * @author stefano.maestri@javalinux.it
 * @author alessio.soldano@jboss.com
 */
@ThreadSafe
public class WSImportImpl extends DefaultWSImportImpl {

    private final ProviderChanger providerChanger;

    public WSImportImpl() {
	super();
	providerChanger = new ProviderChanger();
    }

    // for test purpose
    WSImportImpl(ProviderChanger changer) {
	super();
	providerChanger = changer;
    }

    public WSImportImpl(boolean keepSource, boolean verbose) {
	super();
	providerChanger = new ProviderChanger();
	this.setKeepSource(keepSource);
	this.setVerbose(verbose);
    }

    @Override
    protected void runWSConsume(WSContractConsumer wsImporter, String wsdlURL) throws MalformedURLException {
	try {
	    // NEEDED for WISE-36 issue
	    providerChanger.changeProvider();
	    wsImporter.consume(wsdlURL);
	} finally {
	    // NEEDED for WISE-36 issue
	    providerChanger.restoreDefaultProvider();
	}
    }
    
    @Override
    protected List<String> defineAdditionalCompilerClassPath() throws WiseRuntimeException {
	return super.defineAdditionalCompilerClassPath();
    }

    public class ProviderChanger {

	private final ClassLoader defaultCl = Thread.currentThread().getContextClassLoader();

	public void changeProvider() {
	    URL[] urls = {};
	    Thread.currentThread().setContextClassLoader(new WiseForceToolsProviderClassLoader(urls, defaultCl));
	}

	public void restoreDefaultProvider() {
	    Thread.currentThread().setContextClassLoader(defaultCl);
	}
    }
}
