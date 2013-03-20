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
package org.jboss.wise.core.client.impl.reflection;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import javax.jws.Oneway;
import javax.jws.WebParam;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import org.apache.log4j.Logger;
import org.jboss.wise.core.client.InvocationResult;
import org.jboss.wise.core.client.WSEndpoint;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.exception.InvocationException;
import org.jboss.wise.core.exception.MappingException;
import org.jboss.wise.core.mapper.WiseMapper;

/**
 * Represent a webservice operation invocation
 * 
 * @author stefano.maestri@javalinux.it
 * @since 23-Aug-2007
 */
@ThreadSafe
@Immutable
public class WSMethodImpl implements WSMethod {
    
    private final Method method;

    private final WSEndpoint endpoint;

    private final Map<String, WebParameterImpl> parameters = Collections.synchronizedMap(new HashMap<String, WebParameterImpl>());

    public WSMethodImpl(Method method, WSEndpoint endpoint) throws IllegalArgumentException {
	if (method == null || endpoint == null) {
	    throw new IllegalArgumentException();
	}
	this.method = method;
	this.endpoint = endpoint;
	this.initWebParams();
    }

    /**
     * Invokes this method with the provided arguments
     * 
     * @param args @return @throws WiseException If an unknown exception is
     * received
     */
    InvocationResultImpl invoke(Map<String, Object> args) throws InvocationException, IllegalArgumentException {
//	Method methodPointer = null;
	InvocationResultImpl result = null;
	Map<String, Object> emptyHolder = Collections.emptyMap();

	try {
	    EndpointMethodCaller caller = new EndpointMethodCaller(this.getEndpoint(), this.getMethod(), this
		    .getParametersInRightPositionArray(args));
	    Future<Object> invocation = ((WSEndpointImpl) this.getEndpoint()).getService().submit(caller);
	    if (isOneWay()) {
		invocation.get();
		result = new InvocationResultImpl(null, null, null, emptyHolder);
	    } else {
		result = new InvocationResultImpl(RESULT, method.getGenericReturnType(), invocation.get(), getHoldersResult(args));

	    }
	} catch (Exception ite) {
	    Logger.getLogger(WSMethodImpl.class).info("Error invoking method " + this.getMethod() + ", arguments: " + args != null ? args.values().toArray() : null);
//	    if (methodPointer != null && methodPointer.getExceptionTypes() != null) {
//		for (int i = 0; i < methodPointer.getExceptionTypes().length; i++) {
//		    Class<?> excType = methodPointer.getExceptionTypes()[i];
//		    if (ite.getCause().getClass().isAssignableFrom(excType)) {
//			result = new InvocationResultImpl("exception", excType, ite.getCause(), emptyHolder);
//			return result;
//		    }
//		}
//	    }
	    throw new InvocationException("Unknown exception received: " + ite.getMessage(), ite);
	} catch (Throwable e) {
	    throw new InvocationException("Generic Error during method invocation!", e);
	}
	return result;
    }

    @Override
    public void writeRequestPreview(Map<String, Object> args, OutputStream os) throws InvocationException {
	try {
	    EndpointMethodPreview caller = new EndpointMethodPreview(this.getEndpoint(), this.getMethod(), this
		    .getParametersInRightPositionArray(args), os);
	    ((WSEndpointImpl) this.getEndpoint()).getService().submit(caller).get();
	} catch (Exception ite) {
	    throw new InvocationException("Unknown exception received: " + ite.getMessage(), ite);
	} catch (Throwable e) {
	    throw new InvocationException("Generic Error during method invocation!", e);
	}
    }

    /**
     * Invokes this method with the provided arguments applying provided mapper
     * 
     * @param args
     * @param mapper
     *            if null no mappings are applied method will be invoked using
     *            args directly. in this case the keys of the map gotta be the
     *            parameters names as defined in wsdl/wsconsume generated
     *            classes
     * @return {@link InvocationResultImpl}
     * @throws InvocationException
     * @throws IllegalArgumentException
     * @throws MappingException
     */
    @SuppressWarnings("unchecked")
    public InvocationResultImpl invoke(Object args, WiseMapper mapper) throws InvocationException, IllegalArgumentException, MappingException {
	if (mapper == null) {
	    return this.invoke((Map<String, Object>) args);
	}
	ClassLoader oldLoader = SecurityActions.getContextClassLoader();
	Map<String, Object> mappingResults;
	try {
	    SecurityActions.setContextClassLoader(this.getEndpoint().getClassLoader());
	    mappingResults = mapper.applyMapping(args);
	} finally {
	    SecurityActions.setContextClassLoader(oldLoader);
	}
	return this.invoke(mappingResults);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.wise.core.client.WSMethod#invoke(java.lang.Object)
     */
    public InvocationResult invoke(Object args) throws InvocationException, IllegalArgumentException, MappingException {
	return this.invoke(args, null);
    }

    /**
     * Gets the map of WebParameters for a selected method
     * 
     * @return a map representing valide webparameters
     */
    public Map<String, WebParameterImpl> getWebParams() {
	return parameters;
    }

    private void initWebParams() {
	Method method = this.getMethod();
	Annotation[][] annotations = method.getParameterAnnotations();
	Type[] methodparameterTypes = method.getGenericParameterTypes();
	for (int i = 0; i < annotations.length; i++) {
	    for (int j = 0; j < annotations[i].length; j++) {
		if (annotations[i][j] instanceof WebParam) {
		    WebParam webParaAnno = (WebParam) annotations[i][j];
		    WebParameterImpl parameter = new WebParameterImpl(methodparameterTypes[i], webParaAnno.name(), i, webParaAnno
			    .mode());
		    parameters.put(parameter.getName(), parameter);
		    break;
		}
	    }
	}

    }

    /*
     * package protected method, for test purpose
     */
    /* package */Object[] getParametersInRightPositionArray(Map<String, Object> originalParams) {
	Map<String, WebParameterImpl> webParams = this.getWebParams();
	Object[] arrayToReturn = new Object[webParams.size()];
	Arrays.fill(arrayToReturn, null);

	for (String key : webParams.keySet()) {
	    WebParameterImpl webPara = webParams.get(key);
	    int position = webPara.getPosition();
	    arrayToReturn[position] = originalParams.get(key);
	}
	return arrayToReturn;
    }

    /*
     * package protected method, for test purpose
     */
    Map<String, Object> getHoldersResult(Map<String, Object> paras) {
	Map<String, Object> holders = new HashMap<String, Object>();
	Map<String, WebParameterImpl> webParams = this.getWebParams();

	for (String key : paras.keySet()) {
	    WebParameterImpl wisePara = webParams.get(key);
	    if (wisePara != null && (wisePara.getMode() == WebParam.Mode.INOUT || wisePara.getMode() == WebParam.Mode.OUT)) {
		holders.put(key, paras.get(key));
		holders.put(TYPE_PREFIX + key, wisePara.getType());
	    }
	}
	return holders;
    }

    public synchronized boolean isOneWay() {
	return method.getAnnotation(Oneway.class) != null;
    }

    public Method getMethod() {
	return method;
    }

    public WSEndpoint getEndpoint() {
	return endpoint;
    }

}
