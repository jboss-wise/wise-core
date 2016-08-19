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
package org.jboss.wise.core.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.jboss.logging.Logger;
import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.wise.core.mapper.SmooksMapper;
import org.milyn.Smooks;
import org.milyn.SmooksUtil;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.cdr.SmooksResourceConfigurationList;
import org.milyn.cdr.XMLConfigDigester;
import org.milyn.container.ExecutionContext;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.javabean.repository.BeanRepository;
import org.milyn.profile.DefaultProfileSet;
import org.milyn.profile.ProfileStore;
import org.milyn.profile.UnknownProfileMemberException;
import org.milyn.resource.URIResourceLocator;
import org.jboss.wise.core.i18n.Messages;

/**
 * A SOAPHandler extension. It apply smooks transformation on soap message. Transformation can also use freemarker, using
 * provided javaBeans map to get values It can apply transformation only on inbound message, outbound ones or both, depending on
 * setInBoundHandlingEnabled(boolean) and setOutBoundHandlingEnabled(boolean) methods
 *
 * @see #setInBoundHandlingEnabled(boolean)
 * @see #setOutBoundHandlingEnabled(boolean)
 * @author Stefano Maestri, stefano.maestri@javalinux.it
 */
@ThreadSafe
public class SmooksHandler implements SOAPHandler<SOAPMessageContext> {

    private final Map<String, Object> beansMap;

    private final Smooks smooks;

    private final String smooksReport;

    private final Logger log = Logger.getLogger(SmooksMapper.class);

    @GuardedBy("this")
    private boolean outBoundHandlingEnabled = true;

    @GuardedBy("this")
    private boolean inBoundHandlingEnabled = true;

    /**
     * @param smooksResource URI of smooks config file
     * @param beans used for smooks BeanAccessor
     * @param client a dynamic client
     * @param smooksReport string
     */
    public SmooksHandler(String smooksResource, Map<String, Object> beans, WSDynamicClient client, String smooksReport) {

        assert smooksResource != null;

        try (InputStream smooksResourceStream = new URIResourceLocator().getResource(smooksResource)) {
            smooks = client.getSmooksInstance();
            try {
                ProfileStore profileStore = smooks.getApplicationContext().getProfileStore();
                profileStore.getProfileSet(Integer.toString(this.hashCode()));
            } catch (UnknownProfileMemberException e) {

                synchronized (SmooksMapper.class) {
                    // Register the message flow within the Smooks context....
                    SmooksUtil.registerProfileSet(DefaultProfileSet.create(Integer.toString(this.hashCode()), new String[] {}),
                            smooks);
                }
            }
            SmooksResourceConfigurationList list = XMLConfigDigester.digestConfig(smooksResourceStream, "wise");
            for (int i = 0; i < list.size(); i++) {
                SmooksResourceConfiguration smookResourceElement = list.get(i);
                smookResourceElement.setTargetProfile(Integer.toString(this.hashCode()));
                SmooksUtil.registerResource(smookResourceElement, smooks);
            }

            this.smooksReport = smooksReport;
        } catch (Exception e) {
            throw new WiseRuntimeException("failde to create SmooksMapper", e);
        }

        this.beansMap = beans;
    }

    public Set<QName> getHeaders() {
        return null;
    }

    public void close(MessageContext arg0) {

    }

    public boolean handleFault(SOAPMessageContext arg0) {
        return false;
    }

    public boolean handleMessage(SOAPMessageContext smc) {
        SOAPMessage message = smc.getMessage();
        Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outboundProperty == true && this.isOutBoundHandlingEnabled() == false) {
            return false;
        }
        if (outboundProperty == false && this.isInBoundHandlingEnabled() == false) {
            return false;
        }
        try {
            smc.setMessage(applySmooksTransformation(message));
        } catch (Exception e) {
            log.error(Messages.MESSAGES.errorDescription(), e);
            return false;
        }
        return true;

    }

    /* package */ExecutionContext initExecutionContext(String smooksReport) {
        ExecutionContext executionContext = smooks.createExecutionContext(Integer.toString(this.hashCode()));
        if (smooksReport != null) {
            try {
                executionContext.setEventListener(new HtmlReportGenerator(smooksReport));
            } catch (IOException e) {
                if (log.isDebugEnabled()) {
                    log.debug(Messages.MESSAGES.errorDuringLoading(smooksReport, e.getMessage()));
                    log.info(Messages.MESSAGES.wiseWillContinueWithoutIt());

                }
            }
        }
        return executionContext;

    }

    SOAPMessage applySmooksTransformation(SOAPMessage message) throws Exception {
        ByteArrayOutputStream outStream = null;
        ByteArrayInputStream inStream = null;
        try {
            ExecutionContext executionContext = initExecutionContext(smooksReport);
            StringWriter transResult = new StringWriter();

            BeanRepository.getInstance(executionContext).getBeanMap().putAll(this.beansMap);
            outStream = new ByteArrayOutputStream();
            message.writeTo(outStream);
            outStream.flush();
            inStream = new ByteArrayInputStream(outStream.toByteArray());
            smooks.filterSource(executionContext, new StreamSource(inStream), new StreamResult(transResult));
            inStream.close();
            inStream = new ByteArrayInputStream(transResult.toString().getBytes());
            // TODO evaluate avoiding buinding up a new factory each time + check for soap 1.2
            SOAPMessage message2 = MessageFactory.newInstance().createMessage(message.getMimeHeaders(), inStream);
            return message2;
        } finally {
            try {
                inStream.close();
            } catch (Exception e) {
                // nop
            }
            try {
                outStream.close();
            } catch (Exception e) {
                // nop
            }
        }
    }

    public static boolean isSOAP12(SOAPMessage soapMessage) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        if (SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE.equals(soapEnvelope.getNamespaceURI()))
            return true;
        return false;
    }

    public synchronized boolean isOutBoundHandlingEnabled() {
        return outBoundHandlingEnabled;
    }

    /**
     * @param outBoundHandlingEnabled if true smooks transformation are applied to outBound message
     */
    public synchronized void setOutBoundHandlingEnabled(boolean outBoundHandlingEnabled) {
        this.outBoundHandlingEnabled = outBoundHandlingEnabled;
    }

    public synchronized boolean isInBoundHandlingEnabled() {
        return inBoundHandlingEnabled;
    }

    /**
     * @param inBoundHandlingEnabled if true smooks transformation are applied to inBound message
     */
    public synchronized void setInBoundHandlingEnabled(boolean inBoundHandlingEnabled) {
        this.inBoundHandlingEnabled = inBoundHandlingEnabled;
    }
}
