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
import static restlib.data.HttpHeaders.ACCEPT;
import static restlib.data.HttpHeaders.ACCEPT_CHARSET;
import static restlib.data.HttpHeaders.ACCEPT_ENCODING;
import static restlib.data.HttpHeaders.ACCEPT_LANGUAGE;
import static restlib.data.HttpHeaders.AUTHORIZATION;
import static restlib.data.HttpHeaders.CACHE_CONTROL;
import static restlib.data.HttpHeaders.CONNECTION;
import static restlib.data.HttpHeaders.CONTENT_ENCODING;
import static restlib.data.HttpHeaders.CONTENT_LANGUAGE;
import static restlib.data.HttpHeaders.CONTENT_LENGTH;
import static restlib.data.HttpHeaders.CONTENT_LOCATION;
import static restlib.data.HttpHeaders.CONTENT_TYPE;
import static restlib.data.HttpHeaders.EXPECT;
import static restlib.data.HttpHeaders.FROM;
import static restlib.data.HttpHeaders.HOST;
import static restlib.data.HttpHeaders.IF_MATCH;
import static restlib.data.HttpHeaders.IF_MODIFIED_SINCE;
import static restlib.data.HttpHeaders.IF_NONE_MATCH;
import static restlib.data.HttpHeaders.IF_RANGE;
import static restlib.data.HttpHeaders.IF_UNMODIFIED_SINCE;
import static restlib.data.HttpHeaders.MAX_FORWARDS;
import static restlib.data.HttpHeaders.PRAGMA;
import static restlib.data.HttpHeaders.PROXY_AUTHORIZATION;
import static restlib.data.HttpHeaders.RANGE;
import static restlib.data.HttpHeaders.REFERER;
import static restlib.data.HttpHeaders.TE;
import static restlib.data.HttpHeaders.TRAILER;
import static restlib.data.HttpHeaders.TRANSFER_ENCODING;
import static restlib.data.HttpHeaders.UPGRADE;
import static restlib.data.HttpHeaders.USER_AGENT;
import static restlib.data.HttpHeaders.VIA;

import java.util.Locale;
import java.util.Map.Entry;

import org.junit.Test;

import restlib.data.ByteRangeSpec;
import restlib.data.CacheDirective;
import restlib.data.ChallengeMessage;
import restlib.data.Charset;
import restlib.data.ConnectionOption;
import restlib.data.ContentEncoding;
import restlib.data.EntityTag;
import restlib.data.Expectation;
import restlib.data.Header;
import restlib.data.HttpDate;
import restlib.data.HttpHeaders;
import restlib.data.Language;
import restlib.data.MediaRanges;
import restlib.data.Method;
import restlib.data.Preference;
import restlib.data.Protocol;
import restlib.data.Range;
import restlib.data.TransferCoding;
import restlib.data.UserAgent;
import restlib.data.Via;
import restlib.impl.ImmutableMapEntry;
import restlib.net.EmailAddress;
import restlib.net.Uri;
import restlib.test.WrapperTester;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.testing.NullPointerTester;

/**
 * Tests for Request, RequestBuilder, RequestImpl and RequestWrapper.
 */
public final class RequestTest {
    private static final UserAgent RESTLIB = UserAgent.parse("Restlib/1.0");
    
    @Test(expected = IllegalArgumentException.class)
    public void testBuilder$addCustomHeader_withStandardHeader() {
        Request.builder().addCustomHeader(HttpHeaders.ACCEPT, "");
    }
    
    @Test
    public void testBuilder$build() {
        final Request request =
                Request.builder()
                    .addCacheDirective(CacheDirective.MAX_STALE)
                    .addCacheDirectives(ImmutableList.of(CacheDirective.MUST_REVALIDATE))
                    .addCustomHeader(Header.create("X-Test"), "")
                    .addExpectation(Expectation.EXPECTS_100_CONTINUE)
                    .addPragmaCacheDirective(CacheDirective.MAX_STALE)
                    .addPragmaCacheDirectives(ImmutableList.of(CacheDirective.MUST_REVALIDATE))
                    .setAuthorizationCredentials(ChallengeMessage.base64ChallengeMessage("basic", "ABC="))
                    .setConnectionInfo(
                            ConnectionInfo.builder().addConnectionOption(ConnectionOption.KEEP_ALIVE).build())
                    .setContentInfo(
                            ContentInfo.builder().setLength(10).build())        
                    .setEntity("test")
                    .setFrom(EmailAddress.parse("test@example.org"))
                    .setMaxForwards(10)
                    .setMethod(Method.PATCH)
                    .setPreconditions(
                            RequestPreconditions.builder().setIfRange(EntityTag.strongTag("abc")).build())
                    .setPreferences(
                            ClientPreferences.builder().addAcceptedMediaRange(Preference.create(MediaRanges.APPLICATION_ATOM)).build())
                    .setProxyAuthorizationCredentials(ChallengeMessage.base64ChallengeMessage("basic", "ABC="))
                    .setReferrer(Uri.parse("http://www.example.com"))
                    .setUri(Uri.parse("http://www.example.com?query"))
                    .setUserAgent(RESTLIB)
                    .build();
        
        assertEquals(
                ChallengeMessage.base64ChallengeMessage("basic", "ABC="),
                request.authorizationCredentials().get());
        assertEquals(
                ImmutableSet.of(CacheDirective.MAX_STALE, CacheDirective.MUST_REVALIDATE),
                request.cacheDirectives());
        assertEquals(
                ConnectionInfo.builder().addConnectionOption(ConnectionOption.KEEP_ALIVE).build(),
                request.connectionInfo());
        assertEquals(
                ContentInfo.builder().setLength(10).build(),
                request.contentInfo());
        assertEquals(
                ImmutableListMultimap.of(Header.create("X-Test"), ""),
                request.customHeaders());
        assertEquals(
                "test",
                request.entity().get());
        assertEquals(
                ImmutableSet.of(Expectation.EXPECTS_100_CONTINUE),
                request.expectations());
        assertEquals(
                EmailAddress.parse("test@example.org"),
                request.from().get());
        assertEquals(
                Integer.valueOf(10),
                request.maxForwards().get());
        assertEquals(
                Method.PATCH,
                request.method());
        assertEquals(
                ImmutableSet.of(CacheDirective.MAX_STALE, CacheDirective.MUST_REVALIDATE),
                request.pragmaCacheDirectives());
        assertEquals(
                RequestPreconditions.builder().setIfRange(EntityTag.strongTag("abc")).build(),
                request.preconditions());
        assertEquals(
                ClientPreferences.builder().addAcceptedMediaRange(Preference.create(MediaRanges.APPLICATION_ATOM)).build(),
                request.preferences());
        assertEquals(
                ChallengeMessage.base64ChallengeMessage("basic", "ABC="),
                request.proxyAuthorizationCredentials().get());
        assertEquals(
                Uri.parse("http://www.example.com"),
                request.referrer().get());
        assertEquals(
                Uri.parse("http://www.example.com?query"),
                request.uri());
        assertEquals(
                RESTLIB,
                request.userAgent().get());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBuilder$setMaxForwards_withNegativeValue() {
        Request.builder().setMaxForwards(-1);
    } 
    
    @Test
    public void testNulls() {
        final NullPointerTester tester = new NullPointerTester()
            .setDefault(Header.class, HttpHeaders.ACCEPT)
            .setDefault(Method.class, Method.GET)
            .setDefault(Uri.class, Uri.parse("http://www.example.com"));
        
        tester.testAllPublicInstanceMethods(Request.builder());
        tester.testAllPublicInstanceMethods(
                Request.builder().setUri(Uri.parse("http://www.example.com")).build());
        tester.testAllPublicStaticMethods(Request.class);
    }
    
    @Test
    public void testParse() {
        final String uriScheme = "http";
        final String method = Method.POST.toString();
        final String requestTarget = "/a/b/c";
        final Iterable<Entry<String, String>> headers = 
                Iterables.transform(
                        ImmutableMultimap.<Object, Object> builder()
                            .put(ACCEPT, MediaRanges.APPLICATION_ATOM)
                            .put(ACCEPT_CHARSET, Charset.UTF_8)
                            .put(ACCEPT_ENCODING, ContentEncoding.GZIP)
                            .put(ACCEPT_LANGUAGE, Language.forLocale(Locale.ENGLISH))
                            .put(AUTHORIZATION, ChallengeMessage.base64ChallengeMessage("Basic", "ABC"))
                            .put(CACHE_CONTROL, CacheDirective.MAX_STALE)
                            .put(CONNECTION, ConnectionOption.KEEP_ALIVE)
                            .put(CONTENT_ENCODING, ContentEncoding.GZIP)
                            .put(CONTENT_LANGUAGE, Language.forLocale(Locale.ENGLISH))
                            .put(CONTENT_LENGTH, "100")
                            .put(CONTENT_LOCATION, "http://www.example.com")
                            .put(CONTENT_TYPE, MediaRanges.APPLICATION_ATOM)
                            .put(EXPECT, Expectation.EXPECTS_100_CONTINUE)
                            .put(FROM, "test@example.com")
                            .put(HOST, "www.example.com")
                            .put(IF_MATCH, EntityTag.weakTag("abc"))
                            .put(IF_MODIFIED_SINCE, HttpDate.create(0))
                            .put(IF_NONE_MATCH, EntityTag.weakTag("abc"))
                            .put(IF_RANGE, HttpDate.create(0))
                            .put(IF_UNMODIFIED_SINCE, HttpDate.create(0))
                            .put(MAX_FORWARDS, "10")
                            .put(PRAGMA, CacheDirective.NO_CACHE)
                            .put(PROXY_AUTHORIZATION, ChallengeMessage.base64ChallengeMessage("Basic", "ABC"))
                            .put(RANGE, Range.byteRange(ImmutableList.of(ByteRangeSpec.range(0, 100))))
                            .put(REFERER, "http://www.example.com")
                            .put(TE, TransferCoding.CHUNKED)
                            .put(TRAILER, HttpHeaders.ACCEPT)
                            .put(TRANSFER_ENCODING, TransferCoding.CHUNKED)
                            .put(UPGRADE, Protocol.HTTP_1_1)
                            .put(USER_AGENT, RESTLIB)
                            .put(VIA, Via.create(Protocol.HTTP_0_9, "www.example.com"))   
                            .put("X-Test", "")
                            .build().entries(), 
                        new Function<Entry<Object,Object>, Entry<String,String>>() {
                            @Override
                            public Entry<String, String> apply(
                                    final Entry<Object, Object> entry) {
                                return ImmutableMapEntry.create(entry.getKey().toString(), entry.getValue().toString());
                            }                           
                        });

        final Request request = 
                Request.parse(uriScheme, method, requestTarget, headers);
        
        assertEquals(request.authorizationCredentials().get(), ChallengeMessage.base64ChallengeMessage("Basic", "ABC"));
        assertEquals(request.cacheDirectives(), ImmutableSet.of(CacheDirective.MAX_STALE));
        assertEquals(request.connectionInfo().options(), ImmutableSet.of(ConnectionOption.KEEP_ALIVE));
        assertEquals(request.connectionInfo().trailerHeaders(), ImmutableSet.of(HttpHeaders.ACCEPT));
        assertEquals(request.connectionInfo().upgradeProtocols(), ImmutableSet.of(Protocol.HTTP_1_1));
        assertEquals(request.connectionInfo().via(), ImmutableList.of(Via.create(Protocol.HTTP_0_9, "www.example.com")));
        assertEquals(request.contentInfo().encodings(), ImmutableList.of(ContentEncoding.GZIP));
        assertEquals(request.contentInfo().languages(), ImmutableSet.of(Language.forLocale(Locale.ENGLISH)));
        assertEquals(request.contentInfo().length(), Optional.of(100L));
        assertEquals(request.contentInfo().mediaRange().get(), MediaRanges.APPLICATION_ATOM);
        assertEquals(request.customHeaders(), ImmutableListMultimap.of(Header.create("X-Test"), ""));
        assertEquals(request.expectations(), ImmutableSet.of(Expectation.EXPECTS_100_CONTINUE));
        assertEquals(request.maxForwards().get(), Integer.valueOf(10));
        assertEquals(request.method(), Method.POST);
        assertEquals(request.pragmaCacheDirectives(), ImmutableSet.of(CacheDirective.NO_CACHE));
        assertEquals(request.preconditions().ifMatchTags(), ImmutableSet.of(EntityTag.weakTag("abc")));
        assertEquals(request.preconditions().ifModifiedSinceDate().get(), HttpDate.create(0));
        assertEquals(request.preconditions().ifNoneMatchTags(), ImmutableSet.of(EntityTag.weakTag("abc")));
        assertEquals(request.preconditions().ifRange().get(), HttpDate.create(0));
        assertEquals(request.preconditions().ifUnmodifiedSinceDate().get(), HttpDate.create(0));
        assertEquals(request.preferences().acceptedCharsets(), ImmutableSet.of(Preference.create(Charset.UTF_8, 1000)));
        assertEquals(request.preferences().acceptedEncodings(), ImmutableSet.of(Preference.create(ContentEncoding.GZIP, 1000)));
        assertEquals(request.preferences().acceptedLanguages(), ImmutableSet.of(Preference.create(Language.forLocale(Locale.ENGLISH))));
        assertEquals(request.preferences().acceptedMediaRanges(), ImmutableSet.of(Preference.create(MediaRanges.APPLICATION_ATOM, 1000)));
        assertEquals(request.preferences().acceptedTransferEncodings(), ImmutableSet.of(Preference.create(TransferCoding.CHUNKED, 1000)));
        assertEquals(request.preferences().range().get(), Range.byteRange(ImmutableList.of(ByteRangeSpec.range(0, 100))));
        assertEquals(request.proxyAuthorizationCredentials().get(), ChallengeMessage.base64ChallengeMessage("Basic", "ABC"));
        assertEquals(request.referrer().get(), Uri.parse("http://www.example.com"));
        assertEquals(request.uri(), Uri.parse("http://www.example.com/a/b/c"));
        assertEquals(request.userAgent().get(), RESTLIB);
        
        assertEquals(
                Request.parse("http", "GET", "https://www.example.com/absolute?query", ImmutableMap.<String,String> of().entrySet()).uri(),
                Uri.parse("https://www.example.com/absolute?query"));
        
        try {
            Request.parse("http", "GET", "/absolute?query", ImmutableMap.<String,String> of().entrySet());
        } catch(final IllegalArgumentException expected){}
        
        final Iterable<Header> uniqueHeaders =
                ImmutableList.of(
                        AUTHORIZATION, CONTENT_LENGTH, CONTENT_LOCATION, CONTENT_TYPE, 
                        FROM, HOST, IF_MODIFIED_SINCE, IF_RANGE, IF_UNMODIFIED_SINCE,
                        MAX_FORWARDS, PROXY_AUTHORIZATION, RANGE, REFERER, USER_AGENT);
        
        for (final Header header : uniqueHeaders) {
            try{
                Request.parse(
                        "", "GET", 
                        "http://www.example.com", 
                        ImmutableMultimap.<String,String> of(header.toString(), "", header.toString(), "").entries());
            } catch(final IllegalArgumentException expected){}
        }
    }
    
    @Test
    public void testWrapper() {
        final RequestBuilder builder = Request.builder();
        WrapperTester.create(
                Request.class,
                new Function<Request,Request>() {
                    @Override
                    public Request apply(final Request request) {
                        return new RequestWrapper(request);
                    }                   
                })
            .useDefaultInstances()
            .executeTests(
                    //builder.build(),
                    builder.setUri(Uri.parse("http://www.example.com?query")).build(),
                    builder.addCacheDirective(CacheDirective.MAX_STALE).build(),
                    builder.addCustomHeader(Header.create("X-Test"), "").build(),
                    builder.addExpectation(Expectation.EXPECTS_100_CONTINUE).build(),
                    builder.addPragmaCacheDirective(CacheDirective.MUST_REVALIDATE).build(),
                    builder.setAuthorizationCredentials(ChallengeMessage.base64ChallengeMessage("basic", "ABC=")).build(),
                    builder.setConnectionInfo(ConnectionInfo.builder().addConnectionOption(ConnectionOption.KEEP_ALIVE).build()).build(),
                    builder.setContentInfo(ContentInfo.builder().setMediaRange(MediaRanges.APPLICATION_ATOM).build()).build(),
                    builder.setEntity("hello world").build(),
                    builder.setFrom(EmailAddress.parse("test@example.com")).build(),
                    builder.setMaxForwards(10).build(),
                    builder.setMethod(Method.PATCH).build(),
                    builder.setPreconditions(RequestPreconditions.builder().setIfModifiedSinceDate(HttpDate.create(1234)).build()).build(),
                    builder.setPreferences(ClientPreferences.builder().addAcceptedMediaRange(Preference.create(MediaRanges.APPLICATION_JSON)).build()).build(),
                    builder.setProxyAuthorizationCredentials(ChallengeMessage.base64ChallengeMessage("basic", "ABC=")).build(),
                    builder.setReferrer(Uri.parse("www.example.org")).build(),
                    builder.setUserAgent(RESTLIB).build());
    }
}
