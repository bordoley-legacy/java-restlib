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

import restlib.data.CacheDirective;
import restlib.data.ChallengeMessage;
import restlib.data.ConnectionOption;
import restlib.data.EntityTag;
import restlib.data.Header;
import restlib.data.HttpDate;
import restlib.data.HttpHeaders;
import restlib.data.MediaRanges;
import restlib.data.Method;
import restlib.data.RangeUnit;
import restlib.data.Status;
import restlib.data.UserAgent;
import restlib.data.Warning;
import restlib.net.Uri;
import restlib.test.WrapperTester;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.NullPointerTester;

/**
 * Tests for Response, ResponseBuilder, ResponseImpl and ResponseWrapper.
 */
public final class ResponseTest {
    private static final UserAgent RESTLIB = UserAgent.parse("Restlib/1.0");
    
	@Test(expected = IllegalArgumentException.class)
	public void testBuilder$addCustomHeader_withStandardHeader() {
		Response.builder().addCustomHeader(HttpHeaders.ACCEPT, "text/plain");
	}
	
    @Test
    public void testBuilder$build() {
        final Response response =
                Response.builder()
                    .addAcceptedRangeUnit(RangeUnit.BYTES)
                    .addAcceptedRangeUnits(ImmutableList.<RangeUnit> of())
                    .addAllowedMethod(Method.GET)
                    .addAllowedMethods(ImmutableList.<Method>of())
                    .addAuthenticationChallenge(
                            ChallengeMessage.basicAuthenticationChallenge("www.example.org"))
                    .addAuthenticationChallenges(ImmutableList.<ChallengeMessage> of())
                    .addCacheDirective(CacheDirective.MAX_STALE)
                    .addCacheDirectives(ImmutableList.<CacheDirective> of())
                    .addCustomHeader(Header.create("X-Test"), "test")
                    .addProxyAuthenticationChallenge(
                            ChallengeMessage.basicAuthenticationChallenge("www.example.org"))
                    .addProxyAuthenticationChallenges(ImmutableList.<ChallengeMessage> of())  
                    .addVaryHeader(HttpHeaders.ACCEPT)
                    .addVaryHeaders(ImmutableList.<Header> of())
                    .addWarning(Warning.create(200, "www.example.com", "warn text"))
                    .addWarnings(ImmutableList.<Warning> of())
                    .setAge(10)
                    .setConnectionInfo(
                            ConnectionInfo.builder().addConnectionOption(ConnectionOption.CLOSE).build())
                    .setContentInfo(
                            ContentInfo.builder().setMediaRange(MediaRanges.APPLICATION_JSON).build())
                    .setDate(HttpDate.create(1234))
                    .setEntity("test")
                    .setEntityTag(EntityTag.strongTag("abc"))
                    .setExpires(HttpDate.create(Long.MAX_VALUE))
                    .setLastModified(HttpDate.create(1234))
                    .setLocation(Uri.parse("http://www.example.com"))
                    .setRetryAfterDate(HttpDate.create(Long.MAX_VALUE))
                    .setServer(RESTLIB)
                    .setStatus(Status.CLIENT_ERROR_BAD_REQUEST)
                    .build();
        assertEquals(
                ImmutableSet.of(RangeUnit.BYTES),
                response.acceptedRangeUnits());
        assertEquals(
                Long.valueOf(10),
                response.age().get());
        assertEquals(
                ImmutableSet.of(Method.GET),
                response.allowedMethods());
        assertEquals(
                ImmutableSet.of(ChallengeMessage.basicAuthenticationChallenge("www.example.org")),
                response.authenticationChallenges());
        assertEquals(
                ImmutableSet.of(CacheDirective.MAX_STALE),
                response.cacheDirectives());
        assertEquals(
                ConnectionInfo.builder().addConnectionOption(ConnectionOption.CLOSE).build(),
                response.connectionInfo());
        assertEquals(
                ContentInfo.builder().setMediaRange(MediaRanges.APPLICATION_JSON).build(),
                response.contentInfo());
        assertEquals(
                ImmutableListMultimap.of(Header.create("X-Test"), "test"),
                response.customHeaders());    
        assertEquals(
                HttpDate.create(1234),
                response.date().get());
        assertEquals(
                "test",
                response.entity().get());
        assertEquals(
        		EntityTag.strongTag("abc"),
                response.entityTag().get());
        assertEquals(
                HttpDate.create(Long.MAX_VALUE),
                response.expires().get());
        assertEquals(
                HttpDate.create(1234),
                response.lastModified().get());
        assertEquals(
                Uri.parse("http://www.example.com"),
                response.location().get());
        assertEquals(
                ImmutableSet.of(ChallengeMessage.basicAuthenticationChallenge("www.example.org")),
                response.proxyAuthenticationChallenge());
        assertEquals(
                HttpDate.create(Long.MAX_VALUE),
                response.retryAfterDate().get());
        assertEquals(
                RESTLIB,
                response.server().get());
        assertEquals(
                Status.CLIENT_ERROR_BAD_REQUEST,
                response.status());
        assertEquals(
                ImmutableList.of(Warning.create(200, "www.example.com", "warn text")),
                response.warnings());
        assertEquals(
                ImmutableSet.of(HttpHeaders.ACCEPT),
                response.vary());
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testBuilder$setAge_withNegativeSeconds() {
    	Response.builder().setAge(-1);
    }
    
    @Test
    public void testNulls() {
        final NullPointerTester tester = 
                new NullPointerTester()
                    .setDefault(Header.class, HttpHeaders.ACCEPT);
        tester.testAllPublicInstanceMethods(Response.builder());
        tester.testAllPublicInstanceMethods(Response.builder().build());
        tester.testAllPublicStaticMethods(Response.class);
    }

    @Test
    public void testWrapper() {
        final ResponseBuilder builder = Response.builder();
        WrapperTester.create(
                Response.class,
                new Function<Response,Response>() {
                    @Override
                    public Response apply(final Response response) {
                        return new ResponseWrapper(response);
                    }                   
                })
                .useDefaultInstances()
                .executeTests(
                    builder.build(),
                    builder.addAcceptedRangeUnit(RangeUnit.BYTES).build(),
                    builder.addAllowedMethod(Method.GET).build(),
                    builder.addAuthenticationChallenge(ChallengeMessage.basicAuthenticationChallenge("www.example.org")).build(),
                    builder.addCacheDirective(CacheDirective.MUST_REVALIDATE).build(), 
                    builder.addCustomHeader(Header.create("X-Test"), "test").build(),
                    builder.addProxyAuthenticationChallenge(ChallengeMessage.basicAuthenticationChallenge("www.example.org")).build(),
                    builder.addVaryHeader(HttpHeaders.ACCEPT).build(),
                    builder.addWarning(Warning.create(200, "www.example.com", "warn text")).build(),
                    builder.setAge(10).build(),
                    builder.setConnectionInfo(
                            ConnectionInfo.builder().addConnectionOption(ConnectionOption.CLOSE).build()).build(),
                    builder.setDate(HttpDate.now()).build(),
                    builder.setEntity("test").build(),
                    builder.setExpires(HttpDate.create(Long.MAX_VALUE)).build(),
                    builder.setLastModified(HttpDate.now()).build(),
                    builder.setRetryAfterDate(HttpDate.create(Long.MAX_VALUE)).build(),
                    builder.setServer(RESTLIB).build(),
                    builder.setStatus(Status.CLIENT_ERROR_GONE).build());    
    }
}
