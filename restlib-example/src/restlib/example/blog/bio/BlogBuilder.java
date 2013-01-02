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


package restlib.example.blog.bio;

import restlib.data.ExtensionMap;
import restlib.data.MediaRanges;
import restlib.example.blog.MessageEntryResource;
import restlib.example.blog.MessageFeedResource;
import restlib.example.blog.bio.serialization.DeserializerSuppliers;
import restlib.example.blog.bio.serialization.SerializerSuppliers;
import restlib.example.blog.dao.BlogStore;
import restlib.example.blog.serializable.MessageEntry;
import restlib.example.blog.serializable.MessageFeed;
import restlib.server.Route;
import restlib.server.bio.BioConnegResourceDecorator;
import restlib.server.bio.BioResource;
import restlib.server.bio.BioResources;
import restlib.server.bio.InputStreamDeserializerSupplier;
import restlib.server.bio.OutputStreamSerializerSupplier;

import com.google.common.collect.Iterables;

public final class BlogBuilder {  
    private static final ExtensionMap ENTRY_EXTENSION_MAP =
            ExtensionMap.builder()
                .put(MediaRanges.APPLICATION_JSON_ENTRY, "json")
                .put(MediaRanges.APPLICATION_ATOM_ENTRY, "atom")
                .put(MediaRanges.TEXT_HTML_ENTRY, "html")
                .build();
    
    private static final ExtensionMap FEED_EXTENSION_MAP =
            ExtensionMap.builder()
                .put(MediaRanges.APPLICATION_JSON_FEED, "json")
                .put(MediaRanges.APPLICATION_ATOM_FEED, "atom")
                .put(MediaRanges.TEXT_HTML_FEED, "html")
                .build();
    
    public static BlogBuilder newInstance(final Route feedRoute, final Route entryRoute) {
        return new BlogBuilder(feedRoute, entryRoute);
    }     
    
    public final BioResource<?> bioEntryResource;
    
    public final BioResource<?> bioFeedResource;
    
    private final BlogStore blogStore = new BlogStore();
    
    private BlogBuilder(final Route feedRoute, final Route entryRoute) {
        final MessageEntryResource entryResource =
                MessageEntryResource.builder()
                    .setAcceptedMediaRanges(
                            Iterables.transform(
                                    DeserializerSuppliers.ENTRY_DESERIALIZER_SUPPLIERS, 
                                    DeserializerSuppliers.INPUT_STREAM_DESERIALIZER_SUPPLIER_TO_MEDIARANGE))
                    .setSupportedMediaRanges(
                            Iterables.transform(
                                    SerializerSuppliers.ENTRY_SERIALIZER_SUPPLIERS, 
                                    SerializerSuppliers.OUTPUT_STREAM_SERIALIZER_SUPPLIER_TO_MEDIARANGE))
                    .setBlogStore(blogStore)
                    .setExtensionMap(ENTRY_EXTENSION_MAP)
                    .setRoute(entryRoute)
                    .build();
        
        bioEntryResource = BioResources.errorResource(
                new BioConnegResourceDecorator<MessageEntry>(entryResource) {
                    @Override
                    protected Iterable<InputStreamDeserializerSupplier<MessageEntry>> inputStreamDeserializerSuppliers() {
                        return DeserializerSuppliers.ENTRY_DESERIALIZER_SUPPLIERS;
                    }

                    @Override
                    protected Iterable<OutputStreamSerializerSupplier> outputStreamSerializerSuppliers() {
                        return SerializerSuppliers.ENTRY_SERIALIZER_SUPPLIERS;
                    } 
                });

        
        final MessageFeedResource feedResource = 
                MessageFeedResource.builder()
                    .setAcceptedMediaRanges(
                            Iterables.transform(
                                    DeserializerSuppliers.FEED_DESERIALIZER_SUPPLIERS, 
                                    DeserializerSuppliers.INPUT_STREAM_DESERIALIZER_SUPPLIER_TO_MEDIARANGE))
                    .setSupportedMediaRanges(
                            Iterables.transform(
                                    SerializerSuppliers.FEED_SERIALIZER_SUPPLIERS, 
                                    SerializerSuppliers.OUTPUT_STREAM_SERIALIZER_SUPPLIER_TO_MEDIARANGE))              
                    .setBlogStore(blogStore)
                    .setExtensionMap(FEED_EXTENSION_MAP)
                    .setAtomEntryResource(entryResource)
                    .setRoute(feedRoute)
                    .build();    

        bioFeedResource = BioResources.errorResource(
                new BioConnegResourceDecorator<MessageFeed>(feedResource) {
                    @Override
                    protected Iterable<InputStreamDeserializerSupplier<MessageFeed>> inputStreamDeserializerSuppliers() {
                        return DeserializerSuppliers.FEED_DESERIALIZER_SUPPLIERS;
                    }

                    @Override
                    protected Iterable<OutputStreamSerializerSupplier> outputStreamSerializerSuppliers() {
                        return SerializerSuppliers.FEED_SERIALIZER_SUPPLIERS;
                    }  
                });
    }
}
