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


package restlib.example.blog;

import restlib.data.ExtensionMap;
import restlib.net.IRI;
import restlib.net.Uri;
import restlib.serializable.atom.AtomEntry;
import restlib.serializable.atom.AtomLink;
import restlib.serializable.atom.AtomLinks;
import restlib.server.ConnegResource;
import restlib.server.UniformResource;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public abstract class AtomEntryResource<T extends AtomEntry<?>> extends UniformResource<T> implements ConnegResource {
    protected AtomEntryResource(final Class<T> messageClass) {
        super(messageClass);
    }

    protected abstract ExtensionMap extensionMap();
    
    public final IRI getId(final Uri uri, final long created){
        Preconditions.checkNotNull(uri);
        Preconditions.checkArgument(this.route().match(uri.path()));
        
        return Uri.tagBuilder()
                    .setAuthorityName(uri.host())
                    .setSpecificPath(uri.path())
                    .setDate(created).build();
    }
    
    public final Iterable<AtomLink> getLinks(final Uri uri) {
        Preconditions.checkNotNull(uri);
        Preconditions.checkArgument(this.route().match(uri.path()));

        return Iterables.concat(
                ImmutableList.of(
                        AtomLinks.selfLink(uri),
                        AtomLinks.editLink(uri)), 
                        AtomLinks.alternateLinks(
                                uri, this.supportedMediaRanges(), this.extensionMap()));
    }
}
