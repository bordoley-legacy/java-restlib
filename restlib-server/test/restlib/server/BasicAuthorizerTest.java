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

import java.util.concurrent.ExecutionException;

import org.junit.Test;

import restlib.Request;
import restlib.Response;
import restlib.data.ChallengeMessage;
import restlib.data.Status;
import restlib.net.Uri;

import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import com.google.common.util.concurrent.ListenableFuture;

public final class BasicAuthorizerTest {
    private static final String realm = "www.example.org";
    private static final Authorizer MOCK_AUTHORIZER =
            new BasicAuthorizer(realm) {
                @Override
                protected ListenableFuture<Response> authenticate(final Request request, final String user, final String pwd) {
                    return user.equals(pwd) ? 
                            FutureResponses.SUCCESS_OK :
                                FutureResponses.CLIENT_ERROR_UNAUTHORIZED;
                }};

    @Test            
    public void authorizeTest() throws InterruptedException, ExecutionException { 
        assertEquals(
                Status.CLIENT_ERROR_UNAUTHORIZED, 
                MOCK_AUTHORIZER.authenticate(
                        Request.builder().setUri(Uri.parse("http://www.example.com")).build()).get().status());
        
        final Request request = 
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setAuthorizationCredentials(
                            ChallengeMessage
                                .base64ChallengeMessage("Basic",
                                    BaseEncoding.base64().encode(
                                            "test:test".getBytes(Charsets.UTF_8))))
                    .build();
        assertEquals(Status.SUCCESS_OK, MOCK_AUTHORIZER.authenticate(request).get().status());
    }
    
    @Test
    public void authenticationChallengesTest() {
        final ChallengeMessage expectedChallenge =
                ChallengeMessage.basicAuthenticationChallenge(realm);
        assertEquals(expectedChallenge, MOCK_AUTHORIZER.authenticationChallenge());
    }
    
    @Test
    public void schemeTest() {
        assertEquals("basic", MOCK_AUTHORIZER.scheme());
    }
}
