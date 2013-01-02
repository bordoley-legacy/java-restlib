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

public final class AtomEntryBuilder<T> {
    public static <T> AtomEntryBuilder<T> newInstance() {
        return new AtomEntryBuilder<T>();
    }
    
    final ImmutableList.Builder<AtomPerson> authors = ImmutableList.builder();
    final ImmutableList.Builder<AtomCategory> categories = ImmutableList.builder();
    Optional<T> content = Optional.absent();
    final ImmutableList.Builder<AtomPerson> contributors = ImmutableList.builder();
    IRI id = null;
    final ImmutableList.Builder<AtomLink> links = ImmutableList.builder();
    Optional<AtomDate> published = Optional.absent();
    Optional<String> rights = Optional.absent();
    Optional<AtomFeed<?>> source = Optional.absent();
    Optional<String> summary = Optional.absent();
    String title = "";
    AtomDate updated = null;
    
    private AtomEntryBuilder() {};
    
    public AtomEntryBuilder<T> addAuthor(final AtomPerson author) {
        Preconditions.checkNotNull(author);
        this.authors.add(author);
        return this;
    }
    
    public AtomEntryBuilder<T> addCategory(final AtomCategory category) {
        Preconditions.checkNotNull(category);
        this.categories.add(category);
        return this;
    }
    
    public AtomEntryBuilder<T> addContributor(final AtomPerson contributor) {
        Preconditions.checkNotNull(contributor);
        this.contributors.add(contributor);
        return this;
    }
    
    public AtomEntryBuilder<T> addLink(final AtomLink link) {
        Preconditions.checkNotNull(link);
        this.links.add(link);
        return this;
    }
    
    public AtomEntryBuilder<T> addLinks(final Iterable<? extends AtomLink> links) {
        Preconditions.checkNotNull(links);
        this.links.addAll(links);
        return this;
    }
    
    public AtomEntry<T> build() {
        return new AtomEntryImpl<T>(this);
    }
    
    public AtomEntryBuilder<T> setContent(final T content) {
        this.content = Optional.of(content);
        return this;
    }
    
    public AtomEntryBuilder<T> setId(final IRI id) {
        Preconditions.checkNotNull(id);
        this.id = id;
        return this;
    }
    
    public AtomEntryBuilder<T> setPublished(final AtomDate published) {
        this.published = Optional.of(published);
        return this;
    }
    
    public AtomEntryBuilder<T> setRights(final String rights) {
        this.rights = Optional.of(rights);
        return this;
    }
    
    public AtomEntryBuilder<T> setSource(final AtomFeed<?> source) {
        this.source = Optional.<AtomFeed<?>>of(source);
        return this;
    }
    
    public AtomEntryBuilder<T> setSummary(final String summary) {
        this.summary = Optional.of(summary);
        return this;
    }
    
    public AtomEntryBuilder<T> setTitle(final String title) {
        Preconditions.checkNotNull(title);
        this.title = title;
        return this;
    }
    
    public AtomEntryBuilder<T> setUpdated(final AtomDate updated) {
        Preconditions.checkNotNull(updated);
        this.updated = updated;
        return this;
    }
    
    static void test() {
        
    }
}
