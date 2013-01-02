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

public interface AtomFeed<T extends AtomEntry<?>> {             
    public Iterable<AtomPerson> getAuthors();
    public Iterable<AtomCategory> getCategories();
    public Iterable<AtomPerson> getContributors();
    public Iterable<T> getEntries();
    public Optional<AtomGenerator> getGenerator();
    public Optional<IRI> getIcon();
    public IRI getId();
    public Iterable<AtomLink> getLinks();
    public Optional<IRI> getLogo();
    public Optional<String> getRights();
    public Optional<String> getSubtitle();
    public String getTitle(); 
    public AtomDate getUpdated();
}
