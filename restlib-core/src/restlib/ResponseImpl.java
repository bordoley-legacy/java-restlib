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
import com.google.common.collect.ListMultimap;

final class ResponseImpl extends Response {
    private final Set<RangeUnit> acceptedRangeUnits;
    private final Optional<Long> age;
    private final Set<Method> allowedMethods;
    private final Set<ChallengeMessage> authenticationChallenges;
    private final Set<CacheDirective> cacheDirectives;
    private final ConnectionInfo connectionInfo;
    private final ContentInfo contentInfo;
    private final ListMultimap<Header, String> customHeaders;
    private final Optional<HttpDate> date;
    private final Optional<Object> entity;
    private final Optional<EntityTag> entityTag;
    private final Optional<HttpDate> expires;
    private final Optional<HttpDate> lastModified;
    private final Optional<Uri> location;
    private final Set<ChallengeMessage> proxyAuthenticationChallenges;
    private final Optional<HttpDate> retryAfterDate;
    private final Optional<UserAgent> server;
    private final Status status;
    private final Set<Header> vary;
    private final List<Warning> warning;   
    
    ResponseImpl(final ResponseBuilder builder){
        this.acceptedRangeUnits = builder.acceptedRangeUnits.build();
        this.age = builder.age;
        this.allowedMethods = builder.allowedMethods.build();
        this.authenticationChallenges = builder.authenticationChallenges.build();
        this.cacheDirectives = builder.cacheDirectives.build();
        this.connectionInfo = builder.connectionInfo;
        this.contentInfo = builder.contentInfo;
        this.date = builder.date;
        this.entity = builder.entity;
        this.entityTag = builder.entityTag;
        this.expires = builder.expires;
        this.customHeaders = builder.customHeaders.build();
        this.lastModified = builder.lastModified;
        this.location = builder.location;
        this.proxyAuthenticationChallenges = builder.proxyAuthenticationChallenges.build();
        this.retryAfterDate = builder.retryAfterDate;
        this.server = builder.server;
        this.status = builder.status;
        this.vary = builder.vary.build();
        this.warning = builder.warning.build();
    }

    @Override
    public Set<RangeUnit> acceptedRangeUnits() {
        return this.acceptedRangeUnits;
    }

    @Override
    public Optional<Long> age() {
        return this.age;
    }

    @Override
    public Set<Method> allowedMethods() {
        return this.allowedMethods;
    }

    @Override
    public Set<ChallengeMessage> authenticationChallenges() {
        return this.authenticationChallenges;
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
        return this.customHeaders;
    }

    @Override
    public Optional<HttpDate> date() {
        return this.date;
    }

    @Override
    public Optional<Object> entity() {
        return this.entity;
    }

    @Override
    public Optional<EntityTag> entityTag() {
        return this.entityTag;
    }
    
    @Override
    public Optional<HttpDate> expires() {
        return this.expires;
    }

    @Override
    public Optional<HttpDate> lastModified() {
        return this.lastModified;
    }

    @Override
    public Optional<Uri> location() {
        return this.location;
    }

    @Override
    public Set<ChallengeMessage> proxyAuthenticationChallenge() {
        return this.proxyAuthenticationChallenges;
    }

    @Override
    public Optional<HttpDate> retryAfterDate() {
        return this.retryAfterDate;
    }

    @Override
    public Optional<UserAgent> server() {
        return this.server;
    }

    @Override
    public Status status() {
        return this.status;
    }

    @Override
    public Set<Header> vary() {
        return this.vary;
    }

    @Override
    public List<Warning> warnings() {
        return this.warning;
    }
}
