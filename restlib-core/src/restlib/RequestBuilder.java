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
import restlib.data.Expectation;
import restlib.data.Header;
import restlib.data.HttpHeaders;
import restlib.data.Method;
import restlib.data.UserAgent;
import restlib.net.EmailAddress;
import restlib.net.Uri;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * A builder for generating {@code Request} instances. 
 * RequestBuilder instances can be reused; it is safe to call build() 
 * multiple times to build multiple {@code Request} instances.
 */
@NotThreadSafe
public final class RequestBuilder {    
    Optional<ChallengeMessage> authorizationCredentials = Optional.absent();
    final ImmutableSet.Builder<CacheDirective> cacheDirectives = 
            ImmutableSet.<CacheDirective> builder();
    ConnectionInfo connectionInfo = ConnectionInfo.NONE;
    ContentInfo contentInfo = ContentInfo.NONE;
    Optional<Object> entity = Optional.absent();
    final ImmutableSet.Builder<Expectation> expecations = 
            ImmutableSet.<Expectation> builder();
    Optional<EmailAddress> from = Optional.absent();
    final ImmutableListMultimap.Builder<Header, String> headerMap = ImmutableListMultimap.builder();
    Optional<Integer> maxForwards = Optional.absent();
    Method method = Method.GET;
    final ImmutableSet.Builder<CacheDirective> pragmaCacheDirectives = ImmutableSet.builder();
    RequestPreconditions preconditions = RequestPreconditions.NONE;
    ClientPreferences preferences = ClientPreferences.NONE;
    Optional<ChallengeMessage> proxyAuthorizationCredentials = Optional.absent();
    Optional<Uri> referrer = Optional.absent();
    Optional<Uri> uri = Optional.absent();
    Optional<UserAgent> userAgent = Optional.absent();
    
    RequestBuilder(){}
    
    /**
     * Adds a {@code cacheDirective} to this builder's CacheDirective set.
     * @param cacheDirective the CacheDirective to add.
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code cacheDirective} is null.
     */
    public RequestBuilder addCacheDirective(final CacheDirective cacheDirective) {
        this.cacheDirectives.add(cacheDirective);
        return this;
    }

    /**
     * Adds each {@code CacheDirective} to this builder's CacheDirectives set.
     * @param cacheDirectives the CacheDirectives to add.
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code cacheDirectives} is null or contains a null element.
     */
    public RequestBuilder addCacheDirectives(
            final Iterable<CacheDirective> cacheDirectives) {
        this.cacheDirectives.addAll(cacheDirectives);
        return this;
    }
    
    /**
     * Adds a custom header value to this builder.
     * @param header a non-null non-standard header.
     * @param value a non-null valid header value.
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if header or value are null;
     * @throws IllegalArgumentException if {@code header} is a standard header value
     * or if value is not valid header value. 
     */
    public RequestBuilder addCustomHeader(final Header header, final String value) {
        Preconditions.checkNotNull(header);
        Preconditions.checkNotNull(value);
        Preconditions.checkArgument(!HttpHeaders.isStandardHeader(header));
        // FIXME: Validate value is a valid field-value
        this.headerMap.put(header, value);
        return this;
    }
    
    /**
     * Adds a {@code expectation} to this builder's Expectation set.
     * @param expectation the Expectation to add.
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code expectation} is null.
     */
    public RequestBuilder addExpectation(final Expectation expectation) {
        this.expecations.add(expectation);
        return this;
    }
    
    /**
     * Adds each {@code Expectation} to this builder's Expectations set.
     * @param expectations the Expectations to add.
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code expectation} is null or contains a null element.
     */
    public RequestBuilder addExpectations(final Iterable<Expectation> expectations) {
        this.expecations.addAll(expectations);
        return this;
    }
    
    /**
     * Adds a {@code cacheDirective} to this builder's Pragma CacheDirective set.
     * @param cacheDirective the CacheDirective to add.
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code cacheDirective} is null.
     */
    public RequestBuilder addPragmaCacheDirective(final CacheDirective cacheDirective) {
        this.pragmaCacheDirectives.add(cacheDirective);
        return this;
    }
    
    /**
     * Adds each {@code CacheDirective} to this builder's Pragma CacheDirective set.
     * @param cacheDirectives the CacheDirectives to add.
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code cacheDirectives} is null or contains a null element.
     */
    public RequestBuilder addPragmaCacheDirectives(final Iterable<CacheDirective> cacheDirectives) {
        this.pragmaCacheDirectives.addAll(cacheDirectives);
        return this;
    }
    
    /** 
     * Returns a newly-created {@code Request} instance based 
     * on the contents of the RequestBuilder.
     */
    public Request build() {
        final Request retval = new RequestImpl(this);
        return retval;
    }
    
    /**
     * Sets this builder's authorization credentials.
     * @param credentials a non-null 
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code credentials} is null.
     */
    public RequestBuilder setAuthorizationCredentials(
            final ChallengeMessage credentials) {
        this.authorizationCredentials = Optional.of(credentials);
        return this;
    }
    
    /**
     * Sets this builder's connection info.
     * @param connectionInfo a non-null {@code ConnectionInfo} instance.
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code connectionInfo} is null.
     */
    public RequestBuilder setConnectionInfo(final ConnectionInfo connectionInfo) {
        Preconditions.checkNotNull(connectionInfo);
        this.connectionInfo = connectionInfo;
        return this;
    }
    
    /**
     * Sets this builder's content info.
     * @param contentInfo a non null {@code ContentInfo} instance.
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code contentInfo} is null.
     */
    public RequestBuilder setContentInfo(final ContentInfo contentInfo) {
        Preconditions.checkNotNull(contentInfo);
        this.contentInfo = contentInfo;
        return this;
    }
    
    /**
     * Sets this builder's entity {@code Object}.
     * @param entity a non-null {@code Object}.
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code entity} is null.
     */
    public RequestBuilder setEntity(final Object entity){
        this.entity = Optional.of(entity);
        return this;
    }
    
    /**
     * Sets this builder Request sender's email address.
     * @param from a non-null EmailAddress
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code from} is null.
     */
    public RequestBuilder setFrom(final EmailAddress from) {
        Preconditions.checkNotNull(from);
        this.from = Optional.of(from);
        return this;
    }
    
    /**
     * Sets the maximum number of hops a request produced by this builder can be forwarded.
     * @param maxForwards
     * @return this {@code RequestBuilder} instance.
     * @throws IllegalArgumentException if {@code maxForwards} is not a positive integer or 0.
     */
    public RequestBuilder setMaxForwards(final int maxForwards) {
        Preconditions.checkArgument(maxForwards >= 0);
        this.maxForwards = Optional.of(maxForwards);
        return this;
    }
    
    /**
     * Sets this builder's {@code Method}.
     * @param method a non-null Method.
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code method} is null.
     */
    public RequestBuilder setMethod(final Method method) {
        Preconditions.checkNotNull(method);
        this.method = method;
        return this;
    }
    
    /**
     * Sets this builder's {@code RequestPreconditions}.
     * @param preconditions a non-null RequestPreconditions.
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code preconditions} is null.
     */
    public RequestBuilder setPreconditions(final RequestPreconditions preconditions) {
        Preconditions.checkNotNull(preconditions);
        this.preconditions = preconditions;
        return this;
    }
    
    /**
     * Sets this builder's {@code ClientPreferences}.
     * @param preferences a non-null ClientPreferences.
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code preferences} is null.
     */
    public RequestBuilder setPreferences(final ClientPreferences preferences) {
        Preconditions.checkNotNull(preferences);
        this.preferences = preferences;
        return this;
    }
    
    /**
     * Sets this builder's proxy authorization credentials.
     * @param credentials a non-null 
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code credentials} is null.
     */
    public RequestBuilder setProxyAuthorizationCredentials(final ChallengeMessage credentials){
        this.proxyAuthorizationCredentials = Optional.of(credentials);
        return this;
    }
    
    /**
     * Sets this builder's referrer.
     * @param referrer a non-null uri.
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code referrer} is null.
     */
    public RequestBuilder setReferrer(final Uri referrer) {
        Preconditions.checkNotNull(referrer);
        if (!referrer.toString().isEmpty()) {
            this.referrer = Optional.of(referrer);
        }
        return this;
    }
    
    /**
     * Sets this builder's URI.
     * @param uri a non-null absolute URI.
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code uri} is null.
     * @throws IllegalArgumentException if {@code uri} is not absolute.
     */
    public RequestBuilder setUri(final Uri uri) {
        Preconditions.checkNotNull(uri);
        Preconditions.checkArgument(uri.isAbsolute());
        this.uri = Optional.of(uri);
        return this;
    }

    /**
     * Sets this builder's UserAgent.
     * @param userAgent a non-null {@code UserAgent}.
     * @return this {@code RequestBuilder} instance.
     * @throws NullPointerException if {@code userAgent} is null.
     */
    public RequestBuilder setUserAgent(final UserAgent userAgent) {
        this.userAgent = Optional.of(userAgent);
        return this;
    }
}