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


package restlib.example.blog.dao;

import restlib.example.blog.serializable.MessageEntry;
import restlib.net.IRI;
import restlib.serializable.atom.AtomDate;
import restlib.serializable.atom.AtomLink;

public final class BlogEntryHelpers {
    private BlogEntryHelpers() {}
    
    public static final MessageEntry toMessageEntry(
            final BlogEntry entry, 
            final IRI id,
            final Iterable<AtomLink> links) {
        return MessageEntry.builder()
                .setId(id)
                .addLinks(links)
                .setContent(entry.getContent())
                .setPublished(AtomDate.of(entry.getCreated()))
                .setUpdated(AtomDate.of(entry.getUpdated())).build();
    }
    
    public static final BlogEntry fromMessageEntry(final MessageEntry entry, final String id){
        return BlogEntryBuilder.newInstance()
                    .setId(id) 
                    .setContent(entry.getContent().or(""))
                    .build();
        
    }
}
