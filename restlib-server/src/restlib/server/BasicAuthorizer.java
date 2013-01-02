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

import restlib.Request;
import restlib.Response;
import restlib.data.ChallengeMessage;
import restlib.impl.Optionals;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;
import com.google.common.util.concurrent.ListenableFuture;

public abstract class BasicAuthorizer implements Authorizer{    
    private static final String BASIC_CRED_SPLIT_REGEX = ":";
    
    private final ChallengeMessage authChallenge;
    
    protected BasicAuthorizer(final String realm){        
        this.authChallenge = ChallengeMessage.basicAuthenticationChallenge(realm);
    }
    
    protected abstract ListenableFuture<Response> authenticate(final Request request, final String user, final String pwd);
    
    @Override
    public final ListenableFuture<Response> authenticate(final Request request) {
        Preconditions.checkNotNull(request);
        final Optional<ChallengeMessage> credentials = request.authorizationCredentials();
        
        if (Optionals.isAbsent(credentials)) {
            return FutureResponses.CLIENT_ERROR_UNAUTHORIZED;
        }
        
        final ChallengeMessage.Base64 base64Credentials =
                (ChallengeMessage.Base64) credentials.get();
        
        final String encodedCreds = base64Credentials.base64data();
        final String decodedCredsString = new String (BaseEncoding.base64().decode(encodedCreds));
        final String[] args = decodedCredsString.split(BASIC_CRED_SPLIT_REGEX, 2);
        
        if (args.length != 2) {
            return FutureResponses.CLIENT_ERROR_UNAUTHORIZED;
        }
             
        return authenticate(request, args[0], args[1]);     
    }
    
    public final ChallengeMessage authenticationChallenge() {
        return this.authChallenge;
    }
    
    public final String scheme() {
        return "basic";
    }
}
