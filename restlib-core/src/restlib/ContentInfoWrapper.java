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

import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import restlib.data.ContentEncoding;
import restlib.data.ContentRange;
import restlib.data.Language;
import restlib.data.MediaRange;
import restlib.net.Uri;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * An implementation of ContentInfo which forwards all its method calls to another instance of ContentInfo. 
 * Subclasses should override one or more methods to modify the behavior of the backing ContentInfo 
 * as desired per the decorator pattern.
 */
@Immutable
public class ContentInfoWrapper extends ContentInfo {
    private final ContentInfo delegate;
    
    /**
     * Constructs an instance of ContentInfoWrapper that 
     * forwards all method calls to {@code delegate}.
     * @param delegate a non-null instance of ContentInfo.
     * @throws NullPointerException if {@code delegate} is null.
     */
    protected ContentInfoWrapper(final ContentInfo delegate) {
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public List<ContentEncoding> encodings() {
        return this.delegate.encodings();
    }

    @Override
    public Set<Language> languages() {
        return this.delegate.languages();
    }

    @Override
    public Optional<Long> length() {
        return this.delegate.length();
    }

    @Override
    public Optional<Uri> location() {
        return this.delegate.location();
    }

    @Override
    public Optional<MediaRange> mediaRange() {
        return this.delegate.mediaRange();
    }

    @Override
    public Optional<ContentRange> range() {
        return this.delegate.range();
    }
}
