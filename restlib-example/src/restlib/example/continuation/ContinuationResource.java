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


package restlib.example.continuation;

import java.util.Timer;
import java.util.TimerTask;

import restlib.Request;
import restlib.Response;
import restlib.data.Form;
import restlib.data.Status;
import restlib.server.Continuation;
import restlib.server.Continuations;
import restlib.server.Resource;
import restlib.server.Route;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public final class ContinuationResource implements Resource {
    public static ContinuationResource newInstance(final Route route) {
        Preconditions.checkNotNull(route);
        return new ContinuationResource(route);
    }
    
    private final Route route;
    
    private ContinuationResource(final Route route) {
        this.route = route;
    }

    @Override
    public Response acceptMessage(final Request request, final Object message) {
        return Continuations.suspend(
                getContinuation(Status.SUCCESS_OK, 500));
    }
    
    private Continuation getContinuation(final Status status, final long waitTime) {
        return new Continuation() {
            final Timer timer = new Timer();
            
            public void cancel() {
                timer.cancel();
            }
            
            public void resume(final Function<Response, Void> responseHandler) {
                final TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        timer.cancel();
                        responseHandler.apply(status.toResponse());                     
                    }};               
                timer.schedule(task, waitTime);
            }
        };
    }

    @Override
    public Response handle(final Request request) {
        final Multimap<String, String> form;
        try {
            form = Form.parse(request.uri().query());
        } catch (final IllegalArgumentException e) {
            return Status.CLIENT_ERROR_BAD_REQUEST.toResponse();
        }
        
        final boolean timeout = 
                Boolean.parseBoolean(Iterables.getFirst(form.get("timeout"), ""));
        final long waitTime = timeout ? 20000 : 500;
        
         return Continuations.suspend(
                    getContinuation(Status.INFORMATIONAL_CONTINUE, waitTime));
    }

    @Override
    public Route route() {
        return this.route;
    }     
}
