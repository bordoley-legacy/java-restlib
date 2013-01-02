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

import restlib.net.IRI;

import com.google.common.base.Optional;

final class AtomFeedImpl<T extends AtomEntry<?>> implements AtomFeed<T>{
    private final Iterable<AtomPerson> authors;
    private final Iterable<AtomCategory> categories;
    private final Iterable<AtomPerson> contributors;
    private final Iterable<T> entries;
    private final Optional<AtomGenerator> generator;
    private final Optional<IRI> icon;
    private final IRI id;
    private final Iterable<AtomLink> links;
    private final Optional<IRI> logo;
    private final Optional<String> rights;
    private final Optional<String> subtitle;
    private final String title;
    private final AtomDate updated;
    
    AtomFeedImpl(final AtomFeedBuilder<T> builder) {
        this.authors = builder.authors.build();
        this.categories = builder.categories.build();
        this.contributors = builder.contributors.build();
        this.entries = builder.entries.build();
        this.generator = builder.generator;
        this.icon = builder.icon;
        this.id = builder.id;
        this.links = builder.links.build();
        this.logo = builder.logo;
        this.rights = builder.rights;
        this.subtitle = builder.subtitle;
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
    public Iterable<AtomPerson> getContributors() {
        return contributors;
    }

    @Override
    public Iterable<T> getEntries() {
        return entries;
    }

    @Override
    public Optional<AtomGenerator> getGenerator() {
        return generator;
    }

    @Override
    public Optional<IRI> getIcon() {
        return icon;
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
    public Optional<IRI> getLogo() {
        return logo;
    }

    @Override
    public Optional<String> getRights() {
        return rights;
    }

    @Override
    public Optional<String> getSubtitle() {
        return subtitle;
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
