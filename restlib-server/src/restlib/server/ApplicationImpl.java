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

import java.util.List;

import restlib.Request;
import restlib.Response;

import com.google.common.base.Function;


final class ApplicationImpl implements Application{
    private final List<Function<Request,Request>> requestFilters;
    private final List<Function<Response,Response>> responseFilters;
    private final Router router;
    
    ApplicationImpl(final ApplicationBuilder builder) {
        this.router = Router.of(builder.resources.build(), builder.errorResource);
        this.requestFilters = builder.requestFiltersBuilder.build();
        this.responseFilters = builder.responseFiltersBuilder.build();
    }
    
    @Override
    public Function<Request, Request> requestFilter() {
        return new Function<Request, Request>() {
            @Override
            public Request apply(Request request) {
                for (final Function<Request,Request> requestFilter : requestFilters){
                    request = requestFilter.apply(request);
                }      
                return request;
            }
        };
    }
    
    @Override
    public Function<Response, Response> responseFilter() {
        return new Function<Response, Response>() {
            @Override
            public Response apply(Response response) {
                for (final Function<Response, Response> responseFilter : responseFilters){
                    response = responseFilter.apply(response);
                }
                return response;
            }
        };
    }
    
    @Override
    public Resource getResource(final Request request) {
        return this.router.getResource(request);
    }
}
