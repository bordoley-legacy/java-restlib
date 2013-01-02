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

import javax.annotation.concurrent.NotThreadSafe;

import restlib.data.CacheDirective;
import restlib.data.ChallengeMessage;
import restlib.data.EntityTag;
import restlib.data.Header;
import restlib.data.HttpDate;
import restlib.data.HttpHeaders;
import restlib.data.Method;
import restlib.data.RangeUnit;
import restlib.data.Status;
import restlib.data.UserAgent;
import restlib.data.Warning;
import restlib.net.Uri;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * A builder for generating {@code Response} instances. 
 * ResponseBuilder instances can be reused; it is safe to call build() 
 * multiple times to build multiple {@code Response} instances.
 */
@NotThreadSafe
public final class ResponseBuilder {    
    final ImmutableSet.Builder<RangeUnit> acceptedRangeUnits = ImmutableSet.builder();
    Optional<Long> age = Optional.absent();
    final ImmutableSet.Builder<Method> allowedMethods = ImmutableSet.builder();
    final ImmutableSet.Builder<ChallengeMessage> authenticationChallenges = ImmutableSet.builder();
    final ImmutableSet.Builder<CacheDirective> cacheDirectives = ImmutableSet.builder();
    ConnectionInfo connectionInfo = ConnectionInfo.NONE;
    ContentInfo contentInfo = ContentInfo.NONE;
    final ImmutableListMultimap.Builder<Header, String> customHeaders = ImmutableListMultimap.builder();
    Optional<HttpDate> date = Optional.absent();
    Optional<Object> entity = Optional.absent();
    Optional<EntityTag> entityTag = Optional.absent();
    Optional<HttpDate> expires = Optional.absent();
    Optional<HttpDate> lastModified = Optional.absent();
    Optional<Uri> location = Optional.absent();
    final ImmutableSet.Builder<ChallengeMessage> proxyAuthenticationChallenges = ImmutableSet.builder();
    Optional<HttpDate> retryAfterDate = Optional.absent();
    Optional<UserAgent> server = Optional.absent();
    Status status = Status.CLIENT_ERROR_BAD_REQUEST;
    final ImmutableSet.Builder<Header> vary = ImmutableSet.builder();
    final ImmutableList.Builder<Warning> warning = ImmutableList.builder();
    
    ResponseBuilder(){}
    
    /**
     * Adds a range unit to this builder's accepted range units.
     * @param rangeUnit a non-null range unit.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code rangeUnit} is null.
     */
    public ResponseBuilder addAcceptedRangeUnit(final RangeUnit rangeUnit) {
        this.acceptedRangeUnits.add(rangeUnit);
        return this;
    }
    
    /**
     * Adds each range unit to this builder's accepted range units.
     * @param rangeUnits the range units to add.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code rangeUnits} is null or contains a null element.
     */
    public ResponseBuilder addAcceptedRangeUnits(final Iterable<RangeUnit> rangeUnits) {
        this.acceptedRangeUnits.addAll(rangeUnits);
        return this;
    }
    
    /**
     * Adds a method to this builder's allowed methods.
     * @param allowedMethod a non-null method.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code allowedMethod} is null.
     */
    public ResponseBuilder addAllowedMethod(final Method allowedMethod) {
        this.allowedMethods.add(allowedMethod);
        return this;
    }
    
    /**
     * Adds each method to this builder's allowed methods.
     * @param allowedMethods the methods to add.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code allowedMethods} is null or contains a null element.
     */
    public ResponseBuilder addAllowedMethods(
            final Iterable<Method> allowedMethods) {
        this.allowedMethods.addAll(allowedMethods);
        return this;
    }

    /**
     * Add an authentication challenge message to this builder's authentication challenges.
     * @param authChallenge a non-null ChallengeMessage.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code authChallenge} is null.
     */
    public ResponseBuilder addAuthenticationChallenge(
            final ChallengeMessage authChallenge) {
        this.authenticationChallenges.add(authChallenge);
        return this;
    }

    /**
     * Add each authentication challenge message to this builder's authentication challenges.
     * @param authChallenges the challenge messages to add.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code authChallenges} is null or contains a null element.
     */
    public ResponseBuilder addAuthenticationChallenges(
            final Iterable<ChallengeMessage> authChallenges) {
        this.authenticationChallenges.addAll(authChallenges);
        return this;
    }

    /**
     * Adds a {@code cacheDirective} to this builder's CacheDirective set.
     * @param cacheDirective the CacheDirective to add.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code cacheDirective} is null.
     */
    public ResponseBuilder addCacheDirective(final CacheDirective cacheDirective) {
        this.cacheDirectives.add(cacheDirective);
        return this;
    }

    /**
     * Adds each {@code CacheDirective} to this builder's CacheDirectives set.
     * @param cacheDirectives the CacheDirectives to add.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code cacheDirectives} is null or contains a null element.
     */
    public ResponseBuilder addCacheDirectives(final Iterable<CacheDirective> cacheDirectives) {
        this.cacheDirectives.addAll(cacheDirectives);
        return this;
    }
    
    /**
     * Adds a custom header value to this builder.
     * @param header a non-null non-standard header.
     * @param value a non-null valid header value.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if header or value are null;
     * @throws IllegalArgumentException if {@code header} is a standard header value
     * or if value is not valid header value. 
     */
    public ResponseBuilder addCustomHeader(final Header header, final String value) {
        Preconditions.checkNotNull(header);
        Preconditions.checkNotNull(value);
        Preconditions.checkArgument(!HttpHeaders.isStandardHeader(header));
        // FIXME: Validate value.toString() is a valid field-value
        this.customHeaders.put(header, value);
        return this;
    }
    
    /**
     * Add an authentication challenge message to this builder's proxy authentication challenges.
     * @param authChallenge a non-null ChallengeMessage.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code authChallenge} is null.
     */
    public ResponseBuilder addProxyAuthenticationChallenge(
            final ChallengeMessage authChallenge) {
        this.proxyAuthenticationChallenges.add(authChallenge);
        return this;
    }
    
    /**
     * Add each authentication challenge message to this builder's proxy authentication challenges.
     * @param authChallenges the challenge messages to add.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code authChallenges} is null or contains a null element. 
     */
    public ResponseBuilder addProxyAuthenticationChallenges(
            final Iterable<ChallengeMessage> authChallenges) {
        this.proxyAuthenticationChallenges.addAll(authChallenges);
        return this;
    }
    
    /**
     * Adds a header to this builder's vary headers.
     * @param header a non-null {@Header}
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code header} is null.
     */
    public ResponseBuilder addVaryHeader(final Header header) {
        this.vary.add(header);
        return this;
    }

    /**
     * Adds each header to this builder's vary headers.
     * @param headers the headers to add.
     * @return this {@code ResponseBuilder} instance.
     *  @throws NullPointerException if {@code headers} is null or contains a null element.
     */
    public ResponseBuilder addVaryHeaders(final Iterable<Header> headers) {
        this.vary.addAll(headers);
        return this;
    }
    
    /**
     * Add a warning to this builder's warning messages.
     * @param warning
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code warning} is null.
     */
    public ResponseBuilder addWarning(final Warning warning) {
        this.warning.add(warning);
        return this;
    }

    /**
     * Add each warning to this builder's warning messages.
     * @param warnings
     * @return this {@code ResponseBuilder} instance.
     *  @throws NullPointerException if {@code warnings} is null or contains a null element.
     */
    public ResponseBuilder addWarnings(final Iterable<Warning> warnings) {
        this.warning.addAll(warnings);
        return this;
    } 
    
    /** 
     * Returns a newly-created {@code Response} instance based 
     * on the contents of the ResponseBuilder.
     */
    public Response build() {
        return new ResponseImpl(this);
    }
    
    /**
     * Sets this builder's response age in milliseconds.
     * @param age
     * @return this {@code ResponseBuilder} instance.
     * @throws IllegalArgumentException if age is not a positive integer.
     */
    public ResponseBuilder setAge(final long age) {
        Preconditions.checkArgument(age > 0);
        this.age = Optional.of(age);
        return this;
    }
    
    /**
     * Sets this builder's connection info.
     * @param connectionInfo a non-null {@code ConnectionInfo} instance.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code connectionInfo} is null.
     */
    public ResponseBuilder setConnectionInfo(final ConnectionInfo connectionInfo) {
        Preconditions.checkNotNull(connectionInfo);
        this.connectionInfo = connectionInfo;
        return this;
    }
    
    /**
     * Sets this builder's content info.
     * @param contentInfo a non null {@code ContentInfo} instance.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code contentInfo} is null.
     */
    public ResponseBuilder setContentInfo(final ContentInfo contentInfo) {
        Preconditions.checkNotNull(contentInfo);
        this.contentInfo = contentInfo;
        return this;
    }
    
    /**
     * Sets the builder's response date.
     * @param date a non-null date.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code date} is null.
     */
    public ResponseBuilder setDate(final HttpDate date) {
        Preconditions.checkNotNull(date);
        this.date = Optional.of(date);
        return this;
    }
    
    /**
     * Sets this builder's entity {@code Object}.
     * @param entity a non-null {@code Object}.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code entity} is null.
     */
    public ResponseBuilder setEntity(final Object entity) {
        this.entity = Optional.of(entity);
        return this;
    }
    
    /**
     * Sets this builder's entity tag.
     * @param entityTag a non-null entity tag.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code entityTag} is null.
     */
    public ResponseBuilder setEntityTag(final EntityTag entityTag) {
        this.entityTag = Optional.of(entityTag);
        return this;
    }
    
    /**
     * Sets this builders expiration date.
     * @param expires a non-null date.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code expires} is null.
     */
    public ResponseBuilder setExpires(final HttpDate expires) {
        Preconditions.checkNotNull(expires);
        this.expires = Optional.of(expires);
        return this;
    }
    
    /**
     * Sets this builder's last modified date.
     * @param lastModified a non-null date.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code lastModified} is null.
     */
    public ResponseBuilder setLastModified(final HttpDate lastModified) {
        Preconditions.checkNotNull(lastModified);
        this.lastModified = Optional.of(lastModified);
        return this;
    }
    
    /**
     * Sets this builder's location.
     * @param location a non-null URI.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code location} is null.
     */
    public ResponseBuilder setLocation(final Uri location) {
        Preconditions.checkNotNull(location);
        if (!location.toString().isEmpty()) {
            this.location = Optional.of(location);
        }
        return this;
    }
    
    /**
     * Sets this builder's retry after date.
     * @param date a non-null http date.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code date} is null.
     */
    public ResponseBuilder setRetryAfterDate(final HttpDate date) {
        Preconditions.checkNotNull(date);
        this.retryAfterDate = Optional.of(date);
        return this;
    }
    
    /**
     * Sets this builder's server UserAgent.
     * @param server a non-null UserAgent.
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code server} is null.
     */
    public ResponseBuilder setServer(final UserAgent server) {
        this.server = Optional.of(server);
        return this;
    }

    /**
     * Sets this builder's status.
     * @param status a non-null {@code Status}
     * @return this {@code ResponseBuilder} instance.
     * @throws NullPointerException if {@code status} is null.
     */
    public ResponseBuilder setStatus(final Status status) {
        Preconditions.checkNotNull(status);
        this.status = status;
        return this;
    }
}