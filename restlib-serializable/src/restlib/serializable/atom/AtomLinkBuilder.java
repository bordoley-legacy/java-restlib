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

import restlib.data.Language;
import restlib.data.MediaRange;
import restlib.net.IRI;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public final class AtomLinkBuilder {    
    IRI href = null;
    Optional<String> rel = Optional.absent();
    Optional<MediaRange> type = Optional.absent();
    Optional<Language> hrefLang = Optional.absent();
    Optional<String> title = Optional.absent();
    Optional<Long> length = Optional.absent();
    
    AtomLinkBuilder() {
    }
    
    public AtomLink build() {
        Preconditions.checkState(this.href != null);
        return new AtomLinkImpl(this);
    }
    
    public AtomLinkBuilder setHref(final IRI href) {
        Preconditions.checkNotNull(href);
        this.href = href;
        return this;
    }

    public AtomLinkBuilder setRel(final String rel) {
        this.rel = Optional.of(rel);
        return this;
    }

    public AtomLinkBuilder setType(final MediaRange type) {
        this.type = Optional.of(type);
        return this;
    }
    
    public AtomLinkBuilder setHrefLang(final Language hrefLang) {
        this.hrefLang = Optional.of(hrefLang);
        return this;
    }

    public AtomLinkBuilder setTitle(final String title) {
        this.title  = Optional.of(title);
        return this;
    }
    
    public AtomLinkBuilder setLength(final long length) {
        Preconditions.checkArgument(length >= 0);
        this.length = Optional.of(length);
        return this;
    }
}
