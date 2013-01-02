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

import com.google.common.base.Optional;

import restlib.net.IRI;

final class AtomEntryImpl<T> implements AtomEntry<T> {
    private final Iterable<AtomPerson> authors;
    private final Iterable<AtomCategory> categories;
    private final Optional<T> content;
    private final Iterable<AtomPerson> contributors;
    private final IRI id;
    private final Iterable<AtomLink> links;
    private final Optional<AtomDate> published;
    private final Optional<String> rights;
    private final Optional<AtomFeed<?>> source;
    private final Optional<String> summary;
    private final String title;
    private final AtomDate updated;
    
    AtomEntryImpl(final AtomEntryBuilder<T> builder) {
        this.authors = builder.authors.build();
        this.categories = builder.categories.build();
        this.content = builder.content;
        this.contributors = builder.contributors.build();
        this.id = builder.id;
        this.links = builder.links.build();
        this.published = builder.published;
        this.rights = builder.rights;
        this.source = builder.source;
        this.summary = builder.summary;
        this.title = builder.title;
        this.updated = builder.updated;
    }
    
    @Override
    public Iterable<AtomPerson> getAuthors() {
        return authors;
    }

    @Override
    public Iterable<AtomCategory> getCategories() {
        return categories;
    }

    @Override
    public Optional<T> getContent() {
        return content;
    }

    @Override
    public Iterable<AtomPerson> getContributors() {
        return contributors;
    }

    @Override
    public IRI getId() {
        return id;
    }

    @Override
    public Iterable<AtomLink> getLinks() {
        return links;
    }

    @Override
    public Optional<AtomDate> getPublished() {
        return published;
    }

    @Override
    public Optional<String> getRights() {
        return rights;
    }

    @Override
    public Optional<AtomFeed<?>> getSource() {
        return source;
    }

    @Override
    public Optional<String> getSummary() {
        return summary;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public AtomDate getUpdated() {
        return updated;
    }

}
