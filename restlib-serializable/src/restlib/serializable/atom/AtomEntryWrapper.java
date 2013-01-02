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

public abstract class AtomEntryWrapper<T> implements AtomEntry<T> {
    private final AtomEntry<?> delegate;
    
    protected AtomEntryWrapper (final AtomEntry<?> delegate) {
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }
    
    protected AtomEntry<?> delegate() {
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
    public IRI getId() {
        return delegate.getId();
    }

    @Override
    public Iterable<AtomLink> getLinks() {
        return delegate.getLinks();
    }

    @Override
    public Optional<AtomDate> getPublished() {
        return delegate.getPublished();
    }

    @Override
    public Optional<String> getRights() {
        return delegate.getRights();
    }

    @Override
    public Optional<AtomFeed<?>> getSource() {
        return delegate.getSource();
    }

    @Override
    public Optional<String> getSummary() {
        return delegate.getSummary();
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
