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

import restlib.data.ContentEncoding;
import restlib.data.ContentRange;
import restlib.data.Language;
import restlib.data.MediaRanges;
import restlib.net.Uri;
import restlib.test.WrapperTester;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;


/**
 * Tests for ContentInfo, ContentInfoBuilder, ContentInfoImpl and ContentInfoWrapper.
 */
public final class ContentInfoTest {
    @Test 
    public void testBuilder$build() {
        final ContentInfo contentInfo =
                ContentInfo.builder()
                    .addEncoding(ContentEncoding.COMPRESS)
                    .addEncodings(ImmutableList.of(ContentEncoding.DEFLATE))
                    .addLanguage(Language.forLocale(Locale.US))
                    .addLanguages(ImmutableList.of(Language.forLocale(Locale.CANADA)))
                    .setLength(100)
                    .setLocation(Uri.parse("www.example.com"))
                    .setMediaRange(MediaRanges.APPLICATION_JSON)
                    .setRange(ContentRange.byteRange(0, 40, 100))
                    .build();
        
        assertEquals(
                ImmutableList.of(ContentEncoding.COMPRESS, ContentEncoding.DEFLATE),
                contentInfo.encodings());
        assertEquals(
                ImmutableSet.of(Language.forLocale(Locale.US), Language.forLocale(Locale.CANADA)),
                contentInfo.languages());
        assertEquals(
                Long.valueOf(100),
                contentInfo.length().get());
        assertEquals(
                Uri.parse("www.example.com"),
                contentInfo.location().get());       
        assertEquals(
                MediaRanges.APPLICATION_JSON,
                contentInfo.mediaRange().get());
        assertEquals(
                ContentRange.byteRange(0, 40, 100),
                contentInfo.range().get());         
        
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBuilder$setLength_withNegativeLength() {
        ContentInfo.builder().setLength(-100);
    }  
    
    @Test
    public void testEquals() {
        final ContentInfoBuilder builder = ContentInfo.builder();
        new EqualsTester()
            .addEqualityGroup(
                    builder.build(),
                    builder.build(),
                    ContentInfo.NONE)
            .addEqualityGroup(
                    builder.addEncoding(ContentEncoding.COMPRESS).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.addEncodings(ImmutableList.of(ContentEncoding.DEFLATE)).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.addLanguage(Language.forLocale(Locale.US)).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.addLanguages(ImmutableList.of(Language.forLocale(Locale.CANADA))).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.setLength(100).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.setLocation(Uri.parse("www.example.com")).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.setMediaRange(MediaRanges.APPLICATION_JSON).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.setRange(ContentRange.byteRange(0, 40, 100)).build(),
                    builder.build())
            .testEquals();
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
                .testAllPublicInstanceMethods(ContentInfo.builder());
        new NullPointerTester()
            .testAllPublicInstanceMethods(ContentInfo.NONE);
    }
    
    @Test
    public void testWrapper() {
        final ContentInfoBuilder builder = ContentInfo.builder();
        WrapperTester.create(
                ContentInfo.class,
                new Function<ContentInfo, ContentInfo>() {
                    @Override
                    public ContentInfo apply(final ContentInfo contentInfo) {
                        return new ContentInfoWrapper(contentInfo);
                    }                   
                })
            .useDefaultInstances()
            .includingEquals()
            .executeTests(
                    builder.build(),
                    builder.addEncoding(ContentEncoding.COMPRESS).build(),
                    builder.addLanguage((Language.forLocale(Locale.US))).build(),
                    builder.setLength(100).build(),
                    builder.setLocation(Uri.parse("www.example.com")).build(),
                    builder.setMediaRange(MediaRanges.APPLICATION_ATOM).build(),
                    builder.setRange(ContentRange.byteRange(0, 99, 100)).build());                    
    }
}
