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


package restlib.example.async;

import restlib.Request;
import restlib.Response;
import restlib.bio.InputStreamDeserializer;
import restlib.bio.OutputStreamSerializer;
import restlib.data.MediaRanges;
import restlib.server.Route;
import restlib.server.bio.BioResourceDecorator;
import restlib.server.bio.InputStreamDeserializerSuppliers;
import restlib.server.bio.OutputStreamSerializerSuppliers;

import com.google.common.base.Preconditions;

public class BioContinuationResource extends BioResourceDecorator<String> {
    public static BioContinuationResource newInstance(final Route route) {
        Preconditions.checkNotNull(route);
        return new BioContinuationResource(route);
    }

    private BioContinuationResource(final Route route) {
        super(ContinuationResource.newInstance(route));
    }

    @Override
    public InputStreamDeserializer<String> getRequestEntityDeserializer(final Request request) {
        return InputStreamDeserializerSuppliers.stringDeserializerSupplier(MediaRanges.TEXT_ANY).get(request);
    }

    @Override
    public OutputStreamSerializer getResponseEntitySerializer(final Request request, final Response response) {
        return OutputStreamSerializerSuppliers.stringSerializerSupplier(MediaRanges.TEXT_PLAIN).get(request, response);
    }
}
