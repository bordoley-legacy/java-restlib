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

import java.util.concurrent.ExecutionException;

import org.junit.Test;

import restlib.Request;
import restlib.Response;
import restlib.data.Method;
import restlib.net.Uri;

import com.google.common.util.concurrent.ListenableFuture;

public final class ResourceWrapperTest {
    private static final Resource MOCK_RESOURCE = 
            new UniformResource<Object>(Object.class) {
        @Override
        protected ListenableFuture<Response> post(Request request, Object message) {
            return FutureResponses.SUCCESS_OK;
        }

        @Override
        public Route route() {
            return Route.NONE;
        }

        @Override
        protected ListenableFuture<Response> get(Request request) {
            return FutureResponses.SUCCESS_OK;
        }    
    };
    
    private static final Resource WRAPPED_RESOURCE =
            new ResourceWrapper(MOCK_RESOURCE){};  
    
    @Test
    public void acceptMessge_postRequest_successOK() {
        final Request request = 
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setMethod(Method.POST)
                    .build();
        assertEquals(MOCK_RESOURCE.acceptMessage(request, ""), 
                WRAPPED_RESOURCE.acceptMessage(request, ""));
    }
    
    @Test
    public void handle_getRequest_successOK() throws InterruptedException, ExecutionException {
        final Request request = 
                Request.builder()
                    .setUri(Uri.parse("http://www.example.com"))
                    .setMethod(Method.GET)
                    .build();
        assertEquals(MOCK_RESOURCE.handle(request).get(),  WRAPPED_RESOURCE.handle(request).get());
    }
    
    @Test
    public void routeTest() {
        assertEquals(MOCK_RESOURCE.route(), WRAPPED_RESOURCE.route());
    }
}
