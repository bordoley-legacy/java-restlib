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
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.impl.CaseInsensitiveString;
import restlib.impl.CharBuffers;
import restlib.impl.Optionals;
import restlib.impl.Parser;
import restlib.impl.Parsers;
import restlib.impl.Tokenizer;

import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * ChallengeMessage
 */
@Immutable
public abstract class ChallengeMessage {  
    /**
     * A base64 ChallengeMesssage. 
     */
    @Immutable
    public static final class Base64 extends ChallengeMessage {
        private final String base64;
        
        private Base64(final CaseInsensitiveString scheme, final String base64) {
            super (scheme);
            this.base64 = base64;
        }
        
        /**
         * Returns the ChallengeMessage's base64 data.
         */
        public String base64data() {
            return this.base64;
        }

        @Override
        public boolean equals(@Nullable final Object obj) {
            if (this == obj) {
                return true;
            } else if (obj instanceof Base64) {
                final Base64 that = (Base64) obj;
                return this.scheme.equals(that.scheme) &&  this.base64.equals(that.base64);
            } 
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.scheme, this.base64);
        }
        
        @Override
        public String toString() {
            return this.scheme() + " " + this.base64;
        }
    }     
    
    /**
     * A parameters ChallengeMessage.
     */
    @Immutable
    public static final class Parameters extends ChallengeMessage {        
        private final ImmutableMap<String, String> parameters;
        
        private Parameters(final CaseInsensitiveString scheme, final ImmutableMap<String, String> parameters) {
            super(scheme);
            this.parameters = parameters;
        }
        
        @Override
        public boolean equals(@Nullable final Object obj) {
            if (this == obj) {
                return true;
            } else if (obj instanceof Parameters) {
                final Parameters that = (Parameters) obj;
                return this.scheme.equals(that.scheme) && this.parameters.equals(that.parameters);
            } 
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.scheme, this.parameters);
        }
        
        /**
         * Returns the ChallengeMessage parameters.
         */
        public Map<String, String> parameters() {
            return this.parameters;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(this.scheme()).append(" ");
            
            for (final Entry<String, String> entry : this.parameters.entrySet()) {
                builder.append(entry.getKey().toString());
                if (entry.getValue().isEmpty()) {
                    continue;
                } else if (entry.getKey().equals("realm")) {
                    builder.append('=').append(Primitives.encodeQuotedString(entry.getValue()));
                } else {
                    builder.append('=').append(Primitives.encodeWord(entry.getValue()));
                } builder.append(',');
            }
            
            if (builder.length() > 0) {
                builder.setLength(builder.length() - 1);
            }
            
            return builder.toString();
        }
    }
    
    // FIXME: There are some really odd cases that we fail to parse, notably empty comma seperated strings.
    static final Parser<ChallengeMessage> PARSER = new Parser<ChallengeMessage>() {
        final Parser<String> BASE_64_TEXT = new Parser<String> () {
            @Override
            public Optional<String> parse(final CharBuffer buffer) {
                Preconditions.checkNotNull(buffer);
                final int startPos = buffer.position();
                
                final List<Optional<Object>> tokens = 
                        Tokenizer.create(buffer)
                            .read(Parsers.whileMatchesParser(CharMatchers.B64_MATCHER)) // 0
                            .read(Parsers.whileMatchesParser(CharMatcher.is('='))) // 1
                            .read(Primitives.WHITE_SPACE_PARSER)
                            .tokens();
                            
                if (buffer.position() < buffer.capacity()) {
                    if (buffer.get() != ',') {
                        buffer.position(startPos);
                        return Optional.absent();
                    } else {
                        CharBuffers.pushback(buffer);
                    }
                }
                
                if (Optionals.isAbsent(tokens.get(0))) {
                    return Optional.absent();
                }

                return Optional.of(Optionals.toString(tokens.get(0)) + Optionals.toStringOrEmpty(tokens.get(1)));           
            }       
        };
        
        final Parser<KeyValuePair> CHALLENGE_MESSAGE_PAIR = new Parser<KeyValuePair>() {
            @Override
            public Optional<KeyValuePair> parse(final CharBuffer buffer) {
                Preconditions.checkNotNull(buffer);
                final int startPos = buffer.position();
                
                final List<Optional<Object>> tokens = 
                        Tokenizer.create(buffer)
                            .read(Primitives.TOKEN_PARSER) // 0
                            .readOptional(Primitives.WHITE_SPACE_PARSER) // 1
                            .read(Parsers.charParser('=')) // 2
                            .readOptional(Primitives.WHITE_SPACE_PARSER) // 3
                            .read(Primitives.WORD_PARSER) // 4
                            .tokens();
                
                if (Optionals.isAbsent(tokens.get(0)) || Optionals.isAbsent(tokens.get(4))) {
                    buffer.position(startPos);
                    return Optional.absent();
                } else {
                    return Optional.of(
                            KeyValuePair.create(
                                    Optionals.toString(tokens.get(0)), 
                                    Optionals.toString(tokens.get(4))));
                }
            }         
        };

        final Parser<Object> BASE_64_OR_CHALLENGE_MESSAGE_PAIR =
                Parsers.firstAvailableParser(BASE_64_TEXT, CHALLENGE_MESSAGE_PAIR);
        
        @Override
        public Optional<ChallengeMessage> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            
            final int startPos = buffer.position();
            final Tokenizer tokenizer = 
                    Tokenizer.create(buffer)
                        .read(Primitives.TOKEN_PARSER) // 0
                        .read(Primitives.WHITE_SPACE_PARSER) // 1
                        .read(BASE_64_OR_CHALLENGE_MESSAGE_PAIR); // 2
            final List<Optional<Object>> tokens = tokenizer.tokens();
            
           if (Optionals.isAbsent(tokens.get(0)) || Optionals.isAbsent(tokens.get(2))) {
               return Optional.absent();
           }
            
           final String scheme = tokens.get(0).get().toString();  
           if (tokens.get(2).get() instanceof String) {
               return Optional.<ChallengeMessage> of(ChallengeMessage.base64ChallengeMessage(scheme, tokens.get(2).get().toString()));
           } else {             
               tokenizer.readWhileAvailable(Primitives.OWS_COMMA_OWS_PARSER, CHALLENGE_MESSAGE_PAIR);        
               final Iterable<KeyValuePair> params = Iterables.filter(Optional.presentInstances(tokens), KeyValuePair.class);
     
               try {
                 return Optional.<ChallengeMessage> of(ChallengeMessage.parameterChallengeMessage(scheme, params));
               } catch (final IllegalArgumentException e) {
                   // Most likely would occur if a key in the parameters challenge occurs more than once.
                   buffer.position(startPos);
                   return Optional.absent();
               }
           }
        }
    };
    
    private static final String REALM = "realm";
    
    /**
     * Returns a new base64 challenge message.
     * @param scheme a non-null HTTP-token.
     * @param base64data a non-null base64 String.
     * @throws NullPointerException if {@code scheme} or {@code base64data} are null.
     * @throws IllegalArgumentException if {@code scheme} is not a valid HTTP-token,
     * or {@code base64data} is not valid base64 data.
     */
    public static ChallengeMessage base64ChallengeMessage(
            final String scheme, final String base64data) {
        Preconditions.checkNotNull(scheme);
        Preconditions.checkNotNull(base64data);     
        Preconditions.checkArgument(Primitives.IS_TOKEN.apply(scheme));
        Preconditions.checkArgument(
                Primitives.IS_BASE_64.apply(base64data));
        
        return new Base64(CaseInsensitiveString.wrap(scheme), base64data);
    }
    
    /**
     * Returns a new basic authentication challenge message.
     * @param realm
     * @return
     */
    public static ChallengeMessage basicAuthenticationChallenge(
            final String realm) {
        Preconditions.checkNotNull(realm);
        Preconditions.checkArgument(Primitives.IS_QUOTABLE.apply(realm));
        
        return ChallengeMessage.parameterChallengeMessage("basic",
                ImmutableMap.of(REALM, realm));
    }
    
    private static ChallengeMessage parameterChallengeMessage(
            final String scheme,
            final Iterable<? extends Entry<String, String>> parameters) {
        Preconditions.checkNotNull(scheme);
        Preconditions.checkNotNull(parameters);
        Preconditions.checkArgument(Primitives.IS_TOKEN.apply(scheme));
        Preconditions.checkArgument(!Iterables.isEmpty(parameters));
        
        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (final Entry<String, String> entry : parameters) {
            Preconditions.checkNotNull(entry);
            Preconditions.checkArgument(Primitives.IS_TOKEN.apply(entry.getKey()));
            Preconditions.checkArgument(Primitives.IS_WORD.apply(entry.getValue()));
            builder.put(Ascii.toLowerCase(entry.getKey()), entry.getValue());
        }  
        
        return new Parameters(CaseInsensitiveString.wrap(scheme), builder.build());
    }

    /**
     * Returns a new {@link ChallengeMessage} with the given scheme
     * and parameters.
     * @param scheme a non-null HTTP-token.
     * @param params a non-null, non-empty Map of HTTP-tokens to HTTP-words.
     * @throws NullPointerException if scheme or parameters are null, or 
     * if parameters contains any entries with null keys or value.
     * @throws IllegalArgumentException if {@code scheme} is not a valid HTTP-token, or if
     * {@code parameters} is empty, or if {@code parameters} include any keys that are not
     * valid HTTP-tokens, or any values that are not valid HTTP-words.
     */
    public static ChallengeMessage parameterChallengeMessage(
            final String scheme,
            final Map<String, String> parameters) {
        Preconditions.checkNotNull(parameters);
        return parameterChallengeMessage(scheme, parameters.entrySet());
    }
    
    final CaseInsensitiveString scheme;
    
    private ChallengeMessage(final CaseInsensitiveString scheme){
        this.scheme = scheme;
    }
    
    /**
     * Returns the scheme component of the ChallengeMessage.
     */
    public final String scheme() {
        return scheme.toString();
    }
}
