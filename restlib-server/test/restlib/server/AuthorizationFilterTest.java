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
import static org.junit.Assert.assertTrue;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import restlib.Request;
import restlib.Response;
import restlib.data.ChallengeMessage;
import restlib.data.Status;
import restlib.net.Uri;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;


public final class AuthorizationFilterTest {
    private static final Authorizer MOCK_BASIC_AUTHORIZER =
            new BasicAuthorizer("www.example.com") {
                @Override
                protected Response authenticate(final Request request, final String user, final String pwd) {
                    return user.equals(pwd) ? 
                            Status.SUCCESS_OK.toResponse() :
                                Status.CLIENT_ERROR_UNAUTHORIZED.toResponse();
                }};
                
    private static final Resource MOCK_RESOURCE =
            new Resource() {
                @Override
                public Response acceptMessage(Request request, Object message) {
                    return Status.SUCCESS_OK.toResponse();
                }

                @Override
                public Route route() {
                    return Route.NONE;
                }

                @Override
                public Response handle(Request request) {
                    return Status.SUCCESS_OK.toResponse();
                }};
                
                
    @Test(expected = NullPointerException.class)
    public void handle_withNull() {
        final Resource filter =
                Resources.authorizedResource(
                        MOCK_RESOURCE, ImmutableList.<Authorizer> of());
        filter.handle(null);
    }
    
    @Test
    public void handle_withNoAuthorizationFiltersOrResources_clientErrorNotFound() {
        final Resource noAuthorizationFilter =
                Resources.authorizedResource(MOCK_RESOURCE, ImmutableList.<Authorizer> of());
        
        final Response noAuthorizationFiltersResponse =
                noAuthorizationFilter.handle(Request.builder().build());      
        assertEquals(
                Status.CLIENT_ERROR_FORBIDDEN, 
                noAuthorizationFiltersResponse.status());
    }
    
    @Test
    public void handle_withBasicAuthorization_successOK() {
        final Resource basicAuthorizationFilter =
                Resources.authorizedResource(
                        MOCK_RESOURCE, ImmutableList.<Authorizer> of(MOCK_BASIC_AUTHORIZER));
        
        final Request basicSuccessRequest =
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setAuthorizationCredentials(
                            ChallengeMessage
                                .base64ChallengeMessage("Basic", 
                                    Base64.encodeBase64String(
                                            "test:test".getBytes(Charsets.UTF_8))))
                    .build();
        final Response basicAuthorizationFilterSuccessResponse =
                basicAuthorizationFilter.handle(basicSuccessRequest);
        assertEquals(
                    Status.SUCCESS_OK,
                    basicAuthorizationFilterSuccessResponse.status());
    }
    
    
    @Test
    public void handle_withBasicAuthorization_clientErrorUnauthorized() {
        final Resource basicAuthorizationFilter =
                Resources.authorizedResource(MOCK_RESOURCE, ImmutableList.of(MOCK_BASIC_AUTHORIZER));
        
        final Request basicFailRequest =
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setAuthorizationCredentials(
                            ChallengeMessage.
                                base64ChallengeMessage("Basic",
                                    Base64.encodeBase64String(
                                            "test:fail".getBytes(Charsets.UTF_8))))
                    .build();
        final Response basicAuthorizationFilterFailResponse =
                basicAuthorizationFilter.handle(basicFailRequest);
        assertEquals(
                    Status.CLIENT_ERROR_UNAUTHORIZED,
                    basicAuthorizationFilterFailResponse.status());
        assertTrue(basicAuthorizationFilterFailResponse
                    .authenticationChallenges()
                    .contains(MOCK_BASIC_AUTHORIZER.authenticationChallenge()));
    }
}
