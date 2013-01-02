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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import restlib.Request;
import restlib.Response;
import restlib.ResponseWrapper;
import restlib.data.HttpHeaders;
import restlib.data.MediaRanges;
import restlib.data.Method;
import restlib.data.Preference;
import restlib.net.Uri;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFuture;

public final class ApplicationBuilderTest {  
    @Test(expected = NullPointerException.class)
    public void addRequestFilter_withNull() {
        final Function<Request, Request> requestFilter = null;
        ApplicationBuilder.newInstance().addRequestFilter(requestFilter);
    }
    
    @Test
    public void addRequestFilterTest() {
        final Function<Request, Request> filter1 = 
                RequestFilters.queryFilter(ImmutableList.of(HttpHeaders.AUTHORIZATION));
        final Function<Request, Request> filter2 =
                ExtensionFilter.getDefaultInstance();
        
        final Application application = 
                ApplicationBuilder.newInstance()
                    .addRequestFilter(filter1)
                    .addRequestFilter(filter2)
                    .build();
        
        final Request request = 
                Request.builder()
                    .setUri(Uri.parse(
                            "http://www.example.com/test.json?Authorization=BASIC%20ABCD"))
                    .build();
        final Request filteredRequest = application.requestFilter().apply(request);
        
        assertEquals("BASIC ABCD", filteredRequest.authorizationCredentials().toString());
        assertTrue(
                 filteredRequest.preferences().acceptedMediaRanges().contains(
                         Preference.create(MediaRanges.APPLICATION_JSON,1)));
        
        
    }
    
    
    @Test(expected = NullPointerException.class)
    public void addResource_withNull() {
        ApplicationBuilder.newInstance().addResource(null);
    }
    
    @Test
    public void addResourceTest() {
        final Resource resource1 = new Resource() {
            private final Route route = Route.parse("/a/:a");
            
            @Override
            public ListenableFuture<Response> acceptMessage(final Request request, final Object message) {
                return FutureResponses.SUCCESS_OK;
            }

            @Override
            public Route route() {
                return route;
            }

            @Override
            public ListenableFuture<Response> handle(Request request) {
                return FutureResponses.SUCCESS_OK;
            }     
        };
        
        final Resource resource2 = new Resource() {
            private final Route route = Route.parse("/b/:a/*c");
            
            @Override
            public ListenableFuture<Response> acceptMessage(final Request request, final Object message) {
                return FutureResponses.SUCCESS_OK;
            }

            @Override
            public Route route() {
                return route;
            }

            @Override
            public ListenableFuture<Response> handle(Request request) {
                return FutureResponses.SUCCESS_OK;
            }     
        };
        
        final Application application = 
                ApplicationBuilder.newInstance()
                    .addResource(resource1)
                    .addResource(resource2)
                    .build();
        
        final Request request1 = 
                Request.builder()
                    .setUri(Uri.parse("http://example.com/a/abcd"))
                    .build();
        
        final Request request2 = 
                Request.builder()
                    .setUri(Uri.parse("http://example.com/b/abcd/j/k/"))
                    .build();
        
        assertEquals(resource1, application.getResource(request1));
        assertEquals(resource2, application.getResource(request2));
    }
    
    
    @Test(expected = NullPointerException.class)
    public void addResponseFilter_withNull() {
        final Function<Response, Response> responseFilter = null;
        ApplicationBuilder.newInstance().addResponseFilter(responseFilter);
    }
    
    @Test
    public void addResponseFilterTest() {
        final Function<Response,Response> filter1 = new Function<Response,Response>() {
            @Override
            public Response apply(final Response response) {
                return new ResponseWrapper(response) {                 
                    @Override
                    public Optional<Uri> location() {
                        return Optional.of(Uri.parse("https://www.example.com/"));
                    }            
                };
            }  
        };
        
        final Function<Response,Response> filter2 = new Function<Response,Response>() {
            @Override
            public Response apply(final Response response) {
                return new ResponseWrapper(response) {                 
                    @Override
                    public Set<Method> allowedMethods() {
                        return ImmutableSet.of(Method.GET, Method.POST);
                    }              
                };
            }  
        };
        
        final Application application =
                ApplicationBuilder.newInstance()
                    .addResponseFilter(filter1)
                    .addResponseFilter(filter2)
                    .build();
        
        final Response response = 
                Response.builder()
                    .setLocation(Uri.parse("http://www.example.com"))
                    .addAllowedMethods(ImmutableList.of(Method.GET, Method.HEAD))
                    .build();
        
        final Response filteredResponse = application.responseFilter().apply(response);
        assertEquals(Uri.parse("https://www.example.com/"), filteredResponse.location());
        assertEquals(ImmutableSet.of(Method.GET, Method.POST), filteredResponse.allowedMethods());
    }
    
    @Test(expected = NullPointerException.class)
    public void setErrorResource_withNull(){
        ApplicationBuilder.newInstance().setErrorResource(null);
    }
    
    @Test
    public void setErrorResourceTest() {
        final Resource resource = new Resource() {
            @Override
            public ListenableFuture<Response> acceptMessage(Request request, Object message) {
                return FutureResponses.SUCCESS_OK;
            }

            @Override
            public Route route() {
                return Route.NONE;
            }

            @Override
            public ListenableFuture<Response> handle(Request request) {
                return FutureResponses.SUCCESS_OK;
            }         
        };
        
        assertEquals(resource, 
                ApplicationBuilder.newInstance()
                    .setErrorResource(resource)
                    .build().getResource(Request.builder().build()));
    }
}
