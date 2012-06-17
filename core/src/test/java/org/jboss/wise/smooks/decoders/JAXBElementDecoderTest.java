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
