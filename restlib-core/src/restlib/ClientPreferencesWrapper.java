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

import javax.annotation.concurrent.Immutable;

import restlib.data.Charset;
import restlib.data.ContentEncoding;
import restlib.data.Language;
import restlib.data.MediaRange;
import restlib.data.Preference;
import restlib.data.Range;
import restlib.data.TransferCoding;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * An implementation of ClientPreferences which forwards all its method calls to another instance of ClientPreferences. 
 * Subclasses should override one or more methods to modify the behavior of the backing ClientPreferences 
 * as desired per the decorator pattern.
 */
@Immutable
public class ClientPreferencesWrapper extends ClientPreferences {
    private final ClientPreferences delegate; 
    
    /**
     * Constructs an instance of ClientPreferencesWrapper that 
     * forwards all method calls to {@code delegate}.
     * @param delegate a non-null instance of ClientPreferences.
     * @throws NullPointerException if {@code delegate} is null.
     */
    protected ClientPreferencesWrapper (final ClientPreferences delegate) {
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }
    
    @Override
    public Set<Preference<Charset>> acceptedCharsets() {
        return this.delegate.acceptedCharsets();
    }

    @Override
    public Set<Preference<ContentEncoding>> acceptedEncodings() {
        return this.delegate.acceptedEncodings();
    }

    @Override
    public Set<Preference<Language>> acceptedLanguages() {
        return this.delegate.acceptedLanguages();
    }

    @Override
    public Set<Preference<MediaRange>> acceptedMediaRanges() {
        return this.delegate.acceptedMediaRanges();
    }

    @Override
    public Set<Preference<TransferCoding>> acceptedTransferEncodings() {
        return this.delegate.acceptedTransferEncodings();
    }

    @Override
    public Optional<Range> range() {
        return this.delegate.range();
    }
}
