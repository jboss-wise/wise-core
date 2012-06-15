/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.wise.core.client.impl.wsdlResolver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.jboss.logging.Logger;
import org.jboss.wise.core.utils.IDGenerator;
import org.jboss.ws.common.Constants;
import org.jboss.ws.common.DOMUtils;
import org.jboss.ws.common.DOMWriter;
import org.jboss.ws.common.IOUtils;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;

/**
 * A class that resolves wsdl files and stores them locally
 * 
 * @author alessio.soldano@jboss.com
 * @since 13-May-2009
 * 
 */
public class WSDLResolver {

    private static Logger log = Logger.getLogger(WSDLResolver.class);

    private final String tmpDir;

    private final Connection connection;

    public WSDLResolver(String tmpDir) {
	this(tmpDir, new Connection(null, null));
    }

    public WSDLResolver(String tmpDir, Connection connection) {
	this.tmpDir = tmpDir;
	this.connection = connection;
    }

    public File retrieveWsdlFile(URL wsdlURL) throws Exception {
	String url = wsdlURL.toString();
	String filename = url.substring(url.lastIndexOf('/')).replaceAll("\\?", "-");
	File wsdlFile = new File(tmpDir, filename);
	wsdlFile.getParentFile().mkdirs();

	Writer fWriter = null;
	try {
	    fWriter = IOUtils.getCharsetFileWriter(wsdlFile, Constants.DEFAULT_XML_CHARSET);

	    Definition def = getWsdlDefinition(wsdlURL);

	    URL savedWsdlURL = wsdlFile.toURL();
	    log.info("WSDL saved to: " + savedWsdlURL);

	    // Process the wsdl imports
	    Map<String, String> saved = new HashMap<String, String>();
	    saveWsdlImports(savedWsdlURL, def, saved, wsdlURL);

	    // Publish XMLSchema imports
	    for (Element documentElement : getSchemaElements(def)) {
		saveSchemaImports(wsdlFile.toURL(), documentElement, saved, wsdlURL);
	    }

	    // save modified file
	    writeDefinition(def, fWriter);

	    return wsdlFile;
	} catch (Exception e) {
	    log.error("Cannot save wsdl to: " + wsdlFile);
	    throw e;
	} finally {
	    if (fWriter != null) {
		fWriter.close();
	    }
	}
    }

    private static void writeDefinition(Definition wsdlDefinition, Writer writer) throws WSDLException, IOException {
	WSDLFactory wsdlFactory = WSDLFactory.newInstance();
	javax.wsdl.xml.WSDLWriter wsdlWriter = wsdlFactory.newWSDLWriter();
	wsdlWriter.writeWSDL(wsdlDefinition, writer);
	writer.close();
    }

    private Definition getWsdlDefinition(URL wsdlLocation) throws WSDLException {
	if (wsdlLocation == null)
	    throw new IllegalArgumentException("URL cannot be null");

	log.info("Getting wsdl definition from: " + wsdlLocation.toExternalForm());

	EntityResolver entityResolver = new WiseEntityResolver(connection);
	//WSDLFactory wsdlFactory = WSDLFactory.newInstance(JBossWSDLFactoryImpl.class.getName(), this.getClass().getClassLoader());
	WSDLFactory wsdlFactory = WSDLFactory.newInstance();
	WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
	wsdlReader.setFeature("javax.wsdl.verbose", false);

	return wsdlReader.readWSDL(new WSDLLocatorImpl(entityResolver, wsdlLocation, connection));
    }

    private List<Element> getSchemaElements(Definition definition) throws WSDLException {
	Types types = definition.getTypes();
	List<Element> result = new LinkedList<Element>();
	if (types != null && types.getExtensibilityElements().size() > 0) {
	    List extElements = types.getExtensibilityElements();
	    int len = extElements.size();
	    for (int i = 0; i < len; i++) {
		ExtensibilityElement extElement = (ExtensibilityElement) extElements.get(i);
		Element domElement;
		if (extElement instanceof Schema) {
		    domElement = ((Schema) extElement).getElement();
		} else if (extElement instanceof UnknownExtensibilityElement) {
		    domElement = ((UnknownExtensibilityElement) extElement).getElement();
		} else {
		    throw new WSDLException(WSDLException.OTHER_ERROR, "Unsupported extensibility element: " + extElement);
		}
		result.add(domElement);
	    }
	}
	return result;
    }

    /**
     * Save the wsdl imports for a given wsdl definition
     * 
     * @param parentURL
     * @param parentDefinition
     * @param saved
     * @param wsdlURL
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void saveWsdlImports(URL parentURL, Definition parentDefinition, Map<String, String> saved, URL wsdlURL) throws Exception {
	String baseURI = parentURL.toExternalForm();

	Iterator it = parentDefinition.getImports().values().iterator();
	while (it.hasNext()) {
	    for (Import wsdlImport : (List<Import>) it.next()) {
		String locationURI = wsdlImport.getLocationURI();
		log.info("Processing wsdl import: " + locationURI);
		Definition subdef = wsdlImport.getDefinition();

		// infinity loops prevention
		if (saved.keySet().contains(locationURI)) {
		    continue;
		}

		URL targetURL = new URL(baseURI.substring(0, baseURI.lastIndexOf("/") + 1) + "wsdl-" + IDGenerator.nextVal() + ".wsdl");
		String newLocationURI = targetURL.getPath();
		File targetFile = new File(newLocationURI);
		log.debug("targetFile: " + targetFile);
		targetFile.getParentFile().mkdirs();
		saved.put(locationURI, newLocationURI);
		saved.put(newLocationURI, newLocationURI);
		// update the current referrer
		wsdlImport.setLocationURI(newLocationURI);

		// recursively save imports
		saveWsdlImports(parentURL, subdef, saved, wsdlURL);

		// Save XMLSchema imports
		String baseWsdlURI = wsdlURL.toExternalForm();
		for (Element domElement : getSchemaElements(subdef)) {
		    saveSchemaImports(parentURL, domElement, saved, new URL(baseWsdlURI.substring(0, baseWsdlURI.lastIndexOf("/") + 1) + locationURI));
		}

		// save modified file
		WSDLFactory wsdlFactory = WSDLFactory.newInstance();
		javax.wsdl.xml.WSDLWriter wsdlWriter = wsdlFactory.newWSDLWriter();
		FileWriter fw = new FileWriter(targetFile);
		try {
		    wsdlWriter.writeWSDL(subdef, fw);
		} finally {
		    fw.close();
		}

		log.debug("WSDL import saved to: " + targetURL);
	    }
	}
    }

    /**
     * Save the schema imports for a given wsdl definition
     * 
     * @param parentURL
     * @param element
     * @param saved
     * @param referrerURL
     * @throws Exception
     */
    private void saveSchemaImports(URL parentURL, Element element, Map<String, String> saved, URL referrerURL) throws Exception {
	String baseURI = parentURL.toExternalForm();

	Iterator<Element> it = DOMUtils.getChildElements(element);
	while (it.hasNext()) {
	    Element childElement = it.next();
	    if ("import".equals(childElement.getLocalName()) || "include".equals(childElement.getLocalName())) {
		String schemaLocation = childElement.getAttribute("schemaLocation");
		if (schemaLocation.length() > 0) {
		    log.info("Processing schema import: " + schemaLocation);
		    if (saved.keySet().contains(schemaLocation)) {
			continue;
		    }

		    URL xsdURL = new URL(baseURI.substring(0, baseURI.lastIndexOf("/") + 1) + "schema-" + IDGenerator.nextVal() + ".xsd");
		    String newSchemaLocation = xsdURL.getPath();
		    File targetFile = new File(newSchemaLocation);
		    targetFile.getParentFile().mkdirs();
		    saved.put(schemaLocation, newSchemaLocation);
		    // update the current referrer
		    childElement.setAttribute("schemaLocation", newSchemaLocation);

		    // get referenced schema
		    URL schemaURL = new URL(referrerURL, schemaLocation);
		    InputStream is = null;
		    Element subDoc = null;
		    try {
			is = connection.open(schemaURL);
			subDoc = DOMUtils.parse(is);
		    } finally {
			try {
			    is.close();
			} catch (Exception ignore) {
			}
		    }
		    // recursively save imports
		    saveSchemaImports(parentURL, subDoc, saved, schemaURL);

		    // save modified file
		    FileOutputStream fos = new FileOutputStream(targetFile);
		    try {
			DOMWriter domWriter = new DOMWriter(fos);
			domWriter.print(subDoc);
			log.debug("XMLSchema import saved to: " + xsdURL);
		    } finally {
			try {
			    fos.close();
			} catch (Exception ignore) {
			}
		    }
		}
	    } else {
		saveSchemaImports(parentURL, childElement, saved, referrerURL);
	    }
	}
    }

}
