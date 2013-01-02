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
import restlib.serializable.atom.AtomEntryBuilder;
import restlib.serializable.atom.AtomEntryWrapper;
import restlib.serializable.atom.AtomFeed;
import restlib.serializable.atom.AtomLink;
import restlib.serializable.atom.AtomPerson;

import com.google.common.base.Optional;

public final class MessageEntry extends AtomEntryWrapper<String> {
    public static final class Builder {
        private final AtomEntryBuilder<String> atomEntryBuilder = AtomEntryBuilder.newInstance();
        
        private Builder(){}
        
        public Builder addAuthor(final AtomPerson author) {
            this.atomEntryBuilder.addAuthor(author);
            return this;
        }
        
        public Builder addCategory(final AtomCategory category) {
            this.atomEntryBuilder.addCategory(category);
            return this;
        }
        
        public Builder addContributor(final AtomPerson contributor) {
            this.atomEntryBuilder.addContributor(contributor);
            return this;
        }
        
        public Builder addLink(final AtomLink link) {
            this.atomEntryBuilder.addLink(link);
            return this;
        }
        
        public Builder addLinks(final Iterable<? extends AtomLink> links) {
            this.atomEntryBuilder.addLinks(links);
            return this;
        }
        
        public MessageEntry build() {
            return new MessageEntry(this);
        }
        
        public Builder setContent(final String content) {
            this.atomEntryBuilder.setContent(content);
            return this;
        }
        
        public Builder setId(final IRI id) {
            this.atomEntryBuilder.setId(id);
            return this;
        }
        
        public Builder setPublished(final AtomDate published) {
            this.atomEntryBuilder.setPublished(published);
            return this;
        }
        
        public Builder setRights(final String rights) {
            this.atomEntryBuilder.setRights(rights);
            return this;
        }
        
        public Builder setSource(final AtomFeed<?> source) {
            this.atomEntryBuilder.setSource(source);
            return this;
        }
        
        public Builder setSummary(final String summary) {
            this.atomEntryBuilder.setSummary(summary);
            return this;
        }
        
        public Builder setTitle(final String title) {
            this.atomEntryBuilder.setTitle(title);
            return this;
        }
        
        public Builder setUpdated(final AtomDate updated) {
            this.atomEntryBuilder.setUpdated(updated);
            return this;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }

    private MessageEntry(final Builder builder) {
        super(builder.atomEntryBuilder.build());
    }

    @Override
    public Optional<String> getContent() {
        return (Optional<String>) this.delegate().getContent();
    }
}
