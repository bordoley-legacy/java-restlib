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
import restlib.Response;
import restlib.ResponseWrapper;
import restlib.data.Header;
import restlib.data.HttpHeaders;
import restlib.data.MediaRange;
import restlib.data.Method;
import restlib.data.Preference;
import restlib.data.Status;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

// FIXME: Support charset, language, etc. conneg.
public abstract class ConnegResourceDecorator extends ResourceWrapper implements ConnegResource {
    private static final class ConnegResponse extends ResponseWrapper {  
        private final Set<Header> vary = 
                ImmutableSet.<Header> builder()
                    .addAll(super.vary())
                    .add(HttpHeaders.ACCEPT)
                    .build();
        
        private ConnegResponse(final Response delegate) {
            super(delegate);
        }
        
        @Override 
        public Set<Header> vary() {
            return this.vary;
        }
    }

    protected ConnegResourceDecorator(final Resource delegate) {
        super(delegate);
    }

    public abstract Iterable<MediaRange> acceptedMediaRanges();
    
    @Override
    public final ListenableFuture<Response> acceptMessage(final Request request, final Object message) {
        return Futures.transform(
                super.acceptMessage(request, message), 
                new Function<Response, Response>() {
                    @Override
                    public Response apply(final Response response) {
                        return new ConnegResponse(response);
                    }
                });
    }

    @Override
    public final ListenableFuture<Response> handle(final Request request) {
        return Futures.transform(
                super.handle(request), 
                new Function<Response, Response>() {
                    @Override
                    public Response apply(final Response response) {
                        final Status status = response.status();
                        
                        if(!(status.statusClass().equals(Status.Class.SUCCESS) || 
                                status.statusClass().equals(Status.Class.INFORMATIONAL))) {
                            return response;
                        } 
                        
                        // Determine if the resource supports the request entity
                        else if (
                                (request.method().equals(Method.POST) || 
                                        request.method().equals(Method.PUT) ||
                                        request.method().equals(Method.PATCH)) &&
                                !messageSupported(request)) {
                            
                            return Response.builder()
                                    .setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE)
                                    .addVaryHeader(HttpHeaders.ACCEPT)
                                    .build();
                        } 
                      
                        // Determine if the resource supports a response content type supported
                        // by the client
                        else if (!responseContentTypeSupported(request)) {
                            return Response.builder()
                                    .setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE)
                                    .addVaryHeader(HttpHeaders.ACCEPT)
                                    .build();
                        } else if (status.statusClass().equals(Status.Class.INFORMATIONAL)){
                            return response;
                        } else {
                            return new ConnegResponse(response);  
                        }
                    }             
                }); 
    }

    private boolean messageSupported(final Request request) {
        final Optional<MediaRange> contentMediaRange = request.contentInfo().mediaRange();
        
        if (contentMediaRange.isPresent()) {    
            // Find accepted MediaRange that has any level of match with 
            // the content MediaRange.
            for (final MediaRange mediaRange : this.acceptedMediaRanges()) {
                if (mediaRange.match(contentMediaRange.get()) > 0) {
                    return true;
                }
            }
        }
        
        return false;
    }

    private boolean responseContentTypeSupported(
            final Request request) {
        final Optional<MediaRange> mediaRange =
                Preference.<MediaRange> bestMatch(
                        request.preferences().acceptedMediaRanges(),
                        this.supportedMediaRanges());

        return mediaRange.isPresent(); 
    }

    public abstract Iterable<MediaRange> supportedMediaRanges();
}
