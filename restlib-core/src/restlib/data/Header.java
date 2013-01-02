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

package restlib.data;



import java.nio.CharBuffer;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.impl.CaseInsensitiveString;
import restlib.impl.Parser;
import restlib.impl.Registry;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * Representation of an HTTP Header field-name.
 */
@Immutable
public final class Header {
    private static final Registry<Header> _REGISTERED = new Registry<Header>();
    
    static final Parser<Header> PARSER = new Parser<Header>() {
        @Override
        public Optional<Header> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final Optional<String> token = Primitives.TOKEN_PARSER.parse(buffer);
            if (token.isPresent()) {
                return Optional.of(Header.create(token.get()));
            } else {
                return Optional.absent();
            }
        }       
    };
    
    /**
     * Returns a new Header.
     * @param header a non-null HTTP-token.
     * @throws NullPointerException if {@code header} is null.
     * @throws IllegalArgumentException if {@header} is not a valid HTTP-token.
     */
    public static Header create(final String header) {
        Preconditions.checkNotNull(header);
        Preconditions.checkArgument(Primitives.IS_TOKEN.apply(header));
        final Header parsed = new Header(CaseInsensitiveString.wrap(header));    
        return _REGISTERED.getIfPresent(parsed);
    }

    static Header register(final Header header) {
        return _REGISTERED.register(header);  
    }
    
    private final CaseInsensitiveString header;

    private Header(final CaseInsensitiveString header) {
        this.header = header;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Header) {
            final Header that = (Header) obj;
            return this.header.equals(that.header);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.header);
    }

    @Override
    public String toString() {
        return this.header.toString();
    }
}
