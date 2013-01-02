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

import java.util.List;

import org.junit.Test;

import restlib.ClientPreferences;
import restlib.ContentInfo;
import restlib.Request;
import restlib.Response;
import restlib.data.HttpHeaders;
import restlib.data.MediaRange;
import restlib.data.MediaRanges;
import restlib.data.Method;
import restlib.data.Preference;
import restlib.data.Status;
import restlib.net.Uri;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public final class ConnegResourceDecoratorTest {   
    private static class MockSuccessResource implements Resource {
        public Response acceptMessage(Request request, Object message) {
            return Status.SUCCESS_OK.toResponse();
        }

        public Route route() {
            return Route.NONE;
        }

        public Response handle(Request request) {
            if (request.method().equals(Method.POST) ||
                    request.method().equals(Method.PUT)) { 
                return Status.INFORMATIONAL_CONTINUE.toResponse();
            } else {
                return Status.SUCCESS_OK.toResponse();
            }
        }   
    }

    private static class MockConnegResource extends ConnegResourceDecorator {
        private static final Resource resource = new MockSuccessResource();
        
        private static final List<MediaRange> accepted =
                ImmutableList.of(
                        MediaRanges.APPLICATION_ATOM);
        
        private static final List<MediaRange> supported =
                ImmutableList.of(
                        MediaRanges.APPLICATION_ATOM, 
                        MediaRanges.APPLICATION_JSON);
        
        protected MockConnegResource() {
            super(resource);
        }       

        @Override
        public Iterable<MediaRange> acceptedMediaRanges() {
            return accepted;
        }

        @Override
        public Iterable<MediaRange> supportedMediaRanges() {
            return supported;
        }
    }
    
    private static boolean isValidConnegResponse(final Response response) {
        return Iterables.contains(response.vary(), HttpHeaders.ACCEPT);
    }
    
    private static final ConnegResourceDecorator RESOURCE = new MockConnegResource();    
    
    @Test 
    public void acceptMessage_putRequest_successOK() {
        final Request request =
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setMethod(Method.PUT)
                    .setContentInfo(
                            ContentInfo.builder()
                                .setMediaRange(MediaRanges.APPLICATION_ATOM)
                                .build())
                     .build();
       assertEquals(Status.SUCCESS_OK, RESOURCE.acceptMessage(request, "").status());             
    }
    
    @Test
    public void handle_postRequestUnsuportedMessageType_unsupportedMediaTypeResponse() {
        final ContentInfo contentInfo =
                ContentInfo.builder()
                    .setMediaRange(MediaRanges.APPLICATION_XML)
                    .build();
        final Request request = 
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setMethod(Method.POST)
                    .setContentInfo(contentInfo)
                    .build();
        final Response response = RESOURCE.handle(request);
        
        assertEquals(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE, response.status());
    }
    
    @Test
    public void handle_postRequestSupportedMessageTypeSupportedResponseType_informationalContinue() {
        final Request request =
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setMethod(Method.POST)
                    .setPreferences(
                            ClientPreferences.builder()
                                .addAcceptedMediaRange(
                                        Preference.create(MediaRanges.APPLICATION_JSON, 1000))
                                .build())
                    .setContentInfo(
                            ContentInfo.builder()
                                .setMediaRange(MediaRanges.APPLICATION_ATOM)
                                .build())
                    .build();
        final Response response = RESOURCE.handle(request);
        assertEquals(Status.INFORMATIONAL_CONTINUE, response.status());
    }
    
    @Test
    public void handle_getRequestUnsupportedResponseContentType_notAcceptableResponse() {
        final Request request =
                Request.builder()
                    .setMethod(Method.GET)
                    .build();
        final Response response = RESOURCE.handle(request);
        assertEquals(Status.CLIENT_ERROR_NOT_ACCEPTABLE, response.status());
        assertTrue(isValidConnegResponse(response));
    }
    
    @Test
    public void handle_getRequest_successOK() {
        final Request request =
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setMethod(Method.GET)
                    .setPreferences(
                            ClientPreferences.builder()
                                .addAcceptedMediaRange(
                                        Preference.create(MediaRanges.APPLICATION_JSON, 1000))
                                .build())
                    .build();
        final Response response = RESOURCE.handle(request);
        assertEquals(Status.SUCCESS_OK, response.status());
        assertTrue(isValidConnegResponse(response));
    }
    
    @Test
    public void handle_getRequestNotFoundResource_notFound() {
        final ConnegResource resource = new ConnegResourceDecorator(Resources.NOT_FOUND) {
            @Override
            public Iterable<MediaRange> acceptedMediaRanges() {
                return ImmutableList.of();
            }

            @Override
            public Iterable<MediaRange> supportedMediaRanges() {
                return ImmutableList.of();
            }     
        };
        
        final Response response = resource.handle(Request.builder().build());
        assertEquals(Status.CLIENT_ERROR_BAD_REQUEST, response.status());
    }
}
