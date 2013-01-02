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

import restlib.data.EntityTag;
import restlib.data.HttpDate;

import com.google.common.base.Optional;

final class RequestPreconditionsImpl extends RequestPreconditions {
    private final Set<EntityTag> ifMatchTags;
    private final Optional<HttpDate> ifModifiedSinceDate;
    private final Set<EntityTag> ifNoneMatchTags;
    private final Optional<Object> ifRange;
    private final Optional<HttpDate> ifUnmodifiedSinceDate;

    RequestPreconditionsImpl(final RequestPreconditionsBuilder builder) {
        this.ifMatchTags = builder.ifMatchTags.build();
        this.ifModifiedSinceDate = builder.ifModifiedSinceDate;
        this.ifNoneMatchTags = builder.ifNoneMatchTags.build();
        this.ifRange = builder.ifRange;
        this.ifUnmodifiedSinceDate = builder.ifUnmodifiedSinceDate;
    }

    @Override
    public Set<EntityTag> ifMatchTags() {
        return this.ifMatchTags;
    }

    @Override
    public Optional<HttpDate> ifModifiedSinceDate() {
        return this.ifModifiedSinceDate;
    }

    @Override
    public Set<EntityTag> ifNoneMatchTags() {
        return this.ifNoneMatchTags;
    }

    @Override
    public Optional<Object> ifRange() {
        return this.ifRange;
    }

    @Override
    public Optional<HttpDate> ifUnmodifiedSinceDate() {
        return this.ifUnmodifiedSinceDate;
    }
}
