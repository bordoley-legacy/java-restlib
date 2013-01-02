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


package restlib.server.bio;

import restlib.Request;
import restlib.Response;
import restlib.server.ApplicationBuilder;

import com.google.common.base.Function;

public final class BioApplicationBuilder {            
    final ApplicationBuilder applicationBuilder =
        ApplicationBuilder.newInstance();
    
    public static BioApplicationBuilder newInstance() {
        return new BioApplicationBuilder();
    }
    
    private BioApplicationBuilder() {
        this.applicationBuilder.setErrorResource(BioResources.NOT_FOUND);
    }

    public BioApplicationBuilder addRequestFilter(final Function<Request, Request> requestFilter) {
        this.applicationBuilder.addRequestFilter(requestFilter);
        return this;
    }
    
    public BioApplicationBuilder addResponseFilter(final Function<Response, Response> responseFilter) {
        this.applicationBuilder.addResponseFilter(responseFilter);
        return this;
    }

    public BioApplicationBuilder addResource(final BioResource<?> resource) {
        this.applicationBuilder.addResource(resource);
        return this;
    }

    public BioApplication build() {
        return new BioApplicationImpl(this);
    }
    
    public BioApplicationBuilder setErrorResource(final BioResource<?> resource) {
        this.applicationBuilder.setErrorResource(resource);
        return this;
    }
}
