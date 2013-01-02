package restlib.data;





import java.nio.CharBuffer;
import java.util.List;

import restlib.impl.CharBuffers;
import restlib.impl.CharSequences;
import restlib.impl.Optionals;
import restlib.impl.Parser;
import restlib.impl.Parsers;
import restlib.impl.Tokenizer;
import restlib.net.HostPort;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

final class Primitives {
    private static final char DQUOTE_CHAR = '"';
    private static final char ESCAPE_CHAR = '\\';
    
    private static final Parser<HostPort> HOST_PORT_PARSER = new Parser<HostPort>() {
        final Parser<String> parser = Parsers.whileMatchesParser(CharMatchers.HOST_PORT);
        @Override
        public Optional<HostPort> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final int startPos = buffer.position();
            final Optional<String> hostPort = parser.parse(buffer);
            if (hostPort.isPresent()) {
                try {            
                    return Optional.of(HostPort.parse(hostPort.get()));
                } catch (final IllegalArgumentException e) {
                    buffer.position(startPos);
                    return Optional.absent();
                }
            } else {
                return Optional.absent();
            }
        }      
    };
    
    static final Predicate<CharSequence> IS_BASE_64 = new Predicate<CharSequence>() {
        private final CharMatcher EQUALS_SIGN_MATCHER = CharMatcher.is('=');
        @Override
        public boolean apply(final CharSequence input) {
            if (CharSequences.isEmpty(input)) {
                return false;
            } else {
                return CharMatchers.B64_MATCHER.matchesAllOf(EQUALS_SIGN_MATCHER.trimTrailingFrom(input));
            }
        }         
    };
    
    static final Predicate<CharSequence> IS_ETAG_CHARACTER = new Predicate<CharSequence>() {
        @Override
        public boolean apply(final CharSequence input) {
            return CharMatchers.ETAGC_MATCHER.matchesAllOf(input);
        }         
    };
    
    static final Predicate<CharSequence> IS_QUOTABLE = new Predicate<CharSequence>() {
        @Override
        public boolean apply(final CharSequence in) {
            try {
                encodeQuotedString (in.toString());
            } catch (final IllegalArgumentException e) {
                return false;
            }
            return true;
        }
    };
    
    static final Predicate<CharSequence> IS_TOKEN = new Predicate<CharSequence>() {
        @Override
        public boolean apply(final CharSequence input) {
            if (CharSequences.isEmpty(input)) {
                return false;
            }
            return CharMatchers.TCHAR_MATCHER.matchesAllOf(input);
        }         
    };
    
    static final Predicate<CharSequence> IS_WORD =
            Predicates.<CharSequence> or(IS_TOKEN, IS_QUOTABLE);
 
    static final Parser<String> OWS_COMMA_OWS_PARSER = new Parser<String>() {
        final Joiner joiner = Joiner.on("");
        
        @Override
        public Optional<String> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final List<Optional<Object>> tokens = 
                    Tokenizer.create(buffer)
                         .readOptional(WHITE_SPACE_PARSER) // 0
                         .read(Parsers.charParser(',')) // 1
                         .readOptional(WHITE_SPACE_PARSER) // 2
                         .tokens();
            
            return Optional.of(joiner.join(Optional.presentInstances(tokens)));
        }    
    };
    
    static final Parser<String> OWS_SEMICOLON_OWS_PARSER = new Parser<String>() {
        final Joiner joiner = Joiner.on("");
        
        @Override
        public Optional<String> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final List<Optional<Object>> tokens = 
                    Tokenizer.create(buffer)
                         .readOptional(WHITE_SPACE_PARSER) // 0
                         .read(Parsers.charParser(';'))         // 1
                         .readOptional(WHITE_SPACE_PARSER) // 2
                         .tokens();
            
            return Optional.of(joiner.join(Optional.presentInstances(tokens)));
        }    
    };
    
    static final Parser<String> QUOTED_STRING_PARSER = new Parser<String>() {
        @Override
        public Optional<String> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final int startPos = buffer.position();
    
            if (startPos >= buffer.capacity()) {
                return Optional.absent();
            }
            
            if (buffer.get() != DQUOTE_CHAR) {
                CharBuffers.pushback(buffer);
                return Optional.absent();
            }
            
            final StringBuilder retval =
                    new StringBuilder();
            
            while(buffer.position() < buffer.capacity()) {
                final char c = buffer.get();
                
                if (CharMatchers.QD_TEXT_MATCHER.apply(c)) {
                    retval.append(c);
                } else if (c == ESCAPE_CHAR) {             
                    if (buffer.position() >= buffer.capacity()) {
                        buffer.position(startPos);
                        return Optional.absent();
                    }
                            
                    final char next = buffer.get();                
                    if (CharMatchers.QUOTED_PAIR_CHAR_MATCHER.apply(next)) {
                        retval.append(next);
                    } else {
                        buffer.position(startPos);
                        return Optional.absent();
                    }
                } else if (c == DQUOTE_CHAR) {
                    return Optional.of(retval.toString());
                } else {
                    buffer.position(startPos);
                    return Optional.absent();
                }
            }
            
            buffer.position(startPos);
            return Optional.absent();
        }    
    };
    
    static final Parser<String> TOKEN_PARSER = Parsers.whileMatchesParser(CharMatchers.TCHAR_MATCHER);
    
    static final Parser<String> WHITE_SPACE_PARSER = Parsers.whileMatchesParser(CharMatchers.WHITE_SPACE_MATCHER);
    
    static final Parser<String> WORD_PARSER = new Parser<String>() {
        final Parser<Object> parser = Parsers.firstAvailableParser(TOKEN_PARSER, QUOTED_STRING_PARSER);
        @Override
        public Optional<String> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final Optional<Object> result = parser.parse(buffer);
            if (Optionals.isAbsent(result)) {
                return Optional.absent();
            } else {
                return Optional.of((String) result.get());
            }
        }        
    };
    
    static final Parser<?> HOST_PORT_OR_PSEUDONYM = 
            Parsers.firstAvailableParser(
                    Primitives.HOST_PORT_PARSER, Primitives.TOKEN_PARSER);
    
    static String encodeQuotedString(final String in) {
        Preconditions.checkNotNull(in);
        final StringBuilder retval = new StringBuilder(in.length() * 2 + 2);

        retval.append(DQUOTE_CHAR);
        for (int i = 0; i < in.length(); i++) {
            final char c = in.charAt(i);
            if (CharMatchers.QD_TEXT_MATCHER.matches(c)) {
                retval.append(c);
            } else if (c == DQUOTE_CHAR || c == ESCAPE_CHAR) {
                retval.append(ESCAPE_CHAR).append(c);
            } else {
                throw new IllegalArgumentException();
            }
        }

        retval.append(DQUOTE_CHAR);
        return retval.toString();
    }

    static String encodeWord(String in) {
        if (IS_TOKEN.apply(in)) {
            return in;
        } else {
            return Primitives.encodeQuotedString(in);
        }
    }
}
