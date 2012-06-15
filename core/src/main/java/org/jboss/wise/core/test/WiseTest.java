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
import java.net.MalformedURLException;
import java.net.URL;
import javax.management.ObjectName;
import org.apache.log4j.Logger;

/**
 * Wise test base class. Subclass can use the methods in this class to 
 * deploy and undeploy a web service war in JBossAS  
 * @author ema@redhat.com
 *
 */
public class WiseTest {
    private static final Logger logger = Logger.getLogger(WiseTest.class);
    private static final String MAIN_DEPLOYER = "jboss.system:service=MainDeployer";
    private static final String WS_SERVER_CONFIG = "jboss.ws:service=ServerConfig";
    private static final String TEST_WS_ARCHIVE_DIR = "test-ws-archive";
    
    /**
     * Deploy the webservice war in JBoss server
     * @param url url for webservice war 
     * @throws Exception if the deployment is failed
     */
    public void deployWS(URL url) throws Exception {
	throw new Exception("TODO");
//        JBossWSTestHelper.getServer().invoke(new ObjectName(MAIN_DEPLOYER), "deploy", new Object[] { url }, new String[] { "java.net.URL" });       
    }
    
    /**Undeploy a webservice
     * @param url url of webservice war 
     * @throws Exception if undeployment is failed
     */
    public void undeployWS(URL url) throws Exception {
	throw new Exception("TODO");
//        JBossWSTestHelper.getServer().invoke(new ObjectName(MAIN_DEPLOYER), "undeploy", new Object[] { url }, new String[] { "java.net.URL" });       
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
                 warUrl = file.getAbsoluteFile().toURL();
             } catch (MalformedURLException e) {
                 return null;
             }
         }
         return warUrl;

     }
    
    /**Get the jboss webservice server side hostname and port 
     * @return http://server-hostname:port
     */
    public String getServerHostAndPort() {
	Logger.getLogger(this.getClass()).warn("TODO!! implement getServerHostAndPort()");
        //return "http://" + JBossWSTestHelper.getServerHost() + ":" + getServerPort();
	return null;
    }
    
    /**Get the web service server port
     * @return webservice server configured port
     */
    public String getServerPort() {
	Logger.getLogger(this.getClass()).warn("TODO!! implement getServerPort()");
        try {
//             return JBossWSTestHelper.getServer().getAttribute(new ObjectName(WS_SERVER_CONFIG), "WebServicePort").toString();
            return "8080";
         } catch (Exception e) {
             logger.warn("WARNING: Failed to get server port; using default 8080");
             return "8080";
         } 
    }


 }
