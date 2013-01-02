package restlib.impl;

import java.nio.CharBuffer;
import java.util.List;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;

/**
 * Static Factory for obtaining {@code Parser} instances.
 */
public final class Parsers {
    public static final Parser<Integer> DIGIT_PARSER = new Parser<Integer>() {
        final Range<Character> DIGIT_RANGE = Range.closed('0', '9');
 
        @Override
        public Optional<Integer> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final int startPos = buffer.position();
            if (!buffer.hasRemaining()) {
                return Optional.absent();
            }
            
            final char c = buffer.get();
            if (DIGIT_RANGE.contains(c)) {
                return Optional.of((int)c - (int)'0');
            } else {
                buffer.position(startPos);
                return Optional.absent();
            }           
        }
        
    };

    public static final Parser<Integer> INTEGER_PARSER = new Parser<Integer>() {
        final Parser<String> parser = Parsers.whileMatchesParser(CharMatcher.inRange('0','9'));
        
        @Override
        public Optional<Integer> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final Optional<String> integer = parser.parse(buffer);
            if (integer.isPresent()) {
                return Optional.of(CommonParsers.parseUnsignedInteger(integer.get()));
            } else {
                return Optional.absent();
            }
        }        
    };
    
    public static final Parser<Long> LONG_PARSER = new Parser<Long>() {
        final Parser<String> parser = Parsers.whileMatchesParser(CharMatcher.inRange('0','9'));
        
        @Override
        public Optional<Long> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final Optional<String> longStr = parser.parse(buffer);
            if (longStr.isPresent()) {
                return Optional.of(CommonParsers.parseUnsignedLong(longStr.get()));
            } else {
                return Optional.absent();
            }
        }        
    };
    
    public static Parser<Character> characterPredicateParser(final Predicate<Character> predicate) {
        Preconditions.checkNotNull(predicate);
        
        return new Parser<Character> () {
            @Override
            public Optional<Character> parse(final CharBuffer buffer) {
                Preconditions.checkNotNull(buffer);
                
                if (!buffer.hasRemaining()) {
                    return Optional.absent();
                } 
                
                final char c = buffer.get();
                if (predicate.apply(c)) {
                    return Optional.of(c);
                } else {
                    return Optional.absent();
                }
            }           
        };
    }
    
    public static Parser<Character> charParser(final char c) {
        return new Parser<Character> () {
            @Override
            public Optional<Character> parse(final CharBuffer buffer) {
                Preconditions.checkNotNull(buffer);
                
                if (!buffer.hasRemaining()) {
                    return Optional.absent();
                } else if (buffer.get() == c) {
                    return Optional.<Character> of(c);
                } else {
                    CharBuffers.pushback(buffer);
                    return Optional.absent();
                }
            }          
        };
    }
    
    public static Parser<Object> firstAvailableParser(
            final Parser<?> first, 
            final Parser<?> second) {
        Preconditions.checkNotNull(first);
        Preconditions.checkNotNull(second);
        
        return new Parser<Object>() {     
            @Override
            public Optional<Object> parse(final CharBuffer buffer) {
                Preconditions.checkNotNull(buffer);
                
                @SuppressWarnings("unchecked")
                final Optional<Object> o1 = (Optional<Object>) first.parse(buffer);
                if (o1.isPresent()) {
                    return  o1;
                }
                
                @SuppressWarnings("unchecked")
                final Optional<Object> o2 = (Optional<Object>) second.parse(buffer);
                if (o2.isPresent()) {
                    return o2;
                }               
                
                return Optional.absent();
            }           
        };
    } 
    
    public static <T> Parser<Iterable<T>> listParser(
            final Parser<T> itemParser, 
            final Parser<?> seperatorParser, 
            final Class<T> itemClass) {
        Preconditions.checkNotNull(itemParser);
        Preconditions.checkNotNull(seperatorParser);
        Preconditions.checkNotNull(itemClass);
        
        return new Parser<Iterable<T>>() {
            final Function<Object,T> cast =
                    new Function<Object,T>() {
                        @SuppressWarnings("unchecked")
                        @Override
                        public T apply(final Object input) {
                            return (T) input;
                        }                        
                    };
        
            final Parser<Iterable<?>> parser = untypedListParser(itemParser, seperatorParser, itemClass);
                
            @Override
            public Optional<Iterable<T>> parse(final CharBuffer buffer) {
                final Optional<Iterable<?>> parsed = parser.parse(buffer);
                if (Optionals.isAbsent(parsed)) {
                    return Optional.absent();
                }
                return Optional.of(Iterables.transform(parsed.get(), cast));
            }      
        };
    }
    
    public static <T> T parseWithParser(final CharSequence in, final Parser<T> parser) {
        Preconditions.checkNotNull(in);
        Preconditions.checkNotNull(parser);
        
        final CharBuffer buffer = CharBuffer.wrap(in);
        final Optional<T> retval = parser.parse(buffer);
        
        Preconditions.checkArgument(retval.isPresent());
        Preconditions.checkArgument(!buffer.hasRemaining());
        
        return retval.get();
    }

    public static Parser<String> stringParser(final String in) {
        Preconditions.checkNotNull(in);
        
        return new Parser<String>() {
            @Override
            public Optional<String> parse(final CharBuffer buffer) {
                Preconditions.checkNotNull(buffer);
                
                final int startPos = buffer.position();
                
                for (final char c : CharSequences.characters(in)) {
                    if (buffer.hasRemaining() &&
                            (buffer.get() == c)) {
                        continue;
                    } else {
                        buffer.position(startPos);
                        return Optional.absent();
                    }                   
                }
                return Optional.of(in);
            }          
        };
    }
    
    public static Parser<Iterable<?>> untypedListParser(
            final Parser<?> itemParser, 
            final Parser<?> seperatorParser, 
            final Class<?> itemClass) {
        Preconditions.checkNotNull(itemParser);
        Preconditions.checkNotNull(seperatorParser);
        Preconditions.checkNotNull(itemClass);
        
        return new Parser<Iterable<?>>() {
            @Override
            public Optional<Iterable<?>> parse(final CharBuffer buffer) {
                Preconditions.checkNotNull(buffer);
                
                final List<Optional<Object>> tokens =
                        Tokenizer.create(buffer)
                                    .read(itemParser)
                                    .readWhileAvailable(seperatorParser, itemParser)
                                    .tokens();
                final Iterable<?> items = Iterables.filter(Optional.presentInstances(tokens), itemClass);
                if (Iterables.isEmpty(items)) {
                    return Optional.absent();
                }
                return Optional.<Iterable<?>> of(items);
            }        
        };
    }
    
    public static Parser<String> whileMatchesParser(final Predicate<Character> matcher) {
        Preconditions.checkNotNull(matcher);
        
        return new Parser<String>() {
            @Override
            public Optional<String> parse(final CharBuffer buffer) {
                Preconditions.checkNotNull(buffer);
                final StringBuilder builder = new StringBuilder();
                
                while (buffer.hasRemaining()) {
                    final char c = buffer.get();
                    if (matcher.apply(c)) {
                        builder.append(c);
                    } else {
                        CharBuffers.pushback(buffer);
                        break;
                    }
                }     
                
                if (CharSequences.isEmpty(builder)) {
                    return Optional.absent();
                }
                
                return Optional.of(builder.toString());
            }           
        };
    }
    
    private Parsers(){}
}
