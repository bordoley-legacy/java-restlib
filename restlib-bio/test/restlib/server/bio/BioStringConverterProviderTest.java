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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import restlib.ClientPreferences;
import restlib.ContentInfo;
import restlib.Request;
import restlib.Response;
import restlib.bio.OutputStreamSerializer;
import restlib.data.Charset;
import restlib.data.MediaRanges;
import restlib.data.Preference;
import restlib.net.Uri;

public final class BioStringConverterProviderTest {
   @Test
   public void getRequestEntityConverterTest() throws IOException {
       final String test = "this is a test";
       final Request request = 
               Request.builder()
                   .setUri(Uri.parse("http://www.example.com"))
                   .setContentInfo(
                           ContentInfo.builder()
                           .setMediaRange(MediaRanges.TEXT_PLAIN.withCharset(Charset.UTF_8))
                           .build())
                   .build();
       final InputStream is = 
               new ByteArrayInputStream(
                       test.getBytes(Charset.UTF_8.toNioCharset()));
       final Object entity = 
               InputStreamDeserializerSuppliers.stringDeserializerSupplier(MediaRanges.TEXT_ANY).get(request).read(is);
       assertEquals(test, entity);
   }

    
    @Test
    public void getResponseEntityConverterTest() throws IOException {
        final String test = "this is a test";
        final ContentInfo expectedContentInfo =
                ContentInfo.builder()
                    .setMediaRange(MediaRanges.TEXT_PLAIN.withCharset(Charset.UTF_16BE))
                    .setLength(test.getBytes(Charset.UTF_16BE.toNioCharset()).length)
                    .build();
        
        final Request request = 
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setPreferences(
                            ClientPreferences.builder()
                                .addAcceptedCharset(
                                        Preference.create(Charset.UTF_16BE, 1)).build())
                    .build();
        final Response response =
                Response.builder().setEntity(test).build();
        
        final OutputStreamSerializer converter =
                OutputStreamSerializerSuppliers.STRING_AS_PLAIN_TEXT_SERIALIZER_SUPPLIER.get(request, response);
        
        assertEquals(expectedContentInfo, converter.contentInfo());
        
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        converter.write(os);
        
        assertArrayEquals(
                test.getBytes(Charset.UTF_16BE.toNioCharset()),
                os.toByteArray());
    }
}
