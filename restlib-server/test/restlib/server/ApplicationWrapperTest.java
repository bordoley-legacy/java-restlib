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

import org.junit.Test;

import restlib.ContentInfo;
import restlib.Request;
import restlib.Response;
import restlib.data.HttpDate;
import restlib.data.MediaRanges;
import restlib.net.Uri;

import com.google.common.base.Function;

public final class ApplicationWrapperTest {
    private static final Request FILTERED_REQUEST = 
            Request.builder()
                .setUri(Uri.parse("http://www.example.com"))
                .setContentInfo(
                        ContentInfo.builder()
                            .setMediaRange(MediaRanges.APPLICATION_ATOM).build())
                .build();
    
    private static final Response FILTERED_RESPONSE =
            Response.builder()
                .setAge(10)
                .setLastModified(HttpDate.create(100))
                .setLocation(Uri.parse("http://example.com"))
                .build();
    
    private static final Resource MOCK_RESOURCE = new Resource() {
        @Override
        public Response acceptMessage(Request request, Object message) {
            return Response.builder().build();
        }

        @Override
        public Route route() {
            return Route.NONE;
        }

        @Override
        public Response handle(Request request) {
            return Response.builder().build();
        }      
    };
    
    private static final Application MOCK_APPLICATION = new Application() {
        @Override
        public Resource getResource(final Request request) {
            return MOCK_RESOURCE;
        }

        @Override
        public Function<Request, Request> requestFilter() {
            return new Function<Request, Request>() {
                @Override
                public Request apply(Request input) {
                    return FILTERED_REQUEST;
                }      
            };
        }

        @Override
        public Function<Response, Response> responseFilter() {
            return new Function<Response, Response>() {
                @Override
                public Response apply(Response input) {
                    return FILTERED_RESPONSE;
                }      
            };
        }     
    };
    
    private static final Application WRAPPED_APPLICATION =
            new ApplicationWrapper(MOCK_APPLICATION) {};  
    
    @Test
    public void filterRequestTest() {
        assertEquals(FILTERED_REQUEST, WRAPPED_APPLICATION.requestFilter().apply(Request.builder().build()));
    }
    
    @Test
    public void filterResponseTest() {
        assertEquals(FILTERED_RESPONSE, WRAPPED_APPLICATION.responseFilter().apply(Response.builder().build()));
    }
    
    @Test
    public void getResourceTest() {
        assertEquals(MOCK_RESOURCE, WRAPPED_APPLICATION.getResource(Request.builder().build()));
    }
}
