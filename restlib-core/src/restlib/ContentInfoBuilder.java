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

import restlib.data.ContentEncoding;
import restlib.data.ContentRange;
import restlib.data.Language;
import restlib.data.MediaRange;
import restlib.net.Uri;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * A builder for generating instances of ContentInfo. ContentInfoBuilder instances can be 
 * reused; it is safe to call build() multiple times to build multiple ContentInfo instances.
 */
@NotThreadSafe
public final class ContentInfoBuilder {    
    Optional<Long> contentLength = Optional.absent();
    Optional<Uri> contentLocation = Optional.absent();
    Optional<ContentRange> contentRange = Optional.absent();
    final ImmutableList.Builder<ContentEncoding> encoding = ImmutableList.builder();
    final ImmutableSet.Builder<Language> language = ImmutableSet.builder();
    Optional<MediaRange> mediaRange = Optional.absent();

    ContentInfoBuilder() {}

    /**
     * Adds the ContentEncoding to this builder's ContentEncoding list.
     * @param encoding the ContentEncoding to add.
     * @return this {@code ContentInfoBuilder} instance.
     * @throws NullPointerException if {@code encoding} is null.
     */
    public ContentInfoBuilder addEncoding(final ContentEncoding encoding) {
        this.encoding.add(encoding);
        return this;
    }
    
    /**
     * Adds each ContentEncoding to this builder's ContentEncoding list.
     * @param encodings the ContentEncodings to add.
     * @return this {@code ContentInfoBuilder} instance.
     * @throws NullPointerException if {@code encodings} is null or contains a null element.
     */
    public ContentInfoBuilder addEncodings(final Iterable<ContentEncoding> encodings) {
        this.encoding.addAll(encodings);
        return this;
    }

    /**
     * Adds the Language to this builder's Language set.
     * @param language the Language to add.
     * @return this {@code ContentInfoBuilder} instance.
     * @throws NullPointerException if {@code language} is null.
     */
    public ContentInfoBuilder addLanguage(final Language language) {
        this.language.add(language);
        return this;
    }
    
    /**
     * Adds each Language to this builder's Language set.
     * @param languages the Languages to add.
     * @return this {@code ContentInfoBuilder} instance.
     * @throws NullPointerException if {@code languages} is null or contains a null element.
     */
    public ContentInfoBuilder addLanguages(final Iterable<Language> languages) {
        this.language.addAll(languages);
        return this;
    }

    /**
     * Returns a newly-created ContentInfo instance based on the contents of the ContentInfoBuilder.
     */
    public ContentInfo build() {
        return new ContentInfoImpl(this);
    }
    
    /**
     * Sets this builder's content length.
     * @param contentLength a length >= 0.
     * @return this {@code ContentInfoBuilder} instance.
     * @throws IllegalArgumentException if {@code contentLength} is < 0.
     */
    public ContentInfoBuilder setLength(final long contentLength) {
        Preconditions.checkArgument(contentLength  >= 0);
        this.contentLength = Optional.of(contentLength);
        return this;
    }
    
    /**
     * Sets this builder's content location.
     * @param contentLocation a non-null Uri.
     * @return this {@code ContentInfoBuilder} instance.
     * @throws NullPointerException if {@code contentLocation} is null.
     */
    public ContentInfoBuilder setLocation(final Uri contentLocation) {
        Preconditions.checkNotNull(contentLocation);
        if (!contentLocation.toString().isEmpty()) {
            this.contentLocation = Optional.of(contentLocation);
        }
        return this;
    }

    /**
     * Sets this builder's MediaRange.
     * @param mediaRange a non-null MediaRange.
     * @return this {@code ContentInfoBuilder} instance.
     * @throws NullPointerException if {@code mediaRange} is null.
     */
    public ContentInfoBuilder setMediaRange(final MediaRange mediaRange) {
        this.mediaRange = Optional.of(mediaRange);
        return this;
    }

    /**
     * Sets this builder's ContentRange.
     * @param contentRange a non-null ContentRange.
     * @return this {@code ContentInfoBuilder} instance.
     * @throws NullPointerException if {@code contentRange} is null.
     */
    public ContentInfoBuilder setRange(final ContentRange contentRange) {
        this.contentRange = Optional.of(contentRange);
        return this;
    }
}
