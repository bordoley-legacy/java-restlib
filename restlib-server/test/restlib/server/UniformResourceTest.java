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
import static org.junit.Assert.fail;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import restlib.Request;
import restlib.RequestBuilder;
import restlib.RequestPreconditions;
import restlib.Response;
import restlib.data.EntityTag;
import restlib.data.HttpDate;
import restlib.data.Method;
import restlib.data.Status;
import restlib.net.Uri;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public final class UniformResourceTest {
    private static class MockUniformResource extends UniformResource<String> {        
        public MockUniformResource() {
            super(String.class);
        }
        
        @Override
        protected ListenableFuture<Response> get(Request request) {
            return FutureResponses.SUCCESS_OK;
        }

        @Override
        public Route route() {
            return Route.NONE;
        }          
    };
    
    @Test(expected = IllegalArgumentException.class)
    public void acceptMessage_withInvalidMessageClass() {
       new MockUniformResource().acceptMessage(
               Request.builder().setUri(Uri.parse("http://www.example.com")).build(), new Object());
    }
    
    @Test(expected = NullPointerException.class)
    public void acceptMessage_withNullObject() {
       new MockUniformResource().acceptMessage(
               Request.builder().setUri(Uri.parse("http://www.example.com")).build(), null);
    }
    
    @Test(expected = NullPointerException.class)
    public void acceptMessage_withNullRequest() {
       new MockUniformResource().acceptMessage(null, "");
    }
    
    @Test
    public void acceptMessage_withPatchPostPut_successOK() throws InterruptedException, ExecutionException { 
        final Resource resource = new MockUniformResource() {
            @Override
            protected ListenableFuture<Response> patch(Request request, String message) {
                return FutureResponses.SUCCESS_OK;
            } 
            
            @Override
            protected ListenableFuture<Response> post(Request request, String message) {
                return FutureResponses.SUCCESS_OK;
            } 
            
            @Override
            protected ListenableFuture<Response> put(Request request, String message) {
                return FutureResponses.SUCCESS_OK;
            } 
            
            @Override
            protected boolean requireETagForUpdate() {
                return false;
            } 
        };
        
        final Iterable<Method> methods = ImmutableList.of(Method.PATCH, Method.POST, Method.PUT);
        final RequestBuilder builder = Request.builder().setUri(Uri.parse("http://www.example.com"));
        
        for (final Method method : methods) {
            final Response response = resource.acceptMessage(
                    builder.setMethod(method).build(), "").get();
            assertEquals(Status.SUCCESS_OK, response.status());
        }
    }
    
    @Test
    public void acceptMessage_withUnsupportedMethods_illegalArgumentException() {
        final Iterable<Method> methods = 
                ImmutableList.of(Method.BASELINE_CONTROL, Method.BIND);
        
        final Resource resource = new MockUniformResource();
        
        for (final Method method : methods) {
            try {
                resource.acceptMessage(
                        Request.builder()
                            .setUri(Uri.parse("http://www.example.com"))
                            .setMethod(method).build(), ""); 
            } catch (final IllegalArgumentException e) {
                continue;
            }
            
            fail("Expected IllegalArgumentException");
        }
    }
    
    @Test
    public void handle_conditionalGet() throws InterruptedException, ExecutionException {
        final EntityTag tag = EntityTag.weakTag("abcd");
        
        final Resource resource = new MockUniformResource() {
            @Override
            protected ListenableFuture<Response> get(Request request) {
                return Futures.immediateFuture(
                            Response.builder()
                                .setStatus(Status.SUCCESS_OK)
                                .setEntityTag(tag)
                                .setLastModified(HttpDate.create(20))
                                .build());
            }         
        };
        
        final Request getRequestWithTag = 
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setMethod(Method.GET)
                    .setPreconditions(RequestPreconditions.builder().addIfNoneMatchTag(tag).build())
                    .build();
        assertEquals(Status.REDIRECTION_NOT_MODIFIED, resource.handle(getRequestWithTag).get().status());
        
        final Request getRequestWithoutTagOrDate = 
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setMethod(Method.GET)
                    .build();
        assertEquals(Status.SUCCESS_OK, resource.handle(getRequestWithoutTagOrDate).get().status());
        
        final Request getRequestWithLastModifiedGreaterThanModifiedDate = 
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setMethod(Method.GET)
                    .setPreconditions(RequestPreconditions.builder().setIfModifiedSinceDate(HttpDate.create(30)).build())
                    .build();
        assertEquals(Status.REDIRECTION_NOT_MODIFIED, resource.handle(getRequestWithLastModifiedGreaterThanModifiedDate).get().status());
        
        final Request getRequestWithLastModifiedLessModifiedDate = 
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setMethod(Method.GET)
                    .setPreconditions(RequestPreconditions.builder().setIfModifiedSinceDate(HttpDate.create(10)).build())
                    .build();
        assertEquals(Status.SUCCESS_OK, resource.handle(getRequestWithLastModifiedLessModifiedDate).get().status()); 
    }
    
    @Test
    public void handle_conditionalPutWithIfModifiedSince() throws InterruptedException, ExecutionException {
        final Resource resource = new MockUniformResource() {
            @Override
            protected ListenableFuture<Response> get(Request request) {
                return Futures.immediateFuture(
                        Response.builder()
                            .setStatus(Status.SUCCESS_OK)
                            .setLastModified(HttpDate.create(20))
                            .build());
            }

            @Override
            protected ListenableFuture<Response> patch(Request request, String message) {
                return FutureResponses.SUCCESS_OK;
            }

            @Override
            protected ListenableFuture<Response> put(Request request, String message) {
                return FutureResponses.SUCCESS_OK;
            }    
            
            @Override
            protected boolean requireETagForUpdate() {
                return false;
            } 
            
            @Override
            protected boolean requireIfUnmodifiedSinceForUpdate() {
                return true;
            } 
        };
        
        final Request putWithNoIfModifiedSinceDate =
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setMethod(Method.PUT)
                    .build();
        assertEquals(Status.CLIENT_ERROR_FORBIDDEN, resource.handle(putWithNoIfModifiedSinceDate).get().status());
        
        final Request putWithIfModifiedSinceGreaterThanLastModifiedDate = 
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setMethod(Method.PUT)
                    .setPreconditions(RequestPreconditions.builder().setIfUnmodifiedSinceDate(HttpDate.create(30)).build())
                    .build();
        assertEquals(Status.INFORMATIONAL_CONTINUE, resource.handle(putWithIfModifiedSinceGreaterThanLastModifiedDate).get().status());
        
        final Request putWithIfModifiedLessThanLastModifiedDate = 
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setMethod(Method.PUT)
                    .setPreconditions(RequestPreconditions.builder().setIfUnmodifiedSinceDate(HttpDate.create(10)).build())
                    .build();
        assertEquals(Status.CLIENT_ERROR_PRECONDITION_FAILED, resource.handle(putWithIfModifiedLessThanLastModifiedDate).get().status());   
    }
    
    @Test
    public void handle_conditionalPutWithTag() throws InterruptedException, ExecutionException {
        final EntityTag tag = EntityTag.weakTag("abcd");
        
        final Resource resource = new MockUniformResource() {
            @Override
            protected ListenableFuture<Response> get(Request request) {
                return Futures.immediateFuture(
                            Response.builder()
                                .setStatus(Status.SUCCESS_OK)
                                .setEntityTag(tag)
                                .build());
            }    
            
            @Override
            protected ListenableFuture<Response> patch(Request request, String message) {
                return FutureResponses.SUCCESS_OK;
            } 
            
            @Override
            protected ListenableFuture<Response> put(Request request, String message) {
                return FutureResponses.SUCCESS_OK;
            } 
        };
        
        final Request putRequestWithTag = 
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setMethod(Method.PUT)
                    .setPreconditions(RequestPreconditions.builder().addIfMatchTag(tag).build())
                    .build();
        assertEquals(Status.INFORMATIONAL_CONTINUE, resource.handle(putRequestWithTag).get().status());      
        
        final Request putRequestWithoutTag = 
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setMethod(Method.PUT)
                    .build();
        assertEquals(Status.CLIENT_ERROR_FORBIDDEN, resource.handle(putRequestWithoutTag).get().status());   
        
        final EntityTag expiredTag = EntityTag.weakTag("efgh");
        final Request putRequestWithExpiredTag = 
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setMethod(Method.PUT)
                    .setPreconditions(RequestPreconditions.builder().addIfMatchTag(expiredTag).build())
                    .build();
        assertEquals(Status.CLIENT_ERROR_PRECONDITION_FAILED, resource.handle(putRequestWithExpiredTag).get().status());      
        
    }
    
    @Test
    public void handle_resourceFound() throws InterruptedException, ExecutionException {
        final Resource resource = new MockUniformResource() {
            @Override
            protected ListenableFuture<Response> delete(Request request) {
                return FutureResponses.SUCCESS_OK;
            }          
            
            @Override
            protected ListenableFuture<Response> post(Request request, String message) {
                return FutureResponses.SUCCESS_OK;
            } 
        };
        
        final Request deleteRequest = Request.builder().setUri(Uri.parse("http://www.example.com")).setMethod(Method.DELETE).build();
        assertEquals(Status.SUCCESS_OK, resource.handle(deleteRequest).get().status());
        
        final Request getRequest = Request.builder().setUri(Uri.parse("http://www.example.com")).setMethod(Method.GET).build();
        assertEquals(Status.SUCCESS_OK, resource.handle(getRequest).get().status());
        
        final Request postRequest = Request.builder().setUri(Uri.parse("http://www.example.com")).setMethod(Method.POST).build();
        assertEquals(Status.INFORMATIONAL_CONTINUE, resource.handle(postRequest).get().status());         
    }
    
    @Test
    public void handle_resourceNotFound() throws InterruptedException, ExecutionException {
        final Resource resource = new MockUniformResource() {
            @Override
            protected ListenableFuture<Response> delete(Request request) {
                return FutureResponses.SUCCESS_OK;
            }          
            
            @Override
            protected ListenableFuture<Response> get(Request request) {
                return FutureResponses.CLIENT_ERROR_NOT_FOUND;
            }
            
            @Override
            protected ListenableFuture<Response> patch(Request request, String message) {
                return FutureResponses.SUCCESS_OK;
            } 
            
            @Override
            protected ListenableFuture<Response> post(Request request, String message) {
                return FutureResponses.SUCCESS_OK;
            } 
            
            @Override
            protected ListenableFuture<Response> put(Request request, String message) {
                return FutureResponses.SUCCESS_OK;
            } 
        };
        
        final Set<Method> expectedSupportedMethods = 
                ImmutableSet.of(Method.DELETE, Method.GET, Method.HEAD, Method.OPTIONS, Method.PATCH, Method.POST, Method.PUT);
        
        final Request deleteRequest = Request.builder().setUri(Uri.parse("http://www.example.com")).setMethod(Method.DELETE).build();
        assertEquals(Status.CLIENT_ERROR_NOT_FOUND, resource.handle(deleteRequest).get().status());
        
        final Request getRequest = Request.builder().setUri(Uri.parse("http://www.example.com")).setMethod(Method.GET).build();
        assertEquals(Status.CLIENT_ERROR_NOT_FOUND, resource.handle(getRequest).get().status());
        
        final Request optionsRequest = Request.builder().setUri(Uri.parse("http://www.example.com")).setMethod(Method.OPTIONS).build();
        final Response optionsResponse =  resource.handle(optionsRequest).get();
        assertEquals(Status.SUCCESS_OK, optionsResponse.status());
        assertEquals(expectedSupportedMethods, optionsResponse.allowedMethods());
        
        final Request patchRequest = Request.builder().setUri(Uri.parse("http://www.example.com")).setMethod(Method.POST).build();
        assertEquals(Status.CLIENT_ERROR_NOT_FOUND, resource.handle(patchRequest).get().status());              
        
        final Request postRequest = Request.builder().setUri(Uri.parse("http://www.example.com")).setMethod(Method.POST).build();
        assertEquals(Status.CLIENT_ERROR_NOT_FOUND, resource.handle(postRequest).get().status());              
        
        final Request putRequest = Request.builder().setUri(Uri.parse("http://www.example.com")).setMethod(Method.PUT).build();
        assertEquals(Status.CLIENT_ERROR_NOT_FOUND, resource.handle(putRequest).get().status());              
    }
    
    @Test(expected = NullPointerException.class)
    public void handle_withNull() {
       new MockUniformResource().handle(null);
    }
    
    @Test
    public void handle_withPutAndNoConditionsRequired_successOK() throws InterruptedException, ExecutionException {
        final Resource resource = new MockUniformResource() {
            @Override
            protected ListenableFuture<Response> put(Request request, String message) {
                return FutureResponses.SUCCESS_OK;
            }

            @Override
            protected boolean requireETagForUpdate() {
                return false;
            }
            
            @Override
            protected boolean requireIfUnmodifiedSinceForUpdate() {
                return false;
            } 
        };
        
        final Request request = Request.builder().setUri(Uri.parse("http://www.example.com")).setMethod(Method.PUT).build();
        assertEquals(Status.INFORMATIONAL_CONTINUE, resource.handle(request).get().status());
    }
    
    @Test
    public void handle_withUnsupportedMethods() throws InterruptedException, ExecutionException {
        final Iterable<Method> methods = 
                ImmutableList.of(Method.BASELINE_CONTROL, Method.BIND);
        
        final Set<Method> expectedSupportedMethods = 
                ImmutableSet.of(Method.GET, Method.HEAD, Method.OPTIONS);
        
        final Resource resource = new MockUniformResource();
        
        for (final Method method : methods) {
            final Response response = 
                    resource.handle(
                            Request.builder()
                                .setUri(Uri.parse("http://www.example.com"))
                                .setMethod(method).build()).get();
            assertEquals(method.toString(), 
                    Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, response.status());            
            assertEquals(expectedSupportedMethods, response.allowedMethods());
        }
    }
}

