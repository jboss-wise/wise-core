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

package org.jboss.wise.core.client.builder;

import java.io.File;
import java.io.PrintStream;
import java.net.ConnectException;
import java.util.List;
import net.jcip.annotations.ThreadSafe;

import org.apache.tools.ant.taskdefs.Length.When;
import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.exception.WiseRuntimeException;

/**
 * {@link WSDynamicClientBuilder} is an interface to define builder for various
 * kind of implementation of WiseDynamicClient Indirect build permit to easy
 * inject different implementation of {@link WSDynamicClient}.
 * 
 * @author stefano.maestri@javalinux.it
 */
@ThreadSafe
public interface WSDynamicClientBuilder {

    /**
     * Build the {@link WSDynamicClient} with all parameters set on this class
     * 
     * @return {@link WSDynamicClient}
     * @throws IllegalStateException
     * @throws ConnectException
     * @throws WiseRuntimeException
     */
    public WSDynamicClient build() throws IllegalStateException, ConnectException, WiseRuntimeException;

    /**
     * Set the wsdlURL to generate WS client
     * 
     * @param wsdlURL
     * @return {@link WSDynamicClient}
     */
    public WSDynamicClientBuilder wsdlURL(String wsdlURL);

    /**
     * set the userName used in Basic Auth both for downloading wsdl and calling
     * service
     * 
     * @param userName
     * @return {@link WSDynamicClient}
     */
    public WSDynamicClientBuilder userName(String userName);

    /**
     * set the password used in Basic Auth both for downloading wsdl and calling
     * service
     * 
     * @param password
     * @return {@link WSDynamicClient}
     */
    public WSDynamicClientBuilder password(String password);

    /**
     * set the temp direcoory location used to generate temporary client
     * classes. Wise will generate there subdirecoty fo each instance of
     * {@link WSDynamicClient} and take care of all cleanup when
     * WSDynamicClient.close() is called
     * 
     * @param tmpDir
     * @return {@link WSDynamicClient}
     */
    public WSDynamicClientBuilder tmpDir(String tmpDir);

    /**
     * force the package name used for generated client classes. If it is't set
     * wsconsume rules will e used: namespaces and/or bindingfiles
     * 
     * @param targetPackage
     * @return {@link WSDynamicClient}
     */
    public WSDynamicClientBuilder targetPackage(String targetPackage);

    /**
     * set the list of JAXB bindings files used by wsconsume
     * 
     * @param bindings
     * @return {@link WSDynamicClient}
     */
    public WSDynamicClientBuilder bindingFiles(List<File> bindings);

    /**
     * set the catelog file
     * 
     * @param catelog
     * @return {@link WSDynamicClient}
     */
    public WSDynamicClientBuilder catalogFile(File catelog);

    /**
     * It is the URL of config file used by JbossWS to enable WS-SE. For more
     * information about this file refer to our samples and/or to JBossWS
     * documentation
     * 
     * @param url
     * @return {@link WSDynamicClient}
     */
    public WSDynamicClientBuilder securityConfigUrl(String url);

    /**
     * It is the config name sed by JbossWS to enable WS-SE. For more
     * information about this file refer to our samples and/or to JBossWS
     * documentation
     * 
     * @param name
     * @return {@link WSDynamicClient}
     */
    public WSDynamicClientBuilder securityConfigName(String name);

    /**
     * if it it set to true source code generated for client classes will be
     * kept in {@link #tmpDir(String)}
     * 
     * @param bool
     * @return {@link WSDynamicClient}
     */
    public WSDynamicClientBuilder keepSource(boolean bool);

    /**
     * if it set to true wsconsume operation of class generation and compilation
     * will be verbose and its messages will be put on
     * {@link #messageStream(PrintStream)}
     * 
     * @param bool
     * @return {@link WSDynamicClient}
     */
    public WSDynamicClientBuilder verbose(boolean bool);

    /**
     * Sets the PrintStream to use for status feedback. The simplest example
     * would be to use System.out.
     * 
     * @param messageStream
     * @return {@link WSDynamicClient}
     */
    public WSDynamicClientBuilder messageStream(PrintStream messageStream);

    /**
     * Set the max size of thread pool used to invoke in parallel
     * {@link WSMethod} on the build {@link WSDynamicClient}. default value is
     * 100.
     * 
     * @param maxThreadPoolSize
     * @return {@link WSDynamicClient}
     */
    public WSDynamicClientBuilder maxThreadPoolSize(int maxThreadPoolSize);

    public String getWsdlURL();

    public String getUserName();

    public String getPassword();

    public String getTmpDir();

    public String getTargetPackage();

    public List<File> getBindingFiles();

    public File getCatalogFile();

    public String getSecurityConfigFileURL();

    public String getSecurityConfigName();

    public boolean isKeepSource();

    public boolean isVerbose();

    public PrintStream getMessageStream();

    public String getNormalizedWsdlUrl();

    public String getClientSpecificTmpDir();

    public int getMaxThreadPoolSize();

}
