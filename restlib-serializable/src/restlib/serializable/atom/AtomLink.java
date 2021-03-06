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

public interface AtomLink {
    public IRI getHref();
    public Optional<String> getRel();
    public Optional<MediaRange> getType();
    public Optional<Language> getHrefLang();
    public Optional<String> getTitle();
    public Optional<Long> getLength();
}
