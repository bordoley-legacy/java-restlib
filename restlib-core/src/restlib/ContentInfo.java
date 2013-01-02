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

import static restlib.MessageHelpers.appendHeader;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.data.ContentEncoding;
import restlib.data.ContentRange;
import restlib.data.HttpHeaders;
import restlib.data.Language;
import restlib.data.MediaRange;
import restlib.net.Uri;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
/**
 * Content properties of an HTTP message entity.
 * Client preferences that may be used by a server to perform content negotiation. 
 * Implementations must be immutable or effectively immutable.
 */
@Immutable
public abstract class ContentInfo {   
    /**
     * ContentInfo null object instance.
     */
    static final ContentInfo NONE = ContentInfo.builder().build();
    
    /**
     * Returns a new ContentInfoBuilder instance
     */
    public static ContentInfoBuilder builder() {
        return new ContentInfoBuilder();
    }
    
    ContentInfo(){}
    
    /**
     * Returns the content-codings that have been applied to the message entity.
     * The returned {@code List} is unmodifiable.
     */
    public abstract List<ContentEncoding> encodings();
    
    @Override
    public final boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof ContentInfo) {
            final ContentInfo that = (ContentInfo) obj;
            return this.length().equals(that.length()) &&
                    this.location().equals(that.location()) &&
                    this.range().equals(that.range()) &&
                    this.encodings().equals(that.encodings()) &&
                    this.languages().equals(that.languages()) &&
                    this.mediaRange().equals(that.mediaRange());
        } 
        return false;
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(
                this.length(), this.location(), 
                this.range(), this.encodings(),
                this.languages(), this.mediaRange());
    }
    
    /**
     * Returns the natural language(s) for the intended audience of the message entity.
     * The returned {@code Set} is unmodifiable.
     */
    public abstract Set<Language> languages();

    /**
     * Returns the length of the message entity, if available.
     */
    public abstract Optional<Long> length();

    /**
     * Return a URI that can be used as a specific identifier for the message entity.
     */
    public abstract Optional<Uri> location();
    
    /**
     * Returns the media type of the message entity.
     */
    public abstract Optional<MediaRange> mediaRange();
    
    /**
     * Returns where in the full message entity the payload body is intended to be applied in
     * responses to range requests.
     */
    public abstract Optional<ContentRange> range();
    
    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        appendHeader(builder, HttpHeaders.CONTENT_TYPE, this.mediaRange());
        appendHeader(builder, HttpHeaders.CONTENT_ENCODING, this.encodings());
        appendHeader(builder, HttpHeaders.CONTENT_LANGUAGE, this.languages());
        appendHeader(builder, HttpHeaders.CONTENT_LENGTH, this.length());
        appendHeader(builder, HttpHeaders.CONTENT_RANGE, this.range());
            
        return builder.toString();
    }
}
