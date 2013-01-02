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

import javax.annotation.concurrent.NotThreadSafe;

import restlib.data.Charset;
import restlib.data.ContentEncoding;
import restlib.data.Language;
import restlib.data.MediaRange;
import restlib.data.Preference;
import restlib.data.Range;
import restlib.data.TransferCoding;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

/**
 * A builder for generating instances of {@code ClientPreference}. 
 * ClientPreferencesBuilder instances can be reused; it is safe to call build() 
 * multiple times to build multiple {@code ClientPreference} instances.
 */
@NotThreadSafe
public final class ClientPreferencesBuilder {
    final ImmutableSet.Builder<Preference<Charset>> acceptedCharsets = ImmutableSet.builder();
    final ImmutableSet.Builder<Preference<ContentEncoding>> acceptedEncodings = ImmutableSet.builder();
    final ImmutableSet.Builder<Preference<Language>> acceptedLanguages = ImmutableSet.builder();
    final ImmutableSet.Builder<Preference<MediaRange>> acceptedMediaRanges = ImmutableSet.builder();
    final ImmutableSet.Builder<Preference<TransferCoding>> acceptedTransferEncodings = ImmutableSet.builder();
    Optional<Range> range = Optional.absent();

    ClientPreferencesBuilder() {}
    
    /**
     * Adds the Charset preference to this builder's accepted Charset preference set.
     * @param charset the Charset preference to add.
     * @return this {@code ClientPreferencesBuilder} instance.
     * @throws NullPointerException if {@code charset} is null.
     */
    public ClientPreferencesBuilder addAcceptedCharset(final Preference<Charset> charset) {
        this.acceptedCharsets.add(charset);
        return this;
    }

    /**
     * Adds each Charset preference to this builder's accepted Charset preference set.
     * @param charsets the Charset preferences to add.
     * @return this {@code ClientPreferencesBuilder} instance.
     * @throws NullPointerException if {@code charsets} is null or contains a null element.
     */
    public ClientPreferencesBuilder addAcceptedCharsets(final Iterable<Preference<Charset>> charsets) {
        this.acceptedCharsets.addAll(charsets);
        return this;
    }

    /**
     * Adds the ContentEnncoding preference to this builder's accepted ContentEncodings preference set.
     * @param encoding the ContentEncoding preference to add.
     * @return this {@code ClientPreferencesBuilder} instance.
     * @throws NullPointerException if {@code encoding} is null.
     */
    public ClientPreferencesBuilder addAcceptedEncoding(final Preference<ContentEncoding> encoding) {
        this.acceptedEncodings.add(encoding);
        return this;
    }

    /**
     * Adds each encoding preference to this builder's accepted ContentEncodings preference set.
     * @param encodings the ContentEncoding preferences to add.
     * @return this {@code ClientPreferencesBuilder} instance.
     * @throws NullPointerException if {@code encodings} is null or contains a null element.
     */
    public ClientPreferencesBuilder addAcceptedEncodings(final Iterable<Preference<ContentEncoding>> encodings) {
        this.acceptedEncodings.addAll(encodings);
        return this;
    }

    /**
     * Adds the Language preference to this builder's accepted Language preference set.
     * @param language the Language preference to add.
     * @return this {@code ClientPreferencesBuilder} instance.
     * @throws NullPointerException if {@code language} is null.
     */
    public ClientPreferencesBuilder addAcceptedLanguage(final Preference<Language> language) {
        this.acceptedLanguages.add(language);
        return this;
    }

    /**
     * Adds each Language preference to this builder's accepted Language preference set.
     * @param languages the Language preferences to add.
     * @return this {@code ClientPreferencesBuilder} instance.
     * @throws NullPointerException if {@code languages} is null or contains a null element.
     */
    public ClientPreferencesBuilder addAcceptedLanguages(final Iterable<Preference<Language>> languages) {
        this.acceptedLanguages.addAll(languages);
        return this;
    }
    
    /**
     * Adds the MediaRange preference to this builder's accepted MediaRange preference set.
     * @param mediaRange the MediaRange preference to add.
     * @return this {@code ClientPreferencesBuilder} instance.
     * @throws NullPointerException if {@code mediaRange} is null.
     */
    public ClientPreferencesBuilder addAcceptedMediaRange(final Preference<MediaRange> mediaRange) {
        this.acceptedMediaRanges.add(mediaRange);
        return this;
    }

    /**
     * Adds each MediaRange preference to this builder's accepted MediaRange preference set.
     * @param mediaRanges the MediaRange preferences to add.
     * @return this {@code ClientPreferencesBuilder} instance.
     * @throws NullPointerException if {@code mediaRanges} is null or contains a null element.
     */
    public ClientPreferencesBuilder addAcceptedMediaRanges(final Iterable<Preference<MediaRange>> mediaRanges) {
        this.acceptedMediaRanges.addAll(mediaRanges);
        return this;
    }

    /**
     * Adds the TransferEncoding preference to this builder's accepted TransferEncoding preference set.
     * @param transferEncoding the TransferEncoding preference to add.
     * @return this {@code ClientPreferencesBuilder} instance.
     * @throws NullPointerException if {@code transferEncoding} is null.
     */
    public ClientPreferencesBuilder addAcceptedTransferEncoding(final Preference<TransferCoding> transferEncoding) {
        this.acceptedTransferEncodings.add(transferEncoding);
        return this;
    }

    /**
     * Adds each TransferEncoding preference to this builder's accepted TransferEncoding preference set.
     * @param transferEncodings the MediaRange preferences to add.
     * @return this {@code ClientPreferencesBuilder} instance.
     * @throws NullPointerException if {@code transferEncodings} is null or contains a null element.
     */
    public ClientPreferencesBuilder addAcceptedTransferEncodings(final Iterable<Preference<TransferCoding>> transferEncodings) {
        this.acceptedTransferEncodings.addAll(transferEncodings);
        return this;
    }

    /**
     * Returns a newly-created {@code ClientPreferences} instance based 
     * on the contents of the ClientPreferencesBuilder.
     */
    public ClientPreferences build() {
        return new ClientPreferencesImpl(this);
    }

    /**
     * Sets this builder's Range preference.
     * @param range a non-null Range
     * @return this {@code ClientPreferencesBuilder} instance.
     * @throws NullPointerException if {@code range} is null.
     */
    public ClientPreferencesBuilder setRange(final Range range) {
        Preconditions.checkNotNull(range);
        this.range = Optional.of(range);
        return this;
    }
}
