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

import java.util.Set;

import restlib.data.CacheDirective;
import restlib.data.ChallengeMessage;
import restlib.data.Expectation;
import restlib.data.Header;
import restlib.data.Method;
import restlib.data.UserAgent;
import restlib.net.EmailAddress;
import restlib.net.Uri;

import com.google.common.base.Optional;
import com.google.common.collect.ListMultimap;

final class RequestImpl extends Request {
    private final Optional<ChallengeMessage> authorizationCredentials;
    private final Set<CacheDirective> cacheDirectives;
    private final ConnectionInfo connectionInfo;
    private final ContentInfo contentInfo;
    private final Optional<Object> entity;
    private final Set<Expectation> expectations;
    private final Optional<EmailAddress> from;
    private final ListMultimap<Header, String> headerMap;
    private final Optional<Integer> maxForwards;
    private final Method method;
    private final Set<CacheDirective> pragmaCacheDirectives;
    private final RequestPreconditions preconditions;
    private final ClientPreferences preferences;;
    private final Optional<ChallengeMessage> proxyAuthorizationCredentials;
    private final Optional<Uri> referrer;
    private final Uri uri;
    private final Optional<UserAgent> userAgent;
    
    RequestImpl(final RequestBuilder builder) {
        this.authorizationCredentials = builder.authorizationCredentials;
        this.cacheDirectives = builder.cacheDirectives.build();
        this.connectionInfo = builder.connectionInfo;
        this.contentInfo = builder.contentInfo;
        this.entity = builder.entity;
        this.expectations = builder.expecations.build();
        this.from = builder.from;
        this.headerMap = builder.headerMap.build();
        this.maxForwards = builder.maxForwards;
        this.method = builder.method;
        this.pragmaCacheDirectives = builder.pragmaCacheDirectives.build();
        this.preconditions = builder.preconditions;
        this.preferences = builder.preferences;
        this.proxyAuthorizationCredentials = builder.proxyAuthorizationCredentials;
        this.referrer = builder.referrer;
        this.uri = builder.uri.get();
        this.userAgent = builder.userAgent;
    }

    @Override
    public Optional<ChallengeMessage> authorizationCredentials() {
        return this.authorizationCredentials;
    }

    @Override
    public Set<CacheDirective> cacheDirectives() {
        return this.cacheDirectives;
    }

    @Override
    public ConnectionInfo connectionInfo() {
        return this.connectionInfo;
    }

    @Override
    public ContentInfo contentInfo() {
        return this.contentInfo;
    }

    @Override
    public ListMultimap<Header, String> customHeaders() {
        return this.headerMap;
    }

    @Override
    public Optional<Object> entity() {
        return this.entity;
    }

    @Override
    public Set<Expectation> expectations() {
        return this.expectations;
    }
    
    @Override
    public Optional<EmailAddress> from() {
        return this.from;
    }
    
    @Override
    public Optional<Integer> maxForwards() {
        return this.maxForwards;
    }

    @Override
    public Method method() {
        return this.method;
    }

    @Override
    public Set<CacheDirective> pragmaCacheDirectives() {
        return this.pragmaCacheDirectives;
    }

    @Override
    public RequestPreconditions preconditions() {
        return this.preconditions;
    }

    @Override
    public ClientPreferences preferences() {
        return this.preferences;
    }

    @Override
    public Optional<ChallengeMessage> proxyAuthorizationCredentials() {
        return this.proxyAuthorizationCredentials;
    }

    @Override
    public Optional<Uri> referrer() {
        return this.referrer;
    }

    @Override
    public Uri uri() {
        return this.uri;
    }

    @Override
    public Optional<UserAgent> userAgent() {
        return this.userAgent;
    }
}
