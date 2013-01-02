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
 * An HTTP connection option object for use in the HTTP Connection header.
 */
@Immutable
public final class ConnectionOption {
    private static final Registry<ConnectionOption> _REGISTERED = new Registry<ConnectionOption>();  
    public static final ConnectionOption CLOSE = register(create("close"));
    public static final ConnectionOption KEEP_ALIVE = register(create("Keep-Alive"));
    
    static Parser<ConnectionOption> PARSER = new Parser<ConnectionOption>() {
        @Override
        public Optional<ConnectionOption> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final Optional<String> token = Primitives.TOKEN_PARSER.parse(buffer);
            if (token.isPresent()) {
                return Optional.of(ConnectionOption.create(token.get()));
            } else {
                return Optional.absent();
            }
        } 
    };
    
    /**
     * Creates a new ConnectionOption instance.
     * @param connectionOption a non-null HTTP token.
     * @throws NullPointerException if {@code connectionOption} is null.
     * @throws IllegalArgumentException if{@code connectionOption} is not a valid HTTP token.
     */
    public static ConnectionOption create(final String connectionOption) {
        Preconditions.checkNotNull(connectionOption);   
        Preconditions.checkArgument(Primitives.IS_TOKEN.apply(connectionOption));
        
        return _REGISTERED.getIfPresent(
                new ConnectionOption(CaseInsensitiveString.wrap(connectionOption)));
    }
    
    private static ConnectionOption register(final ConnectionOption connectionOption) {
        return _REGISTERED.register(connectionOption);
    }
    
    private final CaseInsensitiveString connectionToken;

    private ConnectionOption(final CaseInsensitiveString connectionToken){
        this.connectionToken = connectionToken;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof ConnectionOption) {
            final ConnectionOption that = (ConnectionOption) obj;
            return this.connectionToken.equals(that.connectionToken);
        } 
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.connectionToken);
    }
    
    @Override
    public String toString() {
        return this.connectionToken.toString();
    }
}
