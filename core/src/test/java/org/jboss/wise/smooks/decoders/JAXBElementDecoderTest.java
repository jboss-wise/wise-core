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
package org.jboss.wise.smooks.decoders;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.junit.Test;
import org.milyn.cdr.SmooksConfigurationException;
import org.milyn.cdr.SmooksResourceConfiguration;

public class JAXBElementDecoderTest {

    @Test( expected = SmooksConfigurationException.class )
    public void setConfigurationShouldThrowExceptionIfNameSpaceURIIsNull() {
        JAXBElementDecoder decorator = new JAXBElementDecoder();
        SmooksResourceConfiguration config = mock(SmooksResourceConfiguration.class);
        when(config.getStringParameter("namespaceURI")).thenReturn(null);
        decorator.setConfiguration(config);
    }

    @Test( expected = SmooksConfigurationException.class )
    public void setConfigurationShouldThrowExceptionIfLocalPartNull() {
        JAXBElementDecoder decorator = new JAXBElementDecoder();
        SmooksResourceConfiguration config = mock(SmooksResourceConfiguration.class);
        when(config.getStringParameter("localPart")).thenReturn(null);
        decorator.setConfiguration(config);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnJAXBElementAccordingConfig() {
        JAXBElementDecoder decorator = new JAXBElementDecoder();
        SmooksResourceConfiguration config = mock(SmooksResourceConfiguration.class);
        when(config.getStringParameter("namespaceURI")).thenReturn("myURI");
        when(config.getStringParameter("localPart")).thenReturn("local");
        decorator.setConfiguration(config);
        assertThat(((JAXBElement<String>)decorator.decode("data")).getValue(), equalTo("data"));
        assertThat(((JAXBElement<String>)decorator.decode("data")).getName(), equalTo(new QName("myURI", "local")));
    }

}
