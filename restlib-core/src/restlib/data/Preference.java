package restlib.data;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.impl.GuavaCollectionHelpers;
import restlib.impl.Optionals;
import restlib.impl.Parser;
import restlib.impl.Parsers;
import restlib.impl.Tokenizer;

import com.google.common.base.CharMatcher;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;

/**
 * Wrapper around a Matcheable<T> which represents a clients preference for a given 
 * instance of T.
 * @param <T> 
 */
@Immutable
public final class Preference<T extends Matcheable<T>> {   
    private static final Parser<Character> EQUALS_CHAR_PARSER = Parsers.charParser('=');
    
    private static final Parser<Character> Q_CHAR_PARSER = 
            Parsers.characterPredicateParser(CharMatcher.anyOf("qQ"));
    
    private static final Parser<Integer> QF_PARSER = new Parser<Integer>() {
        final Parser<Character> PERIOD_PARSER = Parsers.charParser('.');
        final Range<Integer> RANGE = Range.closed(0,1000);
        
        @Override
        public Optional<Integer> parse(final CharBuffer buffer) {        
            Preconditions.checkNotNull(buffer);
            final int startPos = buffer.position();
            
            final List<Optional<Object>> tokens =
                    Tokenizer.create(buffer)
                        .read(Parsers.DIGIT_PARSER) // 0
                        .read(PERIOD_PARSER) // 1
                        .read(Parsers.DIGIT_PARSER) // 2
                        .read(Parsers.DIGIT_PARSER) // 3
                        .read(Parsers.DIGIT_PARSER) // 4
                        .tokens();
            
            if (Optionals.isAbsent(tokens.get(0)) ||
                    (tokens.get(1).isPresent() && Optionals.isAbsent(tokens.get(2)))) {
                buffer.position(startPos);
                return Optional.absent();
            } 
            
            final int retval = 
                    ((Integer) tokens.get(0).get() * 1000) + 
                    ((Integer) tokens.get(2).or(0) * 100) +
                    ((Integer) tokens.get(3).or(0) * 10) +
                    (Integer) tokens.get(4).or(0);
            
            if (RANGE.contains(retval)) {
                return Optional.of(retval);
            }
            
            return Optional.absent();
        }        
    };
    
    /**
     * Returns the best match between the preferred list of preferences
     * and the available instances of T.
     * @param preferred
     * @param available
     * @return
     */
    public static <T extends Matcheable<T>> Optional<T> bestMatch(
            final Iterable<Preference<T>> preferred,
            final Iterable<T> available) {
        Preconditions.checkNotNull(preferred);
        Preconditions.checkNotNull(available);

        int prefScore = 0;
        T bestMatch = null;

        for (final Preference<T> preference : preferred) {
            for (final T availableT : available) {
                final int score = 
                        preference.value().match(availableT) * preference.qualityFactor() / 1000;
                if (score > prefScore) {
                    prefScore = score;
                    bestMatch = availableT;
                }
            }
        }

        return Optional.fromNullable(bestMatch);
    }
    
    /**
     * Creates a new Preference instance wrapping the {@code value} with a
     * quality factor of 1000.
     * @param value a non-null Matcheable.
     * @throws NullPointerException if {@code value} is null.
     */
    public static<T extends Matcheable<T>> Preference<T> create(final T value) {        
        return Preference.create(value, 1000, ImmutableListMultimap.<String, String> of());
    }

    /**
     * Creates a new Preference instance wrapping the {@code value} with the
     * given quality factor.
     * @param value a non-null Matcheable.
     * @param qualityFactor an integer between 0 and 1000
     * @throws NullPointerException if {@code value} is null.
     * @throws IllegalArgumentException if {@code qualityFactor} is not between
     * 0 and 1000.
     */
    public static<T extends Matcheable<T>> Preference<T> create(
            final T value, final int qualityFactor) {        
        return Preference.create(value, qualityFactor, ImmutableListMultimap.<String, String> of());
    }
    
    
    private static<T extends Matcheable<T>> Preference<T> create(
            final T value, final int qualityFactor, final Iterable<? extends Entry<String, String>> parameters) {
        return Preference.create(value, qualityFactor, GuavaCollectionHelpers.immutableListMultimapFromEntries(parameters, KeyValuePair.TO_LOWER_CASE_KEYS));
    }
    
    private static<T extends Matcheable<T>> Preference<T> create(
            final T value, final int qualityFactor, final Multimap<String, String> parameters) {
        Preconditions.checkNotNull(value);
        Preconditions.checkNotNull(parameters);
        Preconditions.checkArgument(qualityFactor >= 0 && qualityFactor <= 1000);       
        return new Preference<T>(value, qualityFactor, ImmutableListMultimap.copyOf(parameters));
    }
    
    static <T extends Matcheable<T>> Parser<Preference<T>> parser(
            final Parser<T> value, final Class<T> clss) {
        Preconditions.checkNotNull(value);
        Preconditions.checkNotNull(clss);
        
        return new Parser<Preference<T>>() {
            @Override
            public Optional<Preference<T>> parse(final CharBuffer buffer) {
                Preconditions.checkNotNull(buffer);
                final int startPos = buffer.position();
                final List<Optional<Object>> tokens = 
                        Tokenizer.create(buffer)
                            .read(value) // 0
                            .read(Primitives.OWS_SEMICOLON_OWS_PARSER) // 1
                            .read(Q_CHAR_PARSER) // 2
                            .readOptional(Primitives.WHITE_SPACE_PARSER) // 3
                            .read(EQUALS_CHAR_PARSER) // 4
                            .readOptional(Primitives.WHITE_SPACE_PARSER) // 5
                            .read(QF_PARSER) // 6
                            .readOptional(Primitives.WHITE_SPACE_PARSER) // 7
                            .readWhileAvailable(
                                    Primitives.OWS_SEMICOLON_OWS_PARSER, KeyValuePair.PARSER) // 8
                            .tokens();
                
                if (Optionals.isAbsent(tokens.get(0))) {
                    return Optional.absent();
                } 
                
                final T value = clss.cast(tokens.get(0).get());
                if (Optionals.isAbsent(tokens.get(2))) {
                    return Optional.of(Preference.create(value));
                }
                
                if (Optionals.isAbsent(tokens.get(6))) {
                    buffer.position(startPos);
                    return Optional.absent();
                }

                final int qualityFactor = (Integer) tokens.get(6).get();            
                final Iterable<KeyValuePair> parameters = 
                        FluentIterable
                            .from(Optional.presentInstances(tokens))
                            .skip(8)
                            .filter(KeyValuePair.class);                
                return Optional.of(Preference.create(value, qualityFactor, parameters));
            }           
        };
    }
    
    private final ImmutableMultimap<String, String> parameters;
    private final int qualityFactor;
    private final T value;
    
    private Preference(final T value, final int qualityFactor, final ImmutableMultimap<String, String> parameters) {
        this.qualityFactor = qualityFactor;
        this.value = value;
        this.parameters = parameters;    
    }
    
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Preference) {
            final Preference<?> that = (Preference<?>) obj;
            return (this.qualityFactor == that.qualityFactor) &&
                (this.value.equals(that.value)) &&
                (this.parameters.equals(that.parameters));
        } 
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects
                .hashCode(this.qualityFactor, this.value, this.parameters);
    }

    private String qfToString() {
       final char pos0 = (char) (((qualityFactor / 1000) % 10) + (int) '0');
       final char pos1 = (char) (((qualityFactor / 100) % 10) + (int) '0');
       final char pos2 = (char) (((qualityFactor / 10) % 10) + (int) '0');
       final char pos3 = (char) (((qualityFactor / 1) % 10) + (int) '0');
       
       final StringBuilder builder = new StringBuilder();
       builder.append(pos0);
       
       if ((pos1 == '0') && (pos2 == '0') && (pos3 == '0')) {
           return builder.toString();
       }
       
       builder.append('.').append(pos1);
       
       if ((pos2 == '0') && (pos3 == '0')) {
           return builder.toString();
       }
       
       builder.append(pos2);
       
       if (pos3 == '0') {
           return builder.toString();
       }
       
       return builder.append(pos3).toString();
    }
    
    /**
     * Returns the preference quality factor as an integer 
     * between 0 and 1000 inclusive.
     */
    public int qualityFactor() {
        return qualityFactor;
    }

    @Override
    public String toString() {
         return this.value.toString() + "; q=" + qfToString() + 
            (this.parameters.isEmpty() ? "" : "; " + 
                    KeyValuePair.toString(this.parameters.entries()));
    }
    
    /**
     * Returns the preference value.
     */
    public T value() {
        return value;
    }
}
