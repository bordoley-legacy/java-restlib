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


package restlib.server.connector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static restlib.data.HttpHeaders.ACCEPT_RANGES;
import static restlib.data.HttpHeaders.AGE;
import static restlib.data.HttpHeaders.ALLOW;
import static restlib.data.HttpHeaders.CACHE_CONTROL;
import static restlib.data.HttpHeaders.CONNECTION;
import static restlib.data.HttpHeaders.CONTENT_ENCODING;
import static restlib.data.HttpHeaders.CONTENT_LANGUAGE;
import static restlib.data.HttpHeaders.CONTENT_LENGTH;
import static restlib.data.HttpHeaders.CONTENT_LOCATION;
import static restlib.data.HttpHeaders.CONTENT_RANGE;
import static restlib.data.HttpHeaders.CONTENT_TYPE;
import static restlib.data.HttpHeaders.DATE;
import static restlib.data.HttpHeaders.ENTITY_TAG;
import static restlib.data.HttpHeaders.EXPIRES;
import static restlib.data.HttpHeaders.LAST_MODIFIED;
import static restlib.data.HttpHeaders.LOCATION;
import static restlib.data.HttpHeaders.PROXY_AUTHENTICATE;
import static restlib.data.HttpHeaders.RETRY_AFTER;
import static restlib.data.HttpHeaders.SERVER;
import static restlib.data.HttpHeaders.TRAILER;
import static restlib.data.HttpHeaders.UPGRADE;
import static restlib.data.HttpHeaders.VARY;
import static restlib.data.HttpHeaders.VIA;
import static restlib.data.HttpHeaders.WARNING;
import static restlib.data.HttpHeaders.WWW_AUTHENTICATE;

import java.util.Collection;
import java.util.Locale;

import org.junit.Test;

import restlib.ConnectionInfo;
import restlib.ContentInfo;
import restlib.Response;
import restlib.data.CacheDirective;
import restlib.data.ChallengeMessage;
import restlib.data.Comment;
import restlib.data.ConnectionOption;
import restlib.data.ContentEncoding;
import restlib.data.ContentRange;
import restlib.data.EntityTag;
import restlib.data.Header;
import restlib.data.HttpDate;
import restlib.data.HttpHeaders;
import restlib.data.Language;
import restlib.data.MediaRanges;
import restlib.data.Method;
import restlib.data.Protocol;
import restlib.data.RangeUnit;
import restlib.data.Status;
import restlib.data.TransferCoding;
import restlib.data.UserAgent;
import restlib.data.Via;
import restlib.data.Warning;
import restlib.impl.Optionals;
import restlib.net.HostPort;
import restlib.net.Uri;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public final class ConnectorResponseTest {
    private static final Joiner joiner = Joiner.on(", ");
    
    private static class MockConnectorResponse extends ConnectorResponse {
        Protocol version;
        Status status;
        Multimap<Header, Object> headers =  HashMultimap.create();
        
        String getHeader(final Header header) {
            return Iterables.getFirst(headers.get(header), "").toString();
        }
        
        @Override
        protected MockConnectorResponse addHeader(final Header header, final Object value) {
            headers.put(header, value);
            return this;
        }

        @Override
        protected MockConnectorResponse setProtocolVersion(final Protocol version) {
            Preconditions.checkNotNull(version);
            this.version = version;
            return this;
        }

        @Override
        protected MockConnectorResponse setStatus(final Status status) {
            Preconditions.checkNotNull(status);
            this.status = status;
            return this;
        }
    };
    
    private void validate(final Response response, final MockConnectorResponse connectorResponse) {
        assertEquals(
                response.status(), connectorResponse.status);
        
        assertEquals(Protocol.HTTP_1_1, connectorResponse.version);
        
        assertEquals(
                joiner.join(response.acceptedRangeUnits()), 
                connectorResponse.getHeader(ACCEPT_RANGES));
        
        final String ageStr = connectorResponse.getHeader(AGE);
        final Optional<Long> age = 
                ageStr.isEmpty() ? Optional.<Long> absent() : Optional.of(Long.parseLong(ageStr));
        assertEquals(response.age(), age);
        
        assertEquals(
                joiner.join(response.allowedMethods()), 
                connectorResponse.getHeader(ALLOW));
           
        assertEquals(
                joiner.join(response.authenticationChallenges()), 
                connectorResponse.getHeader(WWW_AUTHENTICATE));
        
        assertEquals(
                joiner.join(response.cacheDirectives()), 
                connectorResponse.getHeader(CACHE_CONTROL));
        
        assertEquals(
                joiner.join(response.connectionInfo().options()), 
                connectorResponse.getHeader(CONNECTION));
        
        assertEquals(
                joiner.join(response.connectionInfo().trailerHeaders()), 
                connectorResponse.getHeader(TRAILER));
        
        assertEquals(
                joiner.join(response.connectionInfo().upgradeProtocols()), 
                connectorResponse.getHeader(UPGRADE));
        
        assertEquals(
                joiner.join(response.connectionInfo().via()), 
                connectorResponse.getHeader(VIA));
        
        assertEquals(
                Optionals.toStringOrEmpty(response.contentInfo().mediaRange()), 
                connectorResponse.getHeader(CONTENT_TYPE));
        
        assertEquals(
                joiner.join(
                        response.contentInfo().encodings()), 
                connectorResponse.getHeader(CONTENT_ENCODING));
        
        assertEquals(
                joiner.join(
                        response.contentInfo().languages()), 
                connectorResponse.getHeader(CONTENT_LANGUAGE));
        
        final String contentLengthStr = connectorResponse.getHeader(CONTENT_LENGTH);
        final Optional<Long> contentlength = 
                contentLengthStr.isEmpty() ? 
                        Optional.<Long> absent() : 
                            Optional.of(Long.parseLong(contentLengthStr));
        assertEquals(
                response.contentInfo().length(), contentlength);
        
        assertEquals(
                Optionals.toStringOrEmpty(response.contentInfo().location()), 
                connectorResponse.getHeader(CONTENT_LOCATION));
        
        assertEquals(
                Optionals.toStringOrEmpty(response.contentInfo().range()), 
                connectorResponse.getHeader(CONTENT_RANGE));
             
        assertEquals(
                Optionals.toStringOrEmpty(response.date()), 
                connectorResponse.getHeader(DATE));
        
        assertEquals(
                Optionals.toStringOrEmpty(response.entityTag()), 
                connectorResponse.getHeader(ENTITY_TAG));
        
        assertEquals(
                Optionals.toStringOrEmpty(response.expires()), 
                connectorResponse.getHeader(EXPIRES));
        
        assertEquals(
                Optionals.toStringOrEmpty(response.lastModified()), 
                connectorResponse.getHeader(LAST_MODIFIED));
        
        assertEquals(
                Optionals.toStringOrEmpty(response.location()), 
                connectorResponse.getHeader(LOCATION));
        
        assertEquals(
                joiner.join(
                        response.proxyAuthenticationChallenge()), 
                connectorResponse.getHeader(PROXY_AUTHENTICATE));
        
        assertEquals(
                Optionals.toStringOrEmpty(response.retryAfterDate()), 
                connectorResponse.getHeader(RETRY_AFTER));
        
        assertEquals(
                Optionals.toStringOrEmpty(response.server()), 
                connectorResponse.getHeader(SERVER));
        
        assertEquals(
                joiner.join(response.vary()), 
                connectorResponse.getHeader(VARY));
        
        assertEquals(
                joiner.join(response.warnings()), 
                connectorResponse.getHeader(WARNING));
    }
    
    @Test
    public void populateTest_withCustomHeaders() {
        final Header header = Header.create("x-test");
        final Response response =
                Response.builder()
                    .addCustomHeader(header, "a")
                    .addCustomHeader(header, "b")
                    .build();
        final MockConnectorResponse connectorResponse =
                (MockConnectorResponse) 
                    new MockConnectorResponse().populate(response);
        
        final Collection<Object> values = connectorResponse.headers.get(header);
        assertTrue(values.size() == 2);
        assertTrue(values.contains("a"));
        assertTrue(values.contains("b"));
    }
    
    @Test
    public void populateTest_withNone() {    
        validate(Response.builder().build(),  
                (MockConnectorResponse) new MockConnectorResponse().populate(Response.builder().build()));
    }
    
    @Test
    public void populateTest_withValid() {
        final int age = 100;
        
        final HttpDate date = HttpDate.create(System.currentTimeMillis());        
        final String entity = "TEST";
        final EntityTag entityTag = EntityTag.weakTag("ABCD");
        final HttpDate expires = HttpDate.create(date.time() + 100000);
        final HttpDate retry = HttpDate.create(date.time() + 1000000);
        final Protocol upgradeProtocol = Protocol.create("SSL", "1.1");
        final Uri uri = Uri.parse("http://www.example.com");
        final Via via = Via.create(Protocol.HTTP_1_1, "p.example.net", Comment.parse("(Apache/1.1)"));
        
        final ConnectionInfo connectionInfo =
                ConnectionInfo.builder()
                    .addConnectionOption(ConnectionOption.KEEP_ALIVE)
                    .addTrailerHeader(HttpHeaders.ACCEPT)
                    .addTrailerHeader(HttpHeaders.ACCEPT_LANGUAGE)
                    .addTransferEncoding(TransferCoding.CHUNKED)
                    .addUpgradeProtocol(upgradeProtocol)
                    .addVia(via)
                    .build();
        
        final ContentInfo contentInfo =
                ContentInfo.builder()
                    .addEncoding(ContentEncoding.GZIP)
                    .addLanguage(Language.forLocale(Locale.ENGLISH))
                    .setLength(100)
                    .setLocation(uri)
                    .setMediaRange(MediaRanges.APPLICATION_ATOM_FEED)
                    .setRange(
                            ContentRange.byteRange(0, 20, 100))                
                    .build();
        
        final Response response = 
                Response.builder()
                    .addAcceptedRangeUnit(RangeUnit.BYTES)
                    .addAllowedMethods(ImmutableList.of(Method.GET, Method.HEAD))
                    .addAuthenticationChallenge(
                            ChallengeMessage.basicAuthenticationChallenge("www.example.org"))
                    .addCacheDirective(CacheDirective.MAX_STALE)
                    //.addCustomHeader(header, value)
                    .addProxyAuthenticationChallenge(
                            ChallengeMessage.basicAuthenticationChallenge("www.example.org"))
                    .addVaryHeader(HttpHeaders.ACCEPT)
                    .addWarning(
                            Warning.create(110, HostPort.parse("example.com"), "Response is State", date))
                    .setAge(age)
                    .setConnectionInfo(connectionInfo)
                    .setContentInfo(contentInfo)
                    .setDate(date)
                    .setEntity(entity)
                    .setEntityTag(entityTag)
                    .setExpires(expires)
                    .setLastModified(date)
                    .setLocation(uri)
                    .setRetryAfterDate(retry)
                    .setServer(UserAgent.parse("restlib/1.0"))
                    .setStatus(Status.CLIENT_ERROR_BAD_REQUEST)
                    .build();
        
        final MockConnectorResponse connectorResponse = new MockConnectorResponse();
        connectorResponse.populate(response);
        
        validate(response, connectorResponse);
    }
}
