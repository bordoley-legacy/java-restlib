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
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.impl.Optionals;
import restlib.impl.Parser;
import restlib.impl.Parsers;
import restlib.impl.Registry;
import restlib.impl.Tokenizer;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * An HTTP Protocol element for use in the Upgrade and Via headers.
 */
@Immutable
public final class Protocol {
    private static final Registry<Protocol> _REGISTERED = new Registry<Protocol>();
    
    /**
     * The HTTP/1.1 protocol.
     */
    public static final Protocol HTTP_1_1 = register(create("HTTP", "1.1"));
    
    /**
     * The HTTP/1.0 protocol.
     */
    public static final Protocol HTTP_1_0 = register(create("HTTP", "1.0"));
    
    /**
     * The HTTP/0.9 protocol.
     */
    public static final Protocol HTTP_0_9 = register(create("HTTP", "0.9"));
    
    static final Parser<Protocol> PARSER = new Parser<Protocol>() {
        @Override
        public Optional<Protocol> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final int startPos = buffer.position();
            final List<Optional<Object>> tokens = 
                    Tokenizer.create(buffer)
                        .read(Primitives.TOKEN_PARSER) // 0
                        .read(Parsers.charParser('/')) // 1
                        .read(Primitives.TOKEN_PARSER) // 2      
                        .tokens();
            if (Optionals.isAbsent(tokens.get(0)) || 
                    (tokens.get(1).isPresent() && Optionals.isAbsent(tokens.get(2)))) {
                buffer.position(startPos);
                return Optional.absent();
            } else if (Optionals.isAbsent(tokens.get(2))) {
                return Optional.of(Protocol.create(Optionals.toString(tokens.get(0)), ""));
            } else {
                return Optional.of(
                        Protocol.create(
                                Optionals.toString(tokens.get(0)), 
                                Optionals.toString(tokens.get(2))));
            }
        }     
    };
    
    /**
     * Creates a new Protocol instance.
     * @param name an HTTP token.
     * @param version an HTTP token. 
     * @throws NullPointerException if either {@code name} or {@code version} is null.
     * @throws IllegalArgumentException if {@code name is empty}. Also if either {@code name} or {@code version} is
     * not a valid HTTP token.
     */
    public static Protocol create(final String name, final String version) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(version);
        Preconditions.checkArgument(
                Primitives.IS_TOKEN.apply(name));
        Preconditions.checkArgument(
                version.isEmpty() ||
                Primitives.IS_TOKEN.apply(version));
        
        final Protocol retval = new Protocol(name, version);
        return _REGISTERED.getIfPresent(retval);        
    }
    
    private static Protocol register(final Protocol protocol) {
        return _REGISTERED.register(protocol);
        
    }
    
    private final String name;
    private final String version;
    
    private Protocol(final String name, final String version) {
        this.name = name;
        this.version = version;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Protocol) {
            final Protocol that = (Protocol) obj;
            return this.name.equals(that.name) &&
                    this.version.equals(that.version);
        }
        return false;
    }

    /**
     * The protocol name.
     */
    public String name() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name, this.version);
    }

    @Override
    public String toString() {
        return this.name + (this.version.isEmpty() ? "" : "/" + this.version);
    }
    
    /**
     * The protocol version.
     */
    public String version() {
        return version;
    }
}
