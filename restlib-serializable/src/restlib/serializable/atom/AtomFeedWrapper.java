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

public abstract class AtomFeedWrapper<T extends AtomEntry<?>> implements AtomFeed<T> {
    private final AtomFeed<T> delegate;

    protected AtomFeedWrapper(final AtomFeed<T> delegate) {
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }
    
    protected AtomFeed<? extends AtomEntry<?>> delegate() {
        return delegate;
    }
    
    @Override
    public Iterable<AtomPerson> getAuthors() {
        return delegate.getAuthors();
    }
    
    @Override
    public Iterable<AtomCategory> getCategories() {
        return delegate.getCategories();
    }

    @Override
    public Iterable<AtomPerson> getContributors() {
        return delegate.getContributors();
    }

    @Override
    public Iterable<T> getEntries() {
        return delegate.getEntries();
    }

    @Override
    public Optional<AtomGenerator> getGenerator() {
        return delegate.getGenerator();
    }

    @Override
    public Optional<IRI> getIcon() {
        return delegate.getIcon();
    }

    @Override
    public IRI getId() {
        return delegate.getId();
    }

    @Override
    public Iterable<AtomLink> getLinks() {
        return delegate.getLinks();
    }

    @Override
    public Optional<IRI> getLogo() {
        return delegate.getLogo();
    }

    @Override
    public Optional<String> getRights() {
        return delegate.getRights();
    }

    @Override
    public Optional<String> getSubtitle() {
        return delegate.getSubtitle();
    }

    @Override
    public String getTitle() {
        return delegate.getTitle();
    }

    @Override
    public AtomDate getUpdated() {
        return delegate.getUpdated();
    }
}
