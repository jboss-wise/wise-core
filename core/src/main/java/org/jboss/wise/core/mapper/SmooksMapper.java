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
package org.jboss.wise.core.mapper;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;

import javax.xml.transform.Source;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import org.apache.log4j.Logger;
import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.exception.MappingException;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.milyn.Smooks;
import org.milyn.SmooksUtil;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.SmooksResourceConfigurationList;
import org.milyn.cdr.XMLConfigDigester;
import org.milyn.container.ExecutionContext;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.payload.JavaResult;
import org.milyn.payload.JavaSource;
import org.milyn.profile.DefaultProfileSet;
import org.milyn.profile.ProfileStore;
import org.milyn.profile.UnknownProfileMemberException;
import org.milyn.resource.URIResourceLocator;

/**
 * A WiseMapper based on smooks
 * 
 * @author stefano.maestri@javalinux.it
 */
@ThreadSafe
@Immutable
public class SmooksMapper implements WiseMapper {

    private final Logger log = Logger.getLogger(SmooksMapper.class);

    private final Smooks smooks;

    private final String smooksReport;

    public SmooksMapper(String smooksResource, WSDynamicClient client) {
	this(smooksResource, null, client);
    }

    public SmooksMapper(String smooksResource, String smooksReport, WSDynamicClient client) {
	ClassLoader oldLoader = getContextClassLoader();

	try {
	    setContextClassLoader(client.getClassLoader());

	    smooks = client.getSmooksInstance();
	    try {
		ProfileStore profileStore = smooks.getApplicationContext().getProfileStore();
		profileStore.getProfileSet(Integer.toString(this.hashCode()));
	    } catch (UnknownProfileMemberException e) {

		synchronized (SmooksMapper.class) {
		    // Register the message flow within the Smooks context....
		    SmooksUtil
			    .registerProfileSet(DefaultProfileSet.create(Integer.toString(this.hashCode()), new String[] {}), smooks);
		}
	    }
	    SmooksResourceConfigurationList list = XMLConfigDigester.digestConfig(new URIResourceLocator()
		    .getResource(smooksResource), "wise");
	    for (int i = 0; i < list.size(); i++) {
		SmooksResourceConfiguration smookResourceElement = list.get(i);
		smookResourceElement.setTargetProfile(Integer.toString(this.hashCode()));
		SmooksUtil.registerResource(smookResourceElement, smooks);
	    }
	    // smooks.addConfigurations(Integer.toString(this.hashCode()), new
	    // URIResourceLocator().getResource(smooksResource));

	    this.smooksReport = smooksReport;
	} catch (Exception e) {
	    throw new WiseRuntimeException("failde to create SmooksMapper", e);
	} finally {
	    // restore the original classloader
	    setContextClassLoader(oldLoader);
	}
    }

    // package protected for test purpose
    /* package */ExecutionContext initExecutionContext(String smooksReport) {
	ExecutionContext executionContext = smooks.createExecutionContext(Integer.toString(this.hashCode()));
	if (smooksReport != null) {
	    try {
		executionContext.setEventListener(new HtmlReportGenerator(smooksReport));
	    } catch (IOException e) {
		if (log.isDebugEnabled()) {
		    log
			    .debug("Error during loading/instanciating Html report generator (" + smooksReport + ") with exception message: " + e
				    .getMessage());
		    log.info("Wise will continue without it");

		}
	    }
	}
	return executionContext;

    }

    /**
     * apply this mapping to original object
     * 
     * @param originalObjects
     * @return Map returned is typically used to invoke webservice operations.
     *         To do this, beanids defined in smooks config (and used here as
     *         Map's keys) have to be the parameters names as defined in
     *         wsdl/wsconsume generated classes
     * @throws MappingException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> applyMapping(Object originalObjects) throws MappingException {
	ExecutionContext executionContext = initExecutionContext(smooksReport);
	Source source = new JavaSource(originalObjects);
	JavaResult result = new JavaResult();

	smooks.filterSource(executionContext, source, result);

	Map<String, Object> returnMap = result.getResultMap();

	// workaround when we should use smooks to extract a single value
	// Have a look to SmooksMapperTest.shouldMapToPrimitiveInput() for an
	// example of use
	if (returnMap.get("primitiveInputs") != null) {
	    returnMap = (Map<String, Object>) returnMap.get("primitiveInputs");
	}
	return returnMap;

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
    
    private static ClassLoader setContextClassLoader(final ClassLoader cl)
    {
       if (System.getSecurityManager() == null)
       {
          ClassLoader result = Thread.currentThread().getContextClassLoader();
          if (cl != null)
             Thread.currentThread().setContextClassLoader(cl);
          return result;
       }
       else
       {
          try
          {
             return AccessController.doPrivileged(new PrivilegedExceptionAction<ClassLoader>() {
                public ClassLoader run() throws Exception
                {
                   try
                   {
                      ClassLoader result = Thread.currentThread().getContextClassLoader();
                      if (cl != null)
                         Thread.currentThread().setContextClassLoader(cl);
                      return result;
                   }
                   catch (Exception e)
                   {
                      throw e;
                   }
                   catch (Error e)
                   {
                      throw e;
                   }
                   catch (Throwable e)
                   {
                      throw new RuntimeException("ERROR_SETTING_CONTEXT_CLASSLOADER",  e);
                   }
                }
             });
          }
          catch (PrivilegedActionException e)
          {
             throw new RuntimeException("ERROR_RUNNING_PRIVILEGED_ACTION",  e.getCause());
          }
       }
    }

}
