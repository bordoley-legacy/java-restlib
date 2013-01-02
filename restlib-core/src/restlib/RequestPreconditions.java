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

import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.data.EntityTag;
import restlib.data.HttpDate;
import restlib.data.HttpHeaders;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * Preconditions sent by the client that determine if a request should be processed or not.
 */
@Immutable
public abstract class RequestPreconditions {
    /**
     * RequestPreconditions null object instance.
     */
    static final RequestPreconditions NONE = RequestPreconditions.builder().build();

    /**
     * Returns a new RequestPreconditionsBuilder instance.
     */
    public static RequestPreconditionsBuilder builder() {
        return new RequestPreconditionsBuilder();
    }
    
    RequestPreconditions(){}
    
    @Override
    public final boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof RequestPreconditions) {
            final RequestPreconditions that = (RequestPreconditions) obj;
            return this.ifMatchTags().equals(that.ifMatchTags()) &&
                    this.ifModifiedSinceDate().equals(that.ifModifiedSinceDate()) && 
                    this.ifNoneMatchTags().equals(that.ifNoneMatchTags()) &&
                    this.ifRange().equals(that.ifRange()) && 
                    this.ifUnmodifiedSinceDate().equals(that.ifUnmodifiedSinceDate());
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(this.ifMatchTags(), this.ifModifiedSinceDate(), 
                this.ifNoneMatchTags(), this.ifRange(), this.ifUnmodifiedSinceDate());
    }

    /**
     * Returns the set of tags that must be matched before processing a request.
     */
    public abstract Set<EntityTag> ifMatchTags();

    /**
     * Returns the the date after which a server should process a request if the resource has been modified.
     */
    public abstract Optional<HttpDate> ifModifiedSinceDate();

    /**
     * Returns the set of tags that must not be matched before processing a request.
     */
    public abstract Set<EntityTag> ifNoneMatchTags();

    /**
     * Returns either the range condition as an {@code EntityTag} or a {@code DateTime} if available.
     */
    public abstract Optional<Object> ifRange();

    /**
     * Returns the the date after which a server should process a request if the resource has been unmodified.
     */
    public abstract Optional<HttpDate> ifUnmodifiedSinceDate();

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        appendHeader(builder, HttpHeaders.IF_MODIFIED_SINCE, this.ifModifiedSinceDate());
        appendHeader(builder, HttpHeaders.IF_UNMODIFIED_SINCE, this.ifUnmodifiedSinceDate());
        appendHeader(builder, HttpHeaders.IF_MATCH, this.ifMatchTags());
        appendHeader(builder, HttpHeaders.IF_NONE_MATCH, this.ifNoneMatchTags());
        appendHeader(builder, HttpHeaders.IF_RANGE, this.ifRange());       
        return builder.toString();
    }
}
