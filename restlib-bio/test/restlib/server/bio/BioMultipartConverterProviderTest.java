/*
 * Copyright (C) 2012 David Bordoley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package restlib.server.bio;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import restlib.ContentInfo;
import restlib.Request;
import restlib.bio.BioMultiPartInputDeserializer;
import restlib.bio.multipart.BodyPartInputStream;
import restlib.bio.multipart.BioMultiPartInput;
import restlib.data.MediaRange;
import restlib.net.Uri;
import restlib.server.bio.InputStreamDeserializerSupplier;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

public final class BioMultipartConverterProviderTest {
    private static final String TEST_FORM = 
            "--AaB03x\r\n" +
            "Content-Disposition: form-data; name=\"submit-name\"\r\n\r\n" +
            "Larry" +
            "\r\n--AaB03x\r\n" +
            "Content-Disposition: form-data; name=\"files\"; filename=\"file1.txt\"\r\n" +
            "Content-Type: text/plain\r\n\r\n" +
            "File Contents" +
            "\r\n--AaB03x--";
    
    private static final Request REQUEST =
            Request.builder()
                .setUri(Uri.parse("http://www.example.com"))
                .setContentInfo(
                    ContentInfo.builder()
                        .setMediaRange(
                                MediaRange.parse("multipart/form-data; boundary=AaB03x"))
                         .build())
                .build();       
    
    private static InputStreamDeserializerSupplier<String> PROVIDER =
            InputStreamDeserializerSuppliers.multiPartInputDeserializerSupplier(
                    new BioMultiPartInputDeserializer<String>() {

                        @Override
                        public String read(final BioMultiPartInput in) throws IOException {
                            final StringBuilder builder = new StringBuilder();
                            in.getPreamble();
                            for (final BodyPartInputStream part : in) {
                                builder.append(
                                        CharStreams.toString(
                                                new InputStreamReader(
                                                        part, Charsets.US_ASCII)));
                            }
                            return builder.toString();
                        }
                        
                    }, MediaRange.parse("multipart/form-data"));
                
    @Test
    public void getRequestEntityConverterTest() throws IOException {
        final InputStream is = 
                new ByteArrayInputStream(
                        TEST_FORM.getBytes(Charsets.US_ASCII));
        final String string = 
                PROVIDER.get(REQUEST).read(is);
        assertEquals("LarryFile Content", string);
    }
}
