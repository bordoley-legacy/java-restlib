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


package restlib.server.bio;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import restlib.ContentInfo;
import restlib.Request;
import restlib.Response;
import restlib.bio.InputStreamDeserializer;
import restlib.bio.OutputStreamSerializer;
import restlib.data.Status;
import restlib.server.Route;
import restlib.server.bio.BioResource;
import restlib.server.bio.BioResourceWrapper;

public final class BioResourceWrapperTest {
    private static final BioResource<String> MOCK_RESOURCE =
            new BioResource<String>() {
                final InputStreamDeserializer<String> deserializer = 
                        new InputStreamDeserializer<String>() {
                            @Override
                            public String read(InputStream is) throws IOException {
                                return "";
                            }};
                            
                final OutputStreamSerializer serializer = 
                        new OutputStreamSerializer() {
                            @Override
                            public ContentInfo contentInfo() {
                                return ContentInfo.builder().build();
                            }
        
                            @Override
                            public long write(OutputStream os) throws IOException {
                                return 1;
                            }};
                            
                @Override
                public Response acceptMessage(Request request, Object message) {
                    return Status.SUCCESS_OK.toResponse();
                }

                @Override
                public Route route() {
                    return Route.NONE;
                }

                @Override
                public Response handle(Request request) {
                    return Status.SUCCESS_OK.toResponse();
                }

                @Override
                public InputStreamDeserializer<String> getRequestEntityDeserializer(Request request) {
                    return deserializer;
                }

                @Override
                public OutputStreamSerializer getResponseEntitySerializer(Request request, Response response) {
                    return serializer;
                }   
            };

    final BioResourceWrapper<String> MOCK_WRAPPER = new BioResourceWrapper<String>(MOCK_RESOURCE);
    
    @Test
    public void getRequestEntityDeserializerTest() {
        final InputStreamDeserializer<?> resourceDeserializer = 
                MOCK_RESOURCE.getRequestEntityDeserializer(Request.builder().build());
        final InputStreamDeserializer<?> wrapperDeserializer =
                MOCK_WRAPPER.getRequestEntityDeserializer(Request.builder().build());
        assertEquals(resourceDeserializer, wrapperDeserializer);
    }
    
    @Test
    public void getResponseEntitySerializerTest() {
        final OutputStreamSerializer resourceSerializer = 
                MOCK_RESOURCE.getResponseEntitySerializer(Request.builder().build(), Response.builder().build());
        final OutputStreamSerializer wrapperSerializer =
                MOCK_WRAPPER.getResponseEntitySerializer(Request.builder().build(), Response.builder().build());
        assertEquals(resourceSerializer, wrapperSerializer);
    }
    
}
