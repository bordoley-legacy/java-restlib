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

import restlib.Request;
import restlib.Response;
import restlib.bio.InputStreamDeserializer;
import restlib.bio.OutputStreamSerializer;
import restlib.data.MediaRange;
import restlib.data.Preference;
import restlib.server.ConnegResourceDecorator;
import restlib.server.Resource;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public abstract class BioConnegResourceDecorator<T> 
    extends ConnegResourceDecorator implements BioConnegResource<T> {
    
    private static final Function<InputStreamDeserializerSupplier<?>, MediaRange> INPUT_STREAM_SUPPLIER_TO_MEDIARANGE = 
            new Function<InputStreamDeserializerSupplier<?>, MediaRange>() {
                public MediaRange apply(final InputStreamDeserializerSupplier<?> supplier) {
                    return supplier.mediaRange();
                }
            };

    private static final Function<OutputStreamSerializerSupplier, MediaRange> OUTPUT_STREAM_SUPPLIER_TO_MEDIARANGE = 
            new Function<OutputStreamSerializerSupplier, MediaRange>() {
                public MediaRange apply(final OutputStreamSerializerSupplier supplier) {
                    return supplier.mediaRange();
                }
            };          

    protected BioConnegResourceDecorator(final Resource delegate) {
        super(delegate);
    }

    protected abstract Iterable<InputStreamDeserializerSupplier<T>> inputStreamDeserializerSuppliers();

    @Override
    public final Iterable<MediaRange> acceptedMediaRanges() {
        return Iterables.transform(
                this.inputStreamDeserializerSuppliers(),
                INPUT_STREAM_SUPPLIER_TO_MEDIARANGE);
    }

    @Override
    public final InputStreamDeserializer<T> getRequestEntityDeserializer(final Request request) {
        final Optional<MediaRange> contentMediaRange = request.contentInfo().mediaRange();
        
        Preconditions.checkState(contentMediaRange.isPresent());
        
        double bestScore = -1;
        InputStreamDeserializerSupplier<T> retval = null;
        
        for (final InputStreamDeserializerSupplier<T> deserializerSupplier : this.inputStreamDeserializerSuppliers()) {
            final MediaRange mediaRange = deserializerSupplier.mediaRange();
            final double score = mediaRange.match(contentMediaRange.get());
            
            if (score > bestScore) {
                bestScore = score;
                retval = deserializerSupplier;
            }
        }
        
        Preconditions.checkState(retval != null);
        
        // This should never NPE;
        return retval.get(request);
    }
    
    @Override
    public final OutputStreamSerializer getResponseEntitySerializer(final Request request, final Response response) {
        final Optional<MediaRange> preferred =
                Preference.<MediaRange> bestMatch(
                        request.preferences().acceptedMediaRanges(),
                        this.supportedMediaRanges());
        
        Preconditions.checkState(preferred.isPresent());
        
        return Iterables.find(
                this.outputStreamSerializerSuppliers(),          
                new Predicate<OutputStreamSerializerSupplier>() {
                    public boolean apply(final OutputStreamSerializerSupplier supplier) {
                        return supplier.mediaRange().equals(preferred.get());
                    }
                }).get(request, response);
    }
    
    protected abstract Iterable<OutputStreamSerializerSupplier> outputStreamSerializerSuppliers();

    @Override
    public final Iterable<MediaRange> supportedMediaRanges() {
        return Iterables.transform(this.outputStreamSerializerSuppliers(),
                OUTPUT_STREAM_SUPPLIER_TO_MEDIARANGE);
    }
}
