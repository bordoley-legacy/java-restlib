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


package restlib.example.blog.serializable;

import restlib.net.IRI;
import restlib.serializable.atom.AtomCategory;
import restlib.serializable.atom.AtomDate;
import restlib.serializable.atom.AtomFeedBuilder;
import restlib.serializable.atom.AtomFeedWrapper;
import restlib.serializable.atom.AtomGenerator;
import restlib.serializable.atom.AtomLink;
import restlib.serializable.atom.AtomPerson;

public final class MessageFeed extends AtomFeedWrapper<MessageEntry> {
    public static final class Builder {
        private final AtomFeedBuilder<MessageEntry> atomFeedBuilder = AtomFeedBuilder.newInstance();
        private Builder(){}
        
        public Builder addAuthor(final AtomPerson author) {
            this.atomFeedBuilder.addAuthor(author);
            return this;
        }
        
        public Builder addCategory(final AtomCategory category) {
            this.atomFeedBuilder.addCategory(category);
            return this;
        }
        
        public Builder addContributor(final AtomPerson contributor) {
            this.atomFeedBuilder.addContributor(contributor);
            return this;
        }

        public Builder addEntry(final MessageEntry entry) {
            this.atomFeedBuilder.addEntry(entry);
            return this;
        }
        
        public Builder addLink(final AtomLink link) {
            this.atomFeedBuilder.addLink(link);
            return this;
        }
        
        public Builder addLinks(final Iterable<? extends AtomLink> links) {
            this.atomFeedBuilder.addLinks(links);
            return this;
        }
        
        public MessageFeed build() {
            return new MessageFeed(this);
        }
        
        public Builder setGenerator(final AtomGenerator generator) {
            this.atomFeedBuilder.setGenerator(generator);
            return this;
        }
        
        public Builder setIcon(final IRI icon) {
            this.atomFeedBuilder.setIcon(icon);
            return this;
        }
        
        public Builder setId(final IRI id) {
            this.atomFeedBuilder.setId(id);
            return this;
        }
        
        public Builder setLogo(final IRI logo) {
            this.atomFeedBuilder.setLogo(logo);
            return this;
        }
        
        public Builder setPublished(final AtomDate published) {
            this.atomFeedBuilder.setPublished(published);
            return this;
        }
        
        public Builder setRights(final String rights) {
            this.atomFeedBuilder.setRights(rights);
            return this;
        }
        
        public Builder setSubtitle(final String subtitle) {
            this.atomFeedBuilder.setSubtitle(subtitle);
            return this;
        }
        
        public Builder setTitle(final String title) {
            this.atomFeedBuilder.setTitle(title);
            return this;
        }

        public Builder setUpdated(final AtomDate updated) {
            this.atomFeedBuilder.setUpdated(updated);
            return this;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    protected MessageFeed(final Builder builder) {
        super(builder.atomFeedBuilder.build());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterable<MessageEntry> getEntries() {
        // Cast is guaranteed to succeed
        return (Iterable<MessageEntry>) this.delegate().getEntries();
    }
}
