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

/**
 * An HTTP REST application. 
 * 
 * Implementations must be threadsafe and preferably immutable.
 */
public interface Application {    
    /**
     * Applies all RequestFilters to the client's request prior to 
     * routing to a Resource for handling. This is the first step 
     * performed by a connector when processing a request.
     * @param request The client generated Request.
     * @return The filtered Request.
     */
    public Function<Request, Request> requestFilter();
    
    /**
     * Applies all ResponseFilters to a server's response. This is the 
     * final step performed by a connector prior to sending a response
     * to a client.
     * @param response
     * @return The filtered Response.
     */
    public Function<Response, Response> responseFilter();
    
    /**
     * Retrieve the first resource which has a Route that matches the 
     * Request's URI.
     * @param request A filtered client Request.
     * @return The Resource to be used to handle the Request.
     */
    public Resource getResource(Request request);
}
