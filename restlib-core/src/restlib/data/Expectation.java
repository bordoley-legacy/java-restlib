package restlib.data;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.impl.GuavaCollectionHelpers;
import restlib.impl.Optionals;
import restlib.impl.Parser;
import restlib.impl.Tokenizer;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * Server behaviors required by a client. Used in the HTTP Expect header included in Requests.
 */
@Immutable
public final class Expectation {     
    /**
     * Sent by a client to determine if the origin server is willing to accept the request
     * (based on the request header fields) before the client sends the payload body.
     */
    public static final Expectation EXPECTS_100_CONTINUE = 
            Expectation.create(ImmutableMap.<String, String> of("100-continue","").entrySet());
    
    static final Parser<Expectation> PARSER = new Parser<Expectation>() {
        @Override
        public Optional<Expectation> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final List<Optional<Object>> tokens = 
                    Tokenizer.create(buffer)
                                .read(KeyValuePair.PARSER)
                                .readWhileAvailable(
                                        Primitives.OWS_SEMICOLON_OWS_PARSER, KeyValuePair.PARSER)
                                .tokens();
            
            if (Optionals.isAbsent(tokens.get(0))) {
                return Optional.absent();
            } else {
                return Optional.of(
                    Expectation.create(
                            Iterables.filter(Optional.presentInstances(tokens), KeyValuePair.class)));
            }
        }       
    };
    
    /**
     * Returns a new Expecation base upon the key value pairs in {@code expectation}.
     * @param expectation a non-null multimap of non-null HTTP tokens to HTTP words.
     * @throws NullPointerException if {@code expectation} is null or contains null keys or values.
     * @throws IllegalArgumentException if any key in {@code expectation} is a not a valid HTTP token, or if any value in
     * {@code expectation} is not a valid HTTP word.
     */
    public static Expectation create(final Multimap<String, String> expectation) {
        return create(expectation.entries());
    }
    
    private static Expectation create(final Iterable<? extends Map.Entry<String, String>> expectations) {
        return new Expectation(
                GuavaCollectionHelpers.immutableSetMultimapFromEntries(
                        Iterables.transform(expectations, KeyValuePair.VALIDATE_TO_LOWER_CASE_KEYS)));
    }
    
    private final ImmutableSetMultimap<String, String> expectations;
    
    private Expectation(final ImmutableSetMultimap<String, String> expectations) {
        this.expectations = expectations;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(expectations);
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if(obj instanceof Expectation) {
            final Expectation that = (Expectation) obj;
            return this.expectations.equals(that.expectations);
        } 
        return false;
    }

    @Override
    public String toString() {
        return KeyValuePair.toString(this.expectations.entries());
    }
 }
