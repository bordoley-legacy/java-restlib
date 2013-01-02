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


package restlib;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

import restlib.data.ByteRangeSpec;
import restlib.data.Charset;
import restlib.data.ContentEncoding;
import restlib.data.Language;
import restlib.data.MediaRanges;
import restlib.data.Preference;
import restlib.data.Range;
import restlib.data.TransferCoding;
import restlib.test.WrapperTester;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
/**
 * Tests for ClientPreferences, ClientPreferencesBuilder, ClientPreferencesImpl and ClientPreferencesWrapper.
 */
public final class ClientPreferencesTest {
    @Test 
    public void testBuilder$build() {
        final ClientPreferences preferences = 
                ClientPreferences.builder()
                    .addAcceptedCharset(Preference.create(Charset.UTF_8))
                    .addAcceptedCharsets(
                            ImmutableList.of(Preference.create(Charset.US_ASCII)))
                    .addAcceptedEncoding(Preference.create(ContentEncoding.GZIP))   
                    .addAcceptedEncodings(
                            ImmutableList.of(Preference.create(ContentEncoding.DEFLATE)))
                    .addAcceptedLanguage(Preference.create(Language.forLocale(Locale.ENGLISH)))
                    .addAcceptedLanguages(
                            ImmutableList.of(Preference.create(Language.forLocale(Locale.CANADA))))
                    .addAcceptedMediaRange(Preference.create(MediaRanges.APPLICATION_ATOM))
                    .addAcceptedMediaRanges(
                            ImmutableList.of(Preference.create(MediaRanges.APPLICATION_JSON_FEED)))
                    .addAcceptedTransferEncoding(Preference.create(TransferCoding.CHUNKED))        
                    .addAcceptedTransferEncodings(
                            ImmutableList.of(Preference.create(TransferCoding.COMPRESS)))      
                    .setRange(Range.byteRange(ImmutableList.of(ByteRangeSpec.range(0, 100))))                                           
                    .build();
        
        assertEquals(
                ImmutableSet.of(Preference.create(Charset.UTF_8), Preference.create(Charset.US_ASCII)),
                preferences.acceptedCharsets());
        assertEquals(
                ImmutableSet.of(Preference.create(ContentEncoding.GZIP), Preference.create(ContentEncoding.DEFLATE)),
                preferences.acceptedEncodings());
        assertEquals(
                ImmutableSet.of(Preference.create(Language.forLocale(Locale.ENGLISH)), Preference.create(Language.forLocale(Locale.CANADA))),
                preferences.acceptedLanguages());
        assertEquals(
                ImmutableSet.of(Preference.create(MediaRanges.APPLICATION_ATOM), Preference.create(MediaRanges.APPLICATION_JSON_FEED)),
                preferences.acceptedMediaRanges());
        assertEquals(
                ImmutableSet.of(Preference.create(TransferCoding.CHUNKED), Preference.create(TransferCoding.COMPRESS)),
                preferences.acceptedTransferEncodings());
        assertEquals(
                Range.byteRange(ImmutableList.of(ByteRangeSpec.range(0, 100))),
                preferences.range().get());        
    }
    
    @Test
    public void testEquals() {
        final ClientPreferencesBuilder builder = ClientPreferences.builder();
        
        new EqualsTester()
            .addEqualityGroup(
                    builder.build(), builder.build(), ClientPreferences.NONE)
            .addEqualityGroup(
                    builder.addAcceptedCharset(Preference.create(Charset.UTF_8)).build(), 
                    builder.build())
            .addEqualityGroup(
                    builder.addAcceptedCharsets(
                            ImmutableList.of(Preference.create(Charset.US_ASCII))).build(), 
                    builder.build())
            .addEqualityGroup(
                    builder.addAcceptedEncoding(Preference.create(ContentEncoding.GZIP)).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.addAcceptedEncodings(
                            ImmutableList.of(Preference.create(ContentEncoding.DEFLATE))).build(), 
                    builder.build())       
            .addEqualityGroup(
                    builder.addAcceptedLanguage(Preference.create(Language.forLocale(Locale.ENGLISH))).build(),
                    builder.build())  
            .addEqualityGroup(
                    builder.addAcceptedLanguages(
                            ImmutableList.of(Preference.create(Language.forLocale(Locale.CANADA)))).build(), 
                    builder.build())          
            .addEqualityGroup(
                    builder.addAcceptedMediaRange(Preference.create(MediaRanges.APPLICATION_ATOM)).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.addAcceptedMediaRanges(
                            ImmutableList.of(Preference.create(MediaRanges.APPLICATION_JSON_FEED))).build(), 
                    builder.build())                       
            .addEqualityGroup(
                    builder.addAcceptedTransferEncoding(Preference.create(TransferCoding.CHUNKED)).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.addAcceptedTransferEncodings(
                            ImmutableList.of(Preference.create(TransferCoding.COMPRESS))).build(), 
                    builder.build())                     
            .addEqualityGroup(
                    builder.setRange(Range.byteRange(ImmutableList.of(ByteRangeSpec.range(0, 100)))).build(),
                    builder.build())
            .testEquals();
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
                .testAllPublicInstanceMethods(ClientPreferences.builder());
        new NullPointerTester()
            .testAllPublicInstanceMethods(ClientPreferences.NONE);
    }
    
    @Test
    public void testWrapper() {
        final ClientPreferencesBuilder builder = ClientPreferences.builder();
        WrapperTester.create(
                ClientPreferences.class, 
                new Function<ClientPreferences,ClientPreferences>() {
                    @Override
                    public ClientPreferences apply(final ClientPreferences in) {
                        return new ClientPreferencesWrapper(in);
                    }})
            .useDefaultInstances()
            .includingEquals()
            .executeTests(
                    builder.build(),
                    builder.addAcceptedCharset(Preference.create(Charset.UTF_8)).build(),
                    builder.addAcceptedEncoding(Preference.create(ContentEncoding.GZIP)).build(),
                    builder.addAcceptedLanguage(Preference.create(Language.forLocale(Locale.ENGLISH))).build(),
                    builder.addAcceptedMediaRange(Preference.create(MediaRanges.APPLICATION_JSON_ENTRY)).build(),
                    builder.addAcceptedTransferEncoding(Preference.create(TransferCoding.CHUNKED)).build(),
                    builder.setRange(Range.byteRange(ImmutableList.of(ByteRangeSpec.range(0, 100)))).build());            
    }
}
