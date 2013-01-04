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


package restlib.example.echo;

import restlib.Request;
import restlib.Response;
import restlib.data.HttpDate;
import restlib.data.Method;
import restlib.data.Status;
import restlib.server.FutureResponses;
import restlib.server.Resource;
import restlib.server.Route;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public final class EchoResource implements Resource {
    private final Route route;
    
    public static EchoResource newInstance(final Route route) {
        Preconditions.checkNotNull(route);
        return new EchoResource (route);
    }

    private EchoResource(final Route route) {
        this.route = route;   
    }

    public Route route() {
        return this.route;
    }

    public ListenableFuture<Response> handle(final Request request) {
        Preconditions.checkNotNull(request);
        
        if (request.method().equals(Method.POST) || request.method().equals(Method.PUT)) {
            return FutureResponses.INFORMATIONAL_CONTINUE;
        } else {
            return Futures.immediateFuture(
                    Response.builder()
                    .setDate(HttpDate.create(System.currentTimeMillis()))
                    .setEntity(request.toString())
                    .setStatus(Status.SUCCESS_OK)
                    .setLocation(request.uri())
                    .build());
        }
    }

    public ListenableFuture<Response> acceptMessage(final Request request, final Object message) {
        Preconditions.checkNotNull(request);
        Preconditions.checkNotNull(message);
        final String response =
                new StringBuilder()
                        .append(request)
                        .append(message.toString())
                        .toString();
        
        return Futures.immediateFuture(
                    Response.builder()
                        .setDate(HttpDate.create(System.currentTimeMillis()))
                        .setEntity(response) 
                        .setStatus(Status.SUCCESS_OK)
                        .setLocation(request.uri())
                        .build());
    }
}
