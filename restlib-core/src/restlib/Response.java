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

import static restlib.MessageHelpers.appendHeader;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.data.CacheDirective;
import restlib.data.ChallengeMessage;
import restlib.data.EntityTag;
import restlib.data.Header;
import restlib.data.HttpDate;
import restlib.data.HttpHeaders;
import restlib.data.Method;
import restlib.data.Protocol;
import restlib.data.RangeUnit;
import restlib.data.Status;
import restlib.data.UserAgent;
import restlib.data.Warning;
import restlib.net.Uri;

import com.google.common.base.Optional;
import com.google.common.collect.ListMultimap;

/**
 * Object representation of an HTTP Response. 
 * Implementations must be immutable or effectively immutable.
 */
@Immutable
public abstract class Response {            
    /**
     * Returns a new ResponseBuilder instance.
     */
    public static ResponseBuilder builder() {
        return new ResponseBuilder();
    }
    
    Response(){}
    
    /**
     * Returns the range units accepted by the server.
     */
    public abstract Set<RangeUnit> acceptedRangeUnits();
    
    /**
     * Returns an estimate of the amount of time since the response was 
     * generated or successfully validated at the origin server, if available.
     */
    public abstract Optional<Long> age();
    
    /**
     * Returns the set of methods supported by the target resource.
     */
    public abstract Set<Method> allowedMethods();
    
    /**
     * Returns the authentication challenges.
     */
    public abstract Set<ChallengeMessage> authenticationChallenges();
    
    /** 
     * Returns the set of CacheDirectives in the Response.
     */
    public abstract Set<CacheDirective> cacheDirectives();
    
    /**
     * Returns the connectionInfo associated with this response.
     */
    public abstract ConnectionInfo connectionInfo();
    
    /**
     * Returns the contentInfo associated with this response.
     */
    public abstract ContentInfo contentInfo();
    
    /**
     * Returns the a multimap of non-standard headers in the response.
     */
    public abstract ListMultimap<Header, String> customHeaders();
    
    /**
     * Returns the date this response was generated on.
     */
    public abstract Optional<HttpDate> date();
    
    /**
     * Returns the response entity if available.
     */
    public abstract Optional<Object> entity();
    
    /**
     * Returns the entity tag of the response entity.
     */
    public abstract Optional<EntityTag> entityTag();

    @Override
    public final boolean equals(@Nullable final Object obj) {
        return super.equals(obj);
    }

    /**
     * Returns the date this response expires.
     */
    public abstract Optional<HttpDate> expires();
    
    @Override
    public final int hashCode() {
        return super.hashCode();
    }
    
    /**
     * Returns the date the entity of this response was last modified.
     */
    public abstract Optional<HttpDate> lastModified();
    
    /**
     * Returns the location URI of the response.
     */
    public abstract Optional<Uri> location();
    
    /**
     * Returns the proxy authentication challenges.
     */
    public abstract Set<ChallengeMessage> proxyAuthenticationChallenge();
    
    /**
     * Returns the date after which a client should retry the request that generated this response.
     */
    public abstract Optional<HttpDate> retryAfterDate();
    
    /** 
     * Returns the UserAgent of the server that generated the response.
     */
    public abstract Optional<UserAgent> server();
    
    /**
     * Returns the response status.
     */
    public abstract Status status();
    
    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(Protocol.HTTP_1_1).append(" ")
            .append(this.status().code()).append(" ")
            .append(this.status().reason()).append("\r\n");
        
        appendHeader(builder, HttpHeaders.ACCEPT_RANGES, this.acceptedRangeUnits());
        appendHeader(builder, HttpHeaders.AGE, this.age());
        appendHeader(builder, HttpHeaders.ALLOW, this.allowedMethods());
        appendHeader(builder, HttpHeaders.WWW_AUTHENTICATE, this.authenticationChallenges());
        appendHeader(builder, HttpHeaders.CACHE_CONTROL, this.cacheDirectives());
        builder.append(this.connectionInfo());
        builder.append(this.contentInfo());
        
        for (final Entry<Header, String> header : this.customHeaders().entries()) {
            appendHeader(builder, header.getKey(), header.getValue());
        }

        appendHeader(builder, HttpHeaders.DATE, this.date());
        appendHeader(builder, HttpHeaders.ENTITY_TAG, this.entityTag());
        appendHeader(builder, HttpHeaders.EXPIRES, this.expires());
        appendHeader(builder, HttpHeaders.LAST_MODIFIED, this.lastModified());
          
        appendHeader(builder, HttpHeaders.LOCATION, this.location());
        appendHeader(builder, HttpHeaders.PROXY_AUTHENTICATE, this.proxyAuthenticationChallenge());
        appendHeader(builder, HttpHeaders.RETRY_AFTER, this.retryAfterDate());
        appendHeader(builder, HttpHeaders.SERVER, this.server());
        appendHeader(builder, HttpHeaders.VARY, this.vary());
        appendHeader(builder, HttpHeaders.WARNING, this.warnings());

        return builder.toString();
    }
    
    /**
     * Returns the set of headers used by the server to change the entity representation.
     */
    public abstract Set<Header> vary();

    /**
     * Returns the list of additional warning messages.
     */
    public abstract List<Warning> warnings();
}
