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
import restlib.bio.InputStreamDeserializer;
import restlib.bio.OutputStreamSerializer;
import restlib.data.MediaRanges;
import restlib.server.Resource;
import restlib.server.bio.BioResourceDecorator;
import restlib.server.bio.InputStreamDeserializerSupplier;
import restlib.server.bio.InputStreamDeserializerSuppliers;
import restlib.server.bio.OutputStreamSerializerSupplier;
import restlib.server.bio.OutputStreamSerializerSuppliers;

import com.google.common.base.Preconditions;

public final class BioEchoResource extends BioResourceDecorator<String> {
    private static final InputStreamDeserializerSupplier<String> DESERIALIZER_SUPPLIER =
            InputStreamDeserializerSuppliers.stringDeserializerSupplier(MediaRanges.TEXT_ANY);
    
    private static final OutputStreamSerializerSupplier SERIALIZER_SUPPLIER =
            OutputStreamSerializerSuppliers.stringSerializerSupplier(MediaRanges.TEXT_PLAIN);

    public static BioEchoResource newInstance(final Resource resource) {
        Preconditions.checkNotNull(resource);
        return new BioEchoResource(resource);
    }
    
    private BioEchoResource(final Resource resource) {
        super(resource);
    }
    
    @Override
    public InputStreamDeserializer<String> getRequestEntityDeserializer(final Request request) {
        return DESERIALIZER_SUPPLIER.get(request);
    }

    @Override
    public OutputStreamSerializer getResponseEntitySerializer(final Request request, final Response response) {
        return SERIALIZER_SUPPLIER.get(request, response);
    }
}
