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

import javax.annotation.concurrent.Immutable;

import restlib.data.CacheDirective;
import restlib.data.ChallengeMessage;
import restlib.data.Expectation;
import restlib.data.Header;
import restlib.data.Method;
import restlib.data.UserAgent;
import restlib.net.EmailAddress;
import restlib.net.Uri;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ListMultimap;

/**
 * An implementation of Request which forwards all its method calls to another instance of Request. 
 * Subclasses should override one or more methods to modify the behavior of 
 * the backing Request as desired per the decorator pattern.
 */
@Immutable
public class RequestWrapper extends Request {
    private final Request delegate;
    
    /**
     * Constructs an instance of RequestWrapper that forwards all method calls to delegate.
     * @param delegate a non-null instance of Request.
     * @throws NullPointerException if delegate is null.
     */
    protected RequestWrapper(final Request delegate) {
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public Optional<ChallengeMessage> authorizationCredentials() {
        return delegate.authorizationCredentials();
    }

    @Override
    public Set<CacheDirective> cacheDirectives() {
        return delegate.cacheDirectives();
    }

    @Override
    public ConnectionInfo connectionInfo() {
        return delegate.connectionInfo();
    }

    @Override
    public ContentInfo contentInfo() {
        return delegate.contentInfo();
    }
    
    @Override
    public ListMultimap<Header, String> customHeaders() {
        return delegate.customHeaders();
    }

    @Override
    public Optional<Object> entity() {
        return delegate.entity();
    }
    
    @Override
    public Set<Expectation> expectations() {
        return delegate.expectations();
    }
    
    @Override
    public Optional<EmailAddress> from() {
        return delegate.from();
    }

    @Override
    public Optional<Integer> maxForwards() {
        return delegate.maxForwards();
    }

    @Override
    public Method method() {
        return delegate.method();
    }

    @Override
    public Set<CacheDirective> pragmaCacheDirectives() {
        return delegate.pragmaCacheDirectives();
    }

    @Override
    public RequestPreconditions preconditions() {
        return delegate.preconditions();
    }

    @Override
    public ClientPreferences preferences() {
        return delegate.preferences();
    }

    @Override
    public Optional<ChallengeMessage> proxyAuthorizationCredentials() {
        return delegate.proxyAuthorizationCredentials();
    }

    @Override
    public Optional<Uri> referrer() {
        return delegate.referrer();
    }

    @Override
    public Uri uri() {
        return delegate.uri();
    }

    @Override
    public Optional<UserAgent> userAgent() {
        return delegate.userAgent();
    }
}
