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
import restlib.impl.Tokenizer;
import restlib.net.HostPort;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * Tracking information for forwarded messages  added by intermediaries.
 */
@Immutable
public final class Via {  
    static final Parser<Via> PARSER = new Parser<Via>() {   
        final Parser<Protocol> PROTOCOL_PARSER = new Parser<Protocol>() {
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
                    return Optional.of(Protocol.create("HTTP", Optionals.toString(tokens.get(0))));
                } else {
                    return Optional.of(
                            Protocol.create(
                                    Optionals.toString(tokens.get(0)), 
                                    Optionals.toString(tokens.get(2))));
                }
            }     
        };
        
        @Override
        public Optional<Via> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final int startPos = buffer.position();
            
            final List<Optional<Object>> tokens = 
                    Tokenizer.create(buffer)
                        .read(PROTOCOL_PARSER) // 0
                        .read(Primitives.WHITE_SPACE_PARSER) // 1
                        .read(Primitives.HOST_PORT_OR_PSEUDONYM) // 2
                        .read(Primitives.WHITE_SPACE_PARSER) // 3
                        .read(Comment.PARSER) // 4
                        .tokens();
            
            if (Optionals.isAbsent(tokens.get(0))) {
                return Optional.absent();
            }
            
            final Protocol receivedProtocol = (Protocol) tokens.get(0).get();  
            if (Optionals.isAbsent(tokens.get(2))) {
                buffer.position(startPos);
                return Optional.absent();
            }
            
            final Object receivedBy = tokens.get(2).get();   
            
            if (tokens.get(4).isPresent()) {
                final Comment comment = (Comment) tokens.get(4).get();
                return Optional.of(Via.create(receivedProtocol, receivedBy, Optional.of(comment)));
            }
            
            return Optional.of(Via.create(receivedProtocol, receivedBy, Optional.<Comment> absent()));  
        }      
    };
    
    /**
     * Creates a new Via.
     * @param receivedProtocol a non-null {@code Protocol}.
     * @param receivedBy a non-null {@code HostPort}.
     * @throws NullPointerException if {@code receivedProtocol} or {@code receivedBy} is null.
     */
    public static Via create(
            final Protocol receivedProtocol, 
            final HostPort receivedBy) {
        return Via.create(receivedProtocol, receivedBy, Optional.<Comment> absent());
    }
    
    /**
     * Creates a new Via.
     * @param receivedProtocol a non-null {@code Protocol}.
     * @param receivedBy a non-null {@code HostPort}.
     * @param comment a non-null {@code Comment}.
     * @throws NullPointerException if {@code receivedProtocol}, {@code receivedBy} 
     * or {@code comment} is null.
     */
    public static Via create(
            final Protocol receivedProtocol, 
            final HostPort receivedBy,
            final Comment comment) {  
        return Via.create(receivedProtocol, receivedBy, Optional.of(comment));
    }
    
    /**
     * Creates a new Via.
     * @param receivedProtocol a non-null {@code Protocol}.
     * @param receivedBy a valid host port string or HTTP token.
     * @throws NullPointerException if {@code receivedProtocol} or {@code receivedBy} is null.
     * @throws IllegalArgumentException if received by can not be parsed as a valid {@code HostPort} or 
     * HTTP token.
     */
    public static Via create(
            final Protocol receivedProtocol, 
            final String receivedBy) {
        return Via.create(receivedProtocol, receivedBy, Optional.<Comment>absent());
    }
        
    /**
     * Creates a new Via.
     * @param receivedProtocol a non-null {@code Protocol}.
     * @param receivedBy a valid host port string or HTTP token.
     * @param comment a non-null {@code Comment}.
     * @throws NullPointerException if {@code receivedProtocol}, {@code receivedBy} 
     * or {@code comment} is null.
     * @throws IllegalArgumentException if received by can not be parsed as a valid {@code HostPort} or 
     * HTTP token.
     */
    public static Via create(
            final Protocol receivedProtocol, 
            final String receivedBy,
            final Comment comment) {        
        return Via.create(receivedProtocol, receivedBy, Optional.of(comment));
    }
    
    private static Via create(final Protocol receivedProtocol, 
            Object receivedBy,
            final Optional<Comment> comment) {
        Preconditions.checkNotNull(receivedProtocol);
        Preconditions.checkNotNull(receivedBy);
        Preconditions.checkNotNull(comment);
        
        if (receivedBy instanceof String) {
            try {
                receivedBy = HostPort.parse((String) receivedBy);
            } catch (final IllegalArgumentException e){
                Preconditions.checkArgument(Primitives.IS_TOKEN.apply((String) receivedBy));
            }    
        }
        
        return new Via(receivedProtocol, receivedBy, comment);
    }
    
    private final Optional<Comment> comment;
    private final Object receivedBy;
    private final Protocol receivedProtocol;

    private Via(final Protocol receivedProtocol, 
            final Object receivedBy,
            final Optional<Comment> comment){
        this.receivedProtocol = receivedProtocol;
        this.receivedBy = receivedBy;
        this.comment = comment;
    }
    
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Via) {
            final Via that = (Via) obj;
            return this.receivedProtocol.equals(that.receivedProtocol) &&
                    this.receivedBy.equals(that.receivedBy) &&
                    this.comment.equals(that.comment);
        }
        return false;
    }
    
    /**
     * Returns an optional comment that may be used to identify the 
     * recipient software.
     */
    public Optional<Comment> comment() {
        return comment;
    }
    
    /**
     * Returns the host and optional port number of the recipient server or client 
     * that subsequently forwarded the message or a pseudonym. The returned object 
     * is either an instance of {@code HostPort} or {@code String} 
     * if receivedby is a pseudonym.
     */
    public Object receivedBy() {
        return receivedBy;
    }
    
    /**
     * Returns the protocol version of the message received by the 
     * server or client along each segment of the request/response chain.
     */
    public Protocol receivedProtocol() {
        return receivedProtocol;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(
                this.receivedProtocol, this.receivedBy, this.comment);
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (this.receivedProtocol.name().equals("HTTP")) {
            builder.append(this.receivedProtocol.version());
        } else {
            builder.append(this.receivedProtocol);
        }
        
        builder.append(" ").append(this.receivedBy);
        
        if (this.comment.isPresent()) {
            builder.append(" ").append(this.comment.get());
        }

        return builder.toString();
    }
}
