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

import org.junit.Test;

import restlib.data.EntityTag;
import restlib.data.HttpDate;
import restlib.test.WrapperTester;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

/**
 * Tests for RequestPreconditions, RequestPreconditionsBuilder, RequestPreconditionsImpl and RequestPreconditionsWrapper.
 */
public final class RequestPreconditionsTest {
    @Test 
    public void testBuilder$build() {
        final RequestPreconditions preconditions =
                RequestPreconditions.builder()
                    .addIfMatchTag(EntityTag.weakTag("abc"))
                    .addIfNoneMatchTag(EntityTag.weakTag("abc"))
                    .setIfModifiedSinceDate(HttpDate.create(123))
                    .setIfRange((Object) HttpDate.create(123))
                    .setIfUnmodifiedSinceDate(HttpDate.create(123))
                    .build();
        assertEquals(
                ImmutableSet.of(EntityTag.weakTag("abc")),
                preconditions.ifMatchTags());
        assertEquals(
                HttpDate.create(123),
                preconditions.ifModifiedSinceDate().get());
        assertEquals(
                ImmutableSet.of(EntityTag.weakTag("abc")),
                preconditions.ifNoneMatchTags());
        assertEquals(
                HttpDate.create(123),
                preconditions.ifRange().get());
        assertEquals(
                HttpDate.create(123),
                preconditions.ifUnmodifiedSinceDate().get());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBuilder$setIfRange_withInvalidType() {
    	RequestPreconditions.builder().setIfRange("");
    }
    
    @Test
    public void testEquals() {
        final RequestPreconditionsBuilder builder = RequestPreconditions.builder();
        new EqualsTester()
            .addEqualityGroup(
                    builder.build(),
                    builder.build(),
                    RequestPreconditions.NONE)
            .addEqualityGroup(
                    builder.addIfMatchTag(EntityTag.weakTag("abc")).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.addIfMatchTags(ImmutableList.of(EntityTag.strongTag("abc"))).build(),
                    builder.build())   
            .addEqualityGroup(
                    builder.addIfNoneMatchTag(EntityTag.weakTag("abc")).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.addIfNoneMatchTags(ImmutableList.of(EntityTag.strongTag("abc"))).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.setIfModifiedSinceDate(HttpDate.now()).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.setIfRange((Object) EntityTag.weakTag("abc")).build(),
                    builder.build())
            .addEqualityGroup( 
                    builder.setIfRange((Object) HttpDate.now()).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.setIfUnmodifiedSinceDate(HttpDate.now()).build(),
                    builder.build())        
            .testEquals();      
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
                .testAllPublicInstanceMethods(RequestPreconditions.builder());
        new NullPointerTester()
            .testAllPublicInstanceMethods(RequestPreconditions.NONE);
    }
    
    @Test
    public void testWrapper() {
        final RequestPreconditionsBuilder builder = RequestPreconditions.builder();
        WrapperTester.create(
                RequestPreconditions.class, 
                new Function<RequestPreconditions,RequestPreconditions>() {
                    @Override
                    public RequestPreconditions apply(final RequestPreconditions in) {
                        return new RequestPreconditionsWrapper(in);
                    }})
            .useDefaultInstances()
            .includingEquals()
            .executeTests(
                    builder.build(),
                    builder.addIfMatchTag(EntityTag.weakTag("abc")).build(),
                    builder.addIfNoneMatchTag(EntityTag.weakTag("abc")).build(),
                    builder.setIfModifiedSinceDate(HttpDate.create(1234)).build(),
                    builder.setIfRange((Object) EntityTag.weakTag("abc")).build(),
                    builder.setIfUnmodifiedSinceDate(HttpDate.create(1234)).build());            
    }
}
