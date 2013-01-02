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


package restlib.serializable.atom;

import restlib.data.ExtensionMap;
import restlib.data.MediaRange;
import restlib.impl.Optionals;
import restlib.net.IRI;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public final class AtomLinks {
    public static AtomLinkBuilder builder(){
        return new AtomLinkBuilder();
    }
    
    public static Iterable<AtomLink> alternateLinks(
            final IRI iri, 
            final Iterable<MediaRange> mediaRanges,
            final ExtensionMap extensionMap) {
        final ImmutableList.Builder<AtomLink> links = ImmutableList.builder();
        
        for (final MediaRange mediaRange : mediaRanges) {
            final Optional<String> ext = extensionMap.getExtension(mediaRange);
            
            if (ext.isPresent()) {
                links.add(AtomLinks.builder()
                        // FIXME: Should not assume the link has no query or fragment
                        .setHref(IRI.parse(iri.toString() + "." + Optionals.toString(ext)))
                        .setType(mediaRange)
                        .setRel(LinkRelationships.ALTERNATE)
                        .build());
            }
        }
        
        return links.build();
    }
    
    public static AtomLink editLink(final IRI iri) {
        return AtomLinks.builder().setHref(iri).setRel(LinkRelationships.EDIT).build();
    }
    
    public static AtomLink selfLink(final IRI iri) {
        return AtomLinks.builder().setHref(iri).setRel(LinkRelationships.SELF).build();
    }
    
    private AtomLinks(){}
}
