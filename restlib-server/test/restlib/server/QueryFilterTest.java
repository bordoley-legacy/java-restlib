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


package restlib.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static restlib.data.ExtensionHeaders.X_HTTP_METHOD_OVERRIDE;
import static restlib.data.HttpHeaders.ACCEPT;
import static restlib.data.HttpHeaders.ACCEPT_CHARSET;
import static restlib.data.HttpHeaders.ACCEPT_ENCODING;
import static restlib.data.HttpHeaders.ACCEPT_LANGUAGE;
import static restlib.data.HttpHeaders.ACCEPT_RANGES;
import static restlib.data.HttpHeaders.AGE;
import static restlib.data.HttpHeaders.ALLOW;
import static restlib.data.HttpHeaders.AUTHORIZATION;
import static restlib.data.HttpHeaders.CACHE_CONTROL;
import static restlib.data.HttpHeaders.CONNECTION;
import static restlib.data.HttpHeaders.CONTENT_ENCODING;
import static restlib.data.HttpHeaders.CONTENT_LANGUAGE;
import static restlib.data.HttpHeaders.CONTENT_LENGTH;
import static restlib.data.HttpHeaders.CONTENT_LOCATION;
import static restlib.data.HttpHeaders.CONTENT_MD5;
import static restlib.data.HttpHeaders.CONTENT_RANGE;
import static restlib.data.HttpHeaders.CONTENT_TYPE;
import static restlib.data.HttpHeaders.DATE;
import static restlib.data.HttpHeaders.ENTITY_TAG;
import static restlib.data.HttpHeaders.EXPECT;
import static restlib.data.HttpHeaders.EXPIRES;
import static restlib.data.HttpHeaders.FROM;
import static restlib.data.HttpHeaders.HOST;
import static restlib.data.HttpHeaders.IF_MATCH;
import static restlib.data.HttpHeaders.IF_MODIFIED_SINCE;
import static restlib.data.HttpHeaders.IF_NONE_MATCH;
import static restlib.data.HttpHeaders.IF_RANGE;
import static restlib.data.HttpHeaders.IF_UNMODIFIED_SINCE;
import static restlib.data.HttpHeaders.LAST_MODIFIED;
import static restlib.data.HttpHeaders.LOCATION;
import static restlib.data.HttpHeaders.MAX_FORWARDS;
import static restlib.data.HttpHeaders.PRAGMA;
import static restlib.data.HttpHeaders.PROXY_AUTHENTICATE;
import static restlib.data.HttpHeaders.PROXY_AUTHORIZATION;
import static restlib.data.HttpHeaders.RANGE;
import static restlib.data.HttpHeaders.REFERER;
import static restlib.data.HttpHeaders.RETRY_AFTER;
import static restlib.data.HttpHeaders.SERVER;
import static restlib.data.HttpHeaders.TE;
import static restlib.data.HttpHeaders.TRAILER;
import static restlib.data.HttpHeaders.TRANSFER_ENCODING;
import static restlib.data.HttpHeaders.UPGRADE;
import static restlib.data.HttpHeaders.USER_AGENT;
import static restlib.data.HttpHeaders.VARY;
import static restlib.data.HttpHeaders.VIA;
import static restlib.data.HttpHeaders.WARNING;
import static restlib.data.HttpHeaders.WWW_AUTHENTICATE;

import org.junit.Test;

import restlib.Request;
import restlib.data.ChallengeMessage;
import restlib.data.Form;
import restlib.data.Header;
import restlib.data.HttpHeaders;
import restlib.data.MediaRanges;
import restlib.data.Method;
import restlib.net.Uri;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public final class QueryFilterTest {
    private static final Function<Request,Request> TEST_FILTER_ALL_HEADERS_ALLOWED = 
            RequestFilters.queryFilter(
                    ImmutableList.of(
                            ACCEPT,
                            ACCEPT_CHARSET,
                            ACCEPT_ENCODING,
                            ACCEPT_LANGUAGE,
                            ACCEPT_RANGES,
                            AGE,
                            ALLOW,
                            AUTHORIZATION,
                            CACHE_CONTROL,
                            CONNECTION,
                            CONTENT_ENCODING,
                            CONTENT_LANGUAGE,
                            CONTENT_LENGTH,
                            CONTENT_LOCATION,
                            CONTENT_MD5,
                            CONTENT_RANGE,
                            CONTENT_TYPE,
                            DATE,
                            ENTITY_TAG,
                            EXPECT,
                            EXPIRES,
                            FROM,
                            HOST,
                            IF_MATCH,
                            IF_MODIFIED_SINCE,
                            IF_NONE_MATCH,
                            IF_RANGE,
                            IF_UNMODIFIED_SINCE,
                            LAST_MODIFIED,
                            LOCATION,
                            MAX_FORWARDS,
                            PRAGMA,
                            PROXY_AUTHENTICATE,
                            PROXY_AUTHORIZATION,
                            RANGE,
                            REFERER,
                            RETRY_AFTER,
                            SERVER,
                            TE,
                            TRAILER,
                            TRANSFER_ENCODING,
                            UPGRADE,
                            USER_AGENT,
                            VARY,
                            VIA,
                            WARNING,
                            WWW_AUTHENTICATE,
                            X_HTTP_METHOD_OVERRIDE));
    
    private static final Function<Request, Request> TEST_FILTER_NO_HEADERS_ALLOWED = 
            RequestFilters.queryFilter(ImmutableList.<Header> of());
    
    @Test
    public void apply_authorization_OverrideNotAllowedByFilter() {
        final String authorization = "BASIC FDJKLDHEJWR";
        final String form = 
                Form.builder()
                    .put(HttpHeaders.AUTHORIZATION.toString(), authorization)
                    .build().toString();
        final Request request = 
                Request.builder()
                    .setMethod(Method.POST)
                    .setUri(Uri.parse("http://example.com/?" + form))
                    .build();
        
        final Request filteredRequest = TEST_FILTER_NO_HEADERS_ALLOWED.apply(request);
        assertEquals(Optional.absent(), filteredRequest.authorizationCredentials());
    }
    
    @Test
    public void apply_authorizationOverride() {
        final String authorization = "BASIC FDJKLDHEJWR";
        final String form = 
                Form.builder()
                    .put(HttpHeaders.AUTHORIZATION.toString(), authorization)
                    .build().toString();
        final Request request = 
                Request.builder()
                    .setMethod(Method.POST)
                    .setUri(Uri.parse("http://example.com/?" + form))
                    .build();
        
        final Request filteredRequest = TEST_FILTER_ALL_HEADERS_ALLOWED.apply(request);
        assertEquals(authorization, filteredRequest.authorizationCredentials().get().toString());
    }
    
    @Test
    public void apply_caseInsensitiveMatchHeaders() {
        final String form = Form.builder()
                    .put(ACCEPT.toString().toUpperCase(), MediaRanges.APPLICATION_ATOM.toString())
                    .put(AUTHORIZATION.toString().toUpperCase(), "BASIC FDJKLDHEJWR")
                    .put(X_HTTP_METHOD_OVERRIDE.toString().toUpperCase(), "PUT")
                    .build().toString();
        final Request request = 
                Request.builder()
                    .setMethod(Method.POST)
                    .setUri(Uri.parse("http://example.com/?" + form))
                    .build();
        final Request filteredRequest = TEST_FILTER_ALL_HEADERS_ALLOWED.apply(request);
        final Form filteredForm = Form.parse(filteredRequest.uri().query());
        
        assertFalse(filteredForm.containsKey(ACCEPT.toString().toUpperCase()));
        assertFalse(filteredForm.containsKey(AUTHORIZATION.toString().toUpperCase()));
        assertFalse(filteredForm.containsKey(X_HTTP_METHOD_OVERRIDE.toString().toUpperCase()));    
    }
    
    @Test
    public void apply_emptyQuery_sameRequest() {
        final Request request = 
                Request.builder()
                    .setMethod(Method.POST)
                    .setUri(Uri.parse("http://example.com"))
                    .build();
        final Request filteredRequest = TEST_FILTER_ALL_HEADERS_ALLOWED.apply(request);
        assertEquals(request.uri(), filteredRequest.uri());
    }
    
    @Test
    public void apply_getWithPostMethodOverride() {
        final String form = 
                Form.builder()
                    .put(X_HTTP_METHOD_OVERRIDE.toString(), "post")
                    .build().toString();
        final Request request = 
                Request.builder()
                    .setMethod(Method.GET)
                    .setUri(Uri.parse("http://example.com/?" + form))
                    .build();
        
        // Method Override is only allowed for POST requests
        final Request filteredRequest = TEST_FILTER_ALL_HEADERS_ALLOWED.apply(request);
        assertEquals(Method.GET, filteredRequest.method());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void apply_invalidAuthorization() {
        final String form = 
                Form.builder()
                    .put(HttpHeaders.AUTHORIZATION.toString(), "FDJKLDHEJWR")
                    .build().toString();
        
        final ChallengeMessage auth = ChallengeMessage.base64ChallengeMessage("basic", "ABCDE");
        final Request request = 
                Request.builder()
                    .setMethod(Method.POST)
                    .setUri(Uri.parse("http://example.com/?" + form))
                    .setAuthorizationCredentials(auth)
                    .build();
        
        TEST_FILTER_ALL_HEADERS_ALLOWED.apply(request);
    }
    
    @Test
    public void apply_postWithPutMethod_OverrideNotAllowedByFilter() {
        final String form = 
                Form.builder()
                    .put(X_HTTP_METHOD_OVERRIDE.toString(), "PUT")
                    .build().toString();
        final Request request = 
                Request.builder()
                    .setMethod(Method.POST)
                    .setUri(Uri.parse("http://example.com/?" + form))
                    .build();
        
        final Request filteredRequest = TEST_FILTER_NO_HEADERS_ALLOWED.apply(request);
        assertEquals(Method.POST, filteredRequest.method());
    }
    
    @Test
    public void apply_postWithPutMethodOverride() {
        final String form = 
                Form.builder()
                    .put(X_HTTP_METHOD_OVERRIDE.toString(), "PUT")
                    .build().toString();
        final Request request = 
                Request.builder()
                    .setMethod(Method.POST)
                    .setUri(Uri.parse("http://example.com/?" + form))
                    .build();
        
        final Request filteredRequest = TEST_FILTER_ALL_HEADERS_ALLOWED.apply(request);
        assertEquals(Method.PUT, filteredRequest.method());
    }
    
    @Test
    public void apply_validateFilteredQueryStrings() {
        final String form = 
                Form.builder()
                    .put(ACCEPT.toString(), MediaRanges.APPLICATION_ATOM.toString())
                    .put(AUTHORIZATION.toString(), "BASIC FDJKLDHEJWR")
                    .put(X_HTTP_METHOD_OVERRIDE.toString(), "PUT")
                    .build().toString();
        final Request request = 
                Request.builder()
                    .setMethod(Method.POST)
                    .setUri(Uri.parse("http://example.com/?" + form))
                    .build();
        final Request filteredRequest = TEST_FILTER_ALL_HEADERS_ALLOWED.apply(request);
        final Form filteredForm = Form.parse(filteredRequest.uri().query());
        
        assertFalse(filteredForm.containsKey(ACCEPT.toString()));
        assertFalse(filteredForm.containsKey(AUTHORIZATION.toString()));
        assertFalse(filteredForm.containsKey(X_HTTP_METHOD_OVERRIDE.toString()));    
    }
}
