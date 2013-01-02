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

import restlib.data.EntityTag;
import restlib.data.HttpDate;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * An implementation of RequestPreconditions which forwards 
 * all its method calls to another instance of RequestPreconditions. 
 * Subclasses should override one or more methods to modify the behavior of 
 * the backing RequestPreconditions as desired per the decorator pattern.
 */
@Immutable
public class RequestPreconditionsWrapper extends RequestPreconditions {
    private final RequestPreconditions delegate;
    
    /**
     * Constructs an instance of RequestPreconditionsWrapper that forwards all method calls to delegate.
     * @param delegate a non-null instance of RequestPreconditions.
     * @throws NullPointerException if delegate is null.
     */
    protected RequestPreconditionsWrapper(final RequestPreconditions delegate) {
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public Set<EntityTag> ifMatchTags() {
        return this.delegate.ifMatchTags();
    }

    @Override
    public Optional<HttpDate> ifModifiedSinceDate() {
        return this.delegate.ifModifiedSinceDate();
    }

    @Override
    public Set<EntityTag> ifNoneMatchTags() {
        return this.delegate.ifNoneMatchTags();
    }

    @Override
    public Optional<Object> ifRange() {
        return this.delegate.ifRange();
    }

    @Override
    public Optional<HttpDate> ifUnmodifiedSinceDate() {
        return this.delegate.ifUnmodifiedSinceDate();
    }
}
