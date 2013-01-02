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
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public final class AtomFeedBuilder<T extends AtomEntry<?>> {
    public static <T extends AtomEntry<?>> AtomFeedBuilder<T> newInstance() {
        return new AtomFeedBuilder<T>();
    }
    
    final ImmutableList.Builder<AtomPerson> authors = ImmutableList.builder();
    final ImmutableList.Builder<AtomCategory> categories = ImmutableList.builder();
    final ImmutableList.Builder<AtomPerson> contributors = ImmutableList.builder();
    final ImmutableList.Builder<T> entries = ImmutableList.builder();
    Optional<AtomGenerator> generator = Optional.absent();
    Optional<IRI> icon = Optional.absent();
    IRI id = null;
    final ImmutableList.Builder<AtomLink> links = ImmutableList.builder();
    Optional<IRI> logo = Optional.absent();
    Optional<AtomDate> published = Optional.absent();
    Optional<String> rights = Optional.absent();
    Optional<String> subtitle = Optional.absent();
    String title = "";
    AtomDate updated = null;
    
    private AtomFeedBuilder(){
        
    }
    
    public AtomFeedBuilder<T> addAuthor(final AtomPerson author) {
        Preconditions.checkNotNull(author);
        this.authors.add(author);
        return this;
    }
    
    public AtomFeedBuilder<T> addCategory(final AtomCategory category) {
        Preconditions.checkNotNull(category);
        this.categories.add(category);
        return this;
    }
    
    public AtomFeedBuilder<T> addContributor(final AtomPerson contributor) {
        Preconditions.checkNotNull(contributor);
        this.contributors.add(contributor);
        return this;
    }

    public AtomFeedBuilder<T> addEntry(final T entry) {
        Preconditions.checkNotNull(entry);
        this.entries.add(entry);
        return this;
    }
    
    public AtomFeedBuilder<T> addLink(final AtomLink link) {
        Preconditions.checkNotNull(link);
        this.links.add(link);
        return this;
    }
    
    public AtomFeedBuilder<T> addLinks(final Iterable<? extends AtomLink> links) {
        Preconditions.checkNotNull(links);
        this.links.addAll(links);
        return this;
    }
    
    public AtomFeed<T> build() {
        return new AtomFeedImpl<T>(this);
    }
    
    public AtomFeedBuilder<T> setGenerator(final AtomGenerator generator) {
        this.generator = Optional.of(generator);
        return this;
    }
    
    public AtomFeedBuilder<T> setIcon(final IRI icon) {
        this.icon = Optional.of(icon);
        return this;
    }
    
    public AtomFeedBuilder<T> setId(final IRI id) {
        Preconditions.checkNotNull(id);
        this.id = id;
        return this;
    }
    
    public AtomFeedBuilder<T> setLogo(final IRI logo) {
        this.logo = Optional.of(logo);
        return this;
    }
    
    public AtomFeedBuilder<T> setPublished(final AtomDate published) {
        this.published = Optional.of(published);
        return this;
    }
    
    public AtomFeedBuilder<T> setRights(final String rights) {
        this.rights = Optional.of(rights);
        return this;
    }
    
    public AtomFeedBuilder<T> setSubtitle(final String subtitle) {
        this.subtitle = Optional.of(subtitle);
        return this;
    }
    
    public AtomFeedBuilder<T> setTitle(final String title) {
        Preconditions.checkNotNull(title);
        this.title = title;
        return this;
    }

    public AtomFeedBuilder<T> setUpdated(final AtomDate updated) {
        Preconditions.checkNotNull(updated);
        this.updated = updated;
        return this;
    }
}
