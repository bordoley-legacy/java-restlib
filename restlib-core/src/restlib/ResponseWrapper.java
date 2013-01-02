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

import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import restlib.data.CacheDirective;
import restlib.data.ChallengeMessage;
import restlib.data.EntityTag;
import restlib.data.Header;
import restlib.data.HttpDate;
import restlib.data.Method;
import restlib.data.RangeUnit;
import restlib.data.Status;
import restlib.data.UserAgent;
import restlib.data.Warning;
import restlib.net.Uri;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ListMultimap;

/**
 * An implementation of Response which forwards 
 * all its method calls to another instance of Response. 
 * Subclasses should override one or more methods to modify the behavior of 
 * the backing Response as desired per the decorator pattern.
 */
@Immutable
public class ResponseWrapper extends Response {
    private final Response delegate;
    
    /**
     * Constructs an instance of ResponseWrapper that forwards all method calls to delegate.
     * @param delegate a non-null instance of Response.
     * @throws NullPointerException if delegate is null.
     */
    protected ResponseWrapper(final Response delegate) {
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public Set<RangeUnit> acceptedRangeUnits() {
        return delegate.acceptedRangeUnits();
    }

    @Override
    public Optional<Long> age() {
        return delegate.age();
    }

    @Override
    public Set<Method> allowedMethods() {
        return delegate.allowedMethods();
    }

    @Override
    public Set<ChallengeMessage> authenticationChallenges() {
        return delegate.authenticationChallenges();
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
    public Optional<HttpDate> date() {
        return delegate.date();
    }

    @Override
    public Optional<Object> entity() {
        return delegate.entity();
    }

    @Override
    public Optional<EntityTag> entityTag() {
        return delegate.entityTag();
    }
    
    @Override
    public Optional<HttpDate> expires() {
        return delegate.expires();
    }

    @Override
    public Optional<HttpDate> lastModified() {
        return delegate.lastModified();
    }

    @Override
    public Optional<Uri> location() {
        return delegate.location();
    }

    @Override
    public Set<ChallengeMessage> proxyAuthenticationChallenge() {
        return delegate.proxyAuthenticationChallenge();
    }

    @Override
    public Optional<HttpDate> retryAfterDate() {
        return delegate.retryAfterDate();
    }

    @Override
    public Optional<UserAgent> server() {
        return delegate.server();
    }

    @Override
    public Status status() {
        return delegate.status();
    }

    @Override
    public Set<Header> vary() {
        return delegate.vary();
    }

    @Override
    public List<Warning> warnings() {
        return delegate.warnings();
    }
}
