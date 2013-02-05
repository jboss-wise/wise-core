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
