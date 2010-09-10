package org.jboss.wise.smooks.decoders;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import javax.xml.datatype.Duration;
import org.junit.Test;
import org.milyn.javabean.DataDecodeException;

public class DurationDecoderTest {

    @Test( expected = DataDecodeException.class )
    public void shouldThrowExceptionIfDatIsNotANumber() {
        DurationDecoder decoder = new DurationDecoder();
        decoder.decode("aa");
    }

    @Test
    public void shouldReturnDurationAccordingCOnfig() {
        DurationDecoder decoder = new DurationDecoder();
        assertThat(((Duration)decoder.decode("1000")).getSeconds(), equalTo(1));
    }

}
