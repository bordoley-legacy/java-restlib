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

import restlib.data.EntityTag;
import restlib.data.HttpDate;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

/**
 * A builder for generating {@code RequestPrecondition} instances. 
 * RequestBuilder instances can be reused; it is safe to call build() 
 * multiple times to build multiple {@code RequestPreconditions} instances.
 */
@NotThreadSafe
public final class RequestPreconditionsBuilder {
    final ImmutableSet.Builder<EntityTag> ifMatchTags = ImmutableSet.<EntityTag> builder();
    Optional<HttpDate> ifModifiedSinceDate = Optional.absent();
    final ImmutableSet.Builder<EntityTag> ifNoneMatchTags = ImmutableSet.<EntityTag> builder();
    Optional<Object> ifRange = Optional.absent();
    Optional<HttpDate> ifUnmodifiedSinceDate = Optional.absent();

    RequestPreconditionsBuilder() {}

    /**
     * Adds {@code entityTag} to this builder's ifMatchTag set
     * @param entityTag a non-null entityTag.
     * @return this {@code RequestPreconditionsBuilder} instance.
     * @throws NullPointerException if {@code entityTag} is null.
     */
    public RequestPreconditionsBuilder addIfMatchTag(final EntityTag entityTag) {
        this.ifMatchTags.add(entityTag);
        return this;
    }

    /**
     * Adds each {@code EntityTag} to this builder's ifMatchTag set.
     * @param entityTag the EntityTags to add.
     * @return this {@code RequestPreconditionsBuilder} instance.
     * @throws NullPointerException if {@code entityTag} is null or contains a null element.
     */
    public RequestPreconditionsBuilder addIfMatchTags(final Iterable<EntityTag> entityTag) {
        this.ifMatchTags.addAll(entityTag);
        return this;
    }

    /**
     * Adds {@code entityTag} to this builder's ifNoneMatchTag set
     * @param entityTag a non-null entityTag.
     * @return this {@code RequestPreconditionsBuilder} instance.
     * @throws NullPointerException if {@code entityTag} is null.
     */
    public RequestPreconditionsBuilder addIfNoneMatchTag(final EntityTag entityTag) {
        this.ifNoneMatchTags.add(entityTag);
        return this;
    }

    /**
     * Adds each {@code EntityTag} to this builder's ifNoneMatchTag set.
     * @param entityTag the EntityTag to add.
     * @return this {@code RequestPreconditionsBuilder} instance.
     * @throws NullPointerException if {@code entityTag} is null or contains a null element.
     */
    public RequestPreconditionsBuilder addIfNoneMatchTags(final Iterable<EntityTag> entityTag) {
        this.ifNoneMatchTags.addAll(entityTag);
        return this;
    }

    /**
     * Returns a newly-created {@code RequestPreconditions} instance based 
     * on the contents of the RequestPreconditionsBuilder.
     */
    public RequestPreconditions build() {
        return new RequestPreconditionsImpl(this);
    }

    /**
     * Sets the if modified since date of this builder.
     * @param ifModifiedSinceDate a non-null {@code DateTime}.
     * @return this {@code RequestPreconditionsBuilder} instance.
     * @throws NullPointerException if {@code ifModifiedSinceDate} is null.
     */
    public RequestPreconditionsBuilder setIfModifiedSinceDate(final HttpDate ifModifiedSinceDate) {
        Preconditions.checkNotNull(ifModifiedSinceDate);
        this.ifModifiedSinceDate = Optional.of(ifModifiedSinceDate);
        return this;
    }
    
    /**
     * Sets the if range tag of this builder.
     * @param ifRangeTag a non-null entityTag.
     * @return this {@code RequestPreconditionsBuilder} instance.
     * @throws NullPointerException if {@code ifRangeTag} is null.
     */
    public RequestPreconditionsBuilder setIfRange(final EntityTag ifRangeTag) {
        Preconditions.checkNotNull(ifRangeTag);
        this.ifRange = Optional.<Object> of(ifRangeTag);
        return this;
    }

    /**
     * Sets the if range date of this builder.
     * @param ifRangeDate a non-null {@code DateTime}.
     * @return this {@code RequestPreconditionsBuilder} instance.
     * @throws NullPointerException if {@code ifRangeDate} is null.
     */
    public RequestPreconditionsBuilder setIfRange(final HttpDate ifRangeDate) {
        Preconditions.checkNotNull(ifRangeDate);
        this.ifRange = Optional.<Object> of(ifRangeDate);
        return this;
    }

    RequestPreconditionsBuilder setIfRange(final Object ifRange) {
        Preconditions.checkNotNull(ifRange);
        if (ifRange instanceof HttpDate) {
            return setIfRange((HttpDate) ifRange);
        } else if (ifRange instanceof EntityTag) {
            return setIfRange((EntityTag) ifRange);
        } else {
            throw new IllegalArgumentException("ifRange date must be and instance of HttpDate or EntityTag.");
        }
    }

    /**
     * Sets the if unmodified since date of this builder.
     * @param ifUnmodifiedSinceDate a non-null {@code DateTime}.
     * @return this {@code RequestPreconditionsBuilder} instance.
     * @throws NullPointerException if {@code ifUnmodifiedSinceDate} is null.
     */
    public RequestPreconditionsBuilder setIfUnmodifiedSinceDate(final HttpDate ifUnmodifiedSinceDate) {
        Preconditions.checkNotNull(ifUnmodifiedSinceDate);
        this.ifUnmodifiedSinceDate = Optional.of(ifUnmodifiedSinceDate);
        return this;
    }
}