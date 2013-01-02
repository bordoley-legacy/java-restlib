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

import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.data.Charset;
import restlib.data.ContentEncoding;
import restlib.data.HttpHeaders;
import restlib.data.Language;
import restlib.data.MediaRange;
import restlib.data.Preference;
import restlib.data.Range;
import restlib.data.TransferCoding;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * Client preferences that may be used by a server to perform content negotiation.
 * Implementations must be immutable or effectively immutable.
 */
@Immutable
public abstract class ClientPreferences {
    /**
     * ClientPreferences null object instance.
     */
    static final ClientPreferences NONE = ClientPreferences.builder().build();
    
    /**
     * Returns a new ClientPreferencesBuilder instance.
     */
    public static ClientPreferencesBuilder builder() {
        return new ClientPreferencesBuilder();
    }

    ClientPreferences(){}
    
    /**
     * Indicates which character encodings are preferred in a response payload. 
     * The returned {@code Set} is unmodifiable.
     */
    public abstract Set<Preference<Charset>> acceptedCharsets();

    /**
     * Indicates which response content-codings are preferred in ta response payload.
     * The returned {@code Set} is unmodifiable.
     */
    public abstract Set<Preference<ContentEncoding>> acceptedEncodings();

    /**
     * Indicates which natural languages that are preferred in a response payload.
     * The returned {@code Set} is unmodifiable.
     */
    public abstract Set<Preference<Language>> acceptedLanguages();

    /**
     * Indicates which content types are preferred in a response payload.
     * The returned {@code Set} is unmodifiable.
     */
    public abstract Set<Preference<MediaRange>> acceptedMediaRanges();

    /**
     * Indicates which transfer-codings are preferred in a response.
     * The returned {@code Set} is unmodifiable.
     */
    public abstract Set<Preference<TransferCoding>> acceptedTransferEncodings();

    @Override
    public final boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof ClientPreferences) {
            final ClientPreferences that = (ClientPreferences) obj;
            return this.acceptedCharsets().equals(that.acceptedCharsets()) &&
                    this.acceptedEncodings().equals(that.acceptedEncodings()) &&
                    this.acceptedLanguages().equals(that.acceptedLanguages()) &&
                    this.acceptedMediaRanges().equals(that.acceptedMediaRanges()) &&
                    this.acceptedTransferEncodings().equals(that.acceptedTransferEncodings()) &&
                    this.range().equals(that.range());
        } 
        return false;
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(
                this.acceptedCharsets(), 
                this.acceptedEncodings(), 
                this.acceptedLanguages(), 
                this.acceptedMediaRanges(),
                this.acceptedTransferEncodings(),
                this.range());
    }

    /**
     * The ranges requested by the client.
     */
    public abstract Optional<Range> range();

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        appendHeader(builder, HttpHeaders.ACCEPT, this.acceptedMediaRanges());
        appendHeader(builder, HttpHeaders.ACCEPT_CHARSET, this.acceptedCharsets());
        appendHeader(builder, HttpHeaders.ACCEPT_ENCODING, this.acceptedEncodings());
        appendHeader(builder, HttpHeaders.ACCEPT_LANGUAGE, this.acceptedLanguages());
        appendHeader(builder, HttpHeaders.TE, this.acceptedTransferEncodings());
        appendHeader(builder, HttpHeaders.RANGE, this.range());
            
        return builder.toString();
    }
}
