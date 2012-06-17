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

package org.jboss.wise.core.test;

import java.io.File;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.jboss.wsf.spi.SPIProvider;
import org.jboss.wsf.spi.SPIProviderResolver;
import org.jboss.wsf.spi.deployer.Deployer;

/**
 * Wise test base class. Subclass can use the methods in this class to 
 * deploy and undeploy a web service war in JBossAS
 *   
 * @author ema@redhat.com
 * @author alessio.soldano@jboss.com
 *
 */
public class WiseTest {
    private static final String TEST_WS_ARCHIVE_DIR = "test-ws-archive";
    private static final String SYSPROP_JBOSS_BIND_ADDRESS = "jboss.bind.address";
    private static final String SYSPROP_JBOSS_HTTP_PORT = "jboss.http.port";
    private static Deployer DEPLOYER;
    
    private static synchronized Deployer getDeployer()
    {
       //lazy loading of deployer
       if (DEPLOYER == null)
       {
          SPIProvider spiProvider = SPIProviderResolver.getInstance().getProvider();
          DEPLOYER = spiProvider.getSPI(Deployer.class);
       }
       return DEPLOYER;
    }

    /**
     * Deploy the webservice war in JBoss server
     * @param url url for webservice war 
     * @throws Exception if the deployment is failed
     */
    public void deployWS(URL url) throws Exception {
	getDeployer().deploy(url);
    }
    
    /**Undeploy a webservice
     * @param url url of webservice war 
     * @throws Exception if undeployment is failed
     */
    public void undeployWS(URL url) throws Exception {
	getDeployer().undeploy(url);       
    }
    
    /**Get the URL path for a given webservice archive. It will find this war file under ${baseDir}/build/test-ws-archive
     * @param archiveName the webservice archive name
     * @return webservice war url
     */
    public URL getArchiveUrl( String archiveName ) {
         URL warUrl = null;
         URL dirURL = this.getClass().getClassLoader().getResource(".");
         File file = new File(dirURL.getFile(), ".." + File.separator + TEST_WS_ARCHIVE_DIR + File.separator + archiveName);
         if (file.exists()) {
             try {
                 warUrl = file.getAbsoluteFile().toURI().toURL();
             } catch (MalformedURLException e) {
                 return null;
             }
         }
         return warUrl;

     }
    
    /**
     * Get the jboss webservice server side hostname and port
     * 
     * @return http://server-hostname:port
     */
    public String getServerHostAndPort() {
	final String host = System.getProperty(SYSPROP_JBOSS_BIND_ADDRESS, "localhost");
	final String port = System.getProperty(SYSPROP_JBOSS_HTTP_PORT, "8080");
	final StringBuilder sb = new StringBuilder("http://");
	sb.append(toIPv6URLFormat(host)).append(":").append(port);
	return sb.toString();
    }

    private static String toIPv6URLFormat(final String host) {
	try {
	    if (host.startsWith(":")) {
		throw new IllegalArgumentException("JBossWS test suite requires IPv6 addresses to be wrapped with [] brackets. Expected format is: [" + host + "]");
	    }
	    if (host.startsWith("[")) {
		if (System.getProperty("java.net.preferIPv4Stack") == null) {
		    throw new IllegalStateException("always provide java.net.preferIPv4Stack JVM property when using IPv6 address format");
		}
		if (System.getProperty("java.net.preferIPv6Addresses") == null) {
		    throw new IllegalStateException("always provide java.net.preferIPv6Addresses JVM property when using IPv6 address format");
		}
	    }
	    final boolean isIPv6Address = InetAddress.getByName(host) instanceof Inet6Address;
	    final boolean isIPv6Formatted = isIPv6Address && host.startsWith("[");
	    return isIPv6Address && !isIPv6Formatted ? "[" + host + "]" : host;
	} catch (final UnknownHostException e) {
	    throw new RuntimeException(e);
	}
    }
 }
