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

final class AtomLinkImpl implements AtomLink {
    private final IRI href;
    private final Optional<String> rel;
    private final Optional<MediaRange> type;
    private final Optional<Language> hrefLang;
    private final Optional<String> title;
    private final Optional<Long> length;
    
    AtomLinkImpl(final AtomLinkBuilder builder){
        Preconditions.checkNotNull(builder);
        this.href = builder.href;
        this.rel = builder.rel;
        this.type = builder.type;
        this.hrefLang = builder.hrefLang;
        this.title = builder.title;
        this.length = builder.length;
    }
    
    public IRI getHref() {
        return this.href;
    }

    public Optional<String> getRel() {
        return this.rel;
    }

    public Optional<MediaRange> getType() {
        return this.type;
    }

    public Optional<Language> getHrefLang() {
        return this.hrefLang;
    }

    public Optional<String> getTitle() {
        return this.title;
    }

    public Optional<Long> getLength() {
        return this.length;
    }
}
