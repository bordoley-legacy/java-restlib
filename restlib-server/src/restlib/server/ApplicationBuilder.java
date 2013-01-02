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


package restlib.server;

import restlib.Request;
import restlib.Response;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public final class ApplicationBuilder {    
    public static ApplicationBuilder newInstance() {
        return new ApplicationBuilder();
    }
    
    final ImmutableList.Builder<Function<Request,Request>> requestFiltersBuilder = 
        ImmutableList.builder();
    final ImmutableList.Builder<Function<Response, Response>> responseFiltersBuilder = 
        ImmutableList.builder(); 
    final ImmutableSet.Builder<Resource> resources = ImmutableSet.builder();
    Resource errorResource = Resources.NOT_FOUND;
    
    private ApplicationBuilder() {
    }
    
    public ApplicationBuilder addRequestFilter(final Function<Request,Request> requestFilter) {
        Preconditions.checkNotNull(requestFilter);
        requestFiltersBuilder.add(requestFilter);
        return this;
    }
    
    public ApplicationBuilder addResource(final Resource resource){
        // FIXME: Should keep an internal set of resource routes and guarantee that
        // the same route isn't added twice. 
        Preconditions.checkNotNull(resource);
        this.resources.add(resource);
        return this;
    }
    
    public ApplicationBuilder addResponseFilter(final Function<Response,Response> responseFilter) {
        Preconditions.checkNotNull(responseFilter);
        responseFiltersBuilder.add(responseFilter);
        return this;
    }
    
    public Application build() {
        return new ApplicationImpl(this);
    } 
    
    public ApplicationBuilder setErrorResource(final Resource resource) {
        Preconditions.checkNotNull(resource);
        this.errorResource = resource;
        return this;
    }
}
