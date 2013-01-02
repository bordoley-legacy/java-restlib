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

import restlib.example.blog.serializable.MessageEntry;
import restlib.server.Resource;
import restlib.server.Resources;
import restlib.server.bio.BioConnegResourceDecorator;
import restlib.server.bio.InputStreamDeserializerSupplier;
import restlib.server.bio.OutputStreamSerializerSupplier;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public final class BioMessageEntryResource extends BioConnegResourceDecorator<MessageEntry> {
    public static final class Builder {
        private final ImmutableList.Builder<InputStreamDeserializerSupplier<MessageEntry>> deserializerSuppliers = ImmutableList.builder();
        private Resource resource = Resources.NOT_FOUND; 
        private final ImmutableList.Builder<OutputStreamSerializerSupplier> serializerSuppliers = ImmutableList.builder();
        
        private Builder(){}
        
        public Builder addDeserializerSupplier(final InputStreamDeserializerSupplier<MessageEntry> supplier) {
            Preconditions.checkNotNull(supplier);
            this.deserializerSuppliers.add(supplier);
            return this;
        }
        
        public Builder addSerializerSupplier(final OutputStreamSerializerSupplier supplier) {
            Preconditions.checkNotNull(supplier);
            this.serializerSuppliers.add(supplier);
            return this;
        }
        
        public BioMessageEntryResource build() {
            return new BioMessageEntryResource(this);
        }
        
        public Builder setResource(final Resource resource) {
            Preconditions.checkNotNull(resource);
            this.resource = resource;
            return this;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    private final Iterable<InputStreamDeserializerSupplier<MessageEntry>> deserializerSuppliers;
    private final Iterable<OutputStreamSerializerSupplier> serializerSuppliers;
    
    private BioMessageEntryResource(final Builder builder) {
        super(builder.resource);
        this.deserializerSuppliers = builder.deserializerSuppliers.build();
        this.serializerSuppliers = builder.serializerSuppliers.build();
    }

    @Override
    protected Iterable<InputStreamDeserializerSupplier<MessageEntry>> inputStreamDeserializerSuppliers() {
        return deserializerSuppliers;
    }

    @Override
    protected Iterable<OutputStreamSerializerSupplier> outputStreamSerializerSuppliers() {
        return serializerSuppliers;
    }
}
