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

import java.util.Set;

import restlib.Request;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

final class Router {    
    public static Router of(final Iterable<Resource> resources, final Resource defaultResource) {
        Preconditions.checkNotNull(resources);
        Preconditions.checkNotNull(defaultResource);
     
        return new Router(resources, defaultResource);
    }
    
    private final Set<Resource> resources;
    private final Resource defaultResource;
    
    private Router(final Iterable<Resource> resources, final Resource defaultResource) {
        this.resources = ImmutableSet.copyOf(resources);
        this.defaultResource = defaultResource;
    } 

    public Resource getResource(final Request request) {
        Preconditions.checkNotNull(request);
        
        for (final Resource handler : this.resources) {
            if (handler.route().match(request.uri().path())) {
                return handler;
            }
        }
        
        return this.defaultResource;
    }
}
