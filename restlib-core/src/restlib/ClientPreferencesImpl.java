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


package restlib;

import java.util.Set;

import restlib.data.Charset;
import restlib.data.ContentEncoding;
import restlib.data.Language;
import restlib.data.MediaRange;
import restlib.data.Preference;
import restlib.data.Range;
import restlib.data.TransferCoding;

import com.google.common.base.Optional;

final class ClientPreferencesImpl extends ClientPreferences {
    private final Set<Preference<Charset>> acceptedCharsets;
    private final Set<Preference<ContentEncoding>> acceptedEncodings;
    private final Set<Preference<Language>> acceptedLanguages;
    private final Set<Preference<MediaRange>> acceptedMediaRanges;
    private final Set<Preference<TransferCoding>> acceptedTransferEncodings;
    private final Optional<Range> range;

    ClientPreferencesImpl(final ClientPreferencesBuilder builder) {
        this.acceptedCharsets = builder.acceptedCharsets.build();
        this.acceptedEncodings = builder.acceptedEncodings.build();
        this.acceptedLanguages = builder.acceptedLanguages.build();
        this.acceptedMediaRanges = builder.acceptedMediaRanges.build();
        this.acceptedTransferEncodings = builder.acceptedTransferEncodings.build();
        this.range = builder.range;
    }

    @Override
    public Set<Preference<Charset>> acceptedCharsets() {
        return this.acceptedCharsets;
    }

    @Override
    public Set<Preference<ContentEncoding>> acceptedEncodings() {
        return this.acceptedEncodings;
    }

    @Override
    public Set<Preference<Language>> acceptedLanguages() {
        return this.acceptedLanguages;
    }

    @Override
    public Set<Preference<MediaRange>> acceptedMediaRanges() {
        return this.acceptedMediaRanges;
    }

    @Override
    public Set<Preference<TransferCoding>> acceptedTransferEncodings() {
        return this.acceptedTransferEncodings;
    }

    @Override
    public Optional<Range> range() {
        return this.range;
    }
}
