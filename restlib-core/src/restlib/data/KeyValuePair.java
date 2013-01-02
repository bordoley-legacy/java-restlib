package restlib.data;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Map.Entry;

import restlib.impl.ImmutableMapEntry;
import restlib.impl.Optionals;
import restlib.impl.Parser;
import restlib.impl.Parsers;
import restlib.impl.Tokenizer;

import com.google.common.base.Ascii;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingMapEntry;

class KeyValuePair extends ForwardingMapEntry<String,String> {    
    static final Function<Entry<String,String>, Entry<String,String>> CHECK_NOT_Q = 
           new Function<Entry<String,String>, Entry<String,String>>() {
                @Override
                public Entry<String, String> apply(final Entry<String, String> input) {
                    Preconditions.checkArgument(!Ascii.toLowerCase(input.getKey()).equals("q"));
                    return input;
                }      
    };
    
    static final Parser<KeyValuePair> KEY_NOT_Q_PARSER  = new Parser<KeyValuePair>() {        
        @Override
        public Optional<KeyValuePair> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
                    
            final int startPos = buffer.position();
            final Optional<KeyValuePair> kvOptional = PARSER.parse(buffer);
            
            if (kvOptional.isPresent()) {
                final KeyValuePair kv = kvOptional.get();
                if (Ascii.toLowerCase(kv.getKey()).equals("q")) {
                    buffer.position(startPos);
                    return Optional.absent();
                } else {
                    return kvOptional;
                }
            } else {
                return Optional.absent();
            }
        } 
    };
    
    static final Parser<KeyValuePair> PARSER = new Parser<KeyValuePair>() {
        final Parser<Character> EQUALS_CHAR_PARSER = Parsers.charParser('=');
        
        @Override
        public Optional<KeyValuePair> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final int startPos = buffer.position();
            final List<Optional<Object>> tokens = 
                    Tokenizer.create(buffer)
                        .read(Primitives.TOKEN_PARSER) // 0
                        .readOptional(Primitives.WHITE_SPACE_PARSER) // 1
                        .read(EQUALS_CHAR_PARSER) // 2
                        .readOptional(Primitives.WHITE_SPACE_PARSER) // 3
                        .read(Primitives.WORD_PARSER) // 4
                        .tokens();
            
            if (tokens.get(0).isPresent()) {
                final String key = Optionals.toString(tokens.get(0));
                
                if (tokens.get(2).isPresent() && tokens.get(4).isPresent()) {
                     return Optional.of(KeyValuePair.create(key, Optionals.toString(tokens.get(4))));
                } else if (Optionals.isAbsent(tokens.get(2)) && Optionals.isAbsent(tokens.get(4))) {
                     return Optional.of(KeyValuePair.create(key,""));
                }

            } 
            
            buffer.position(startPos);
            return Optional.absent();
        } 
    }; 
    
    static final Function<Entry<String,String>, Entry<String,String>> TO_LOWER_CASE_KEYS =
            new Function<Entry<String,String>, Entry<String,String>> () {
                @Override
                public Entry<String, String> apply(final Entry<String, String> input) {
                    Preconditions.checkNotNull(input);                    
                    return ImmutableMapEntry.create(
                            Ascii.toLowerCase(input.getKey()), input.getValue());
                }      
    };
    
    static final Function<Entry<String,String>, Entry<String,String>> TO_LOWER_CASE_KEY_VALUES =
            new Function<Entry<String,String>, Entry<String,String>> () {
                @Override
                public Entry<String, String> apply(final Entry<String, String> input) {
                    Preconditions.checkNotNull(input);                    
                    return ImmutableMapEntry.create(
                            Ascii.toLowerCase(input.getKey()), 
                            Ascii.toLowerCase(input.getValue()));
                }      
    };
    
    static final Function<Entry<String,String>, Entry<String,String>> VALIDATE =
            new Function<Entry<String,String>, Entry<String,String>> () {
        @Override
        public Entry<String, String> apply(final Entry<String, String> input) {
            Preconditions.checkNotNull(input);
            Preconditions.checkArgument(Primitives.IS_TOKEN.apply(input.getKey()));
            Preconditions.checkArgument(Primitives.IS_WORD.apply(input.getValue()));
            
            return input;
        }      
    };
    
    static final Function<Entry<String,String>, Entry<String,String>> VALIDATE_TO_LOWER_CASE_KEYS =
            Functions.compose(VALIDATE, TO_LOWER_CASE_KEYS);
    
    static final Function<Entry<String,String>, Entry<String,String>> VALIDATE_TO_LOWER_CASE_KEY_NOT_Q =
            Functions.compose(Functions.compose(VALIDATE, CHECK_NOT_Q), TO_LOWER_CASE_KEYS);
    
    static final Function<Entry<String,String>, Entry<String,String>> VALIDATE_TO_LOWER_CASE_KEY_VALUES_NOT_Q =
            Functions.compose(Functions.compose(VALIDATE, CHECK_NOT_Q), TO_LOWER_CASE_KEY_VALUES);
    
    static KeyValuePair create(final String key, final String value) {
        Preconditions.checkArgument(Primitives.IS_TOKEN.apply(key));
        Preconditions.checkArgument(Primitives.IS_WORD.apply(value));
        return new KeyValuePair(key, value);
    }
    
    static String toString(Iterable<? extends Entry<String,String>> entries) {
        Preconditions.checkNotNull(entries);
        
        final StringBuilder builder = new StringBuilder();                   
        for (final Entry<?, ?> entry : entries) {
            builder.append(entry.getKey().toString());
                      
            final String value = entry.getValue().toString();
            if (!value.isEmpty()) {
                builder.append('=').append(Primitives.encodeWord(value));
            } builder.append(';');
        }
        
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();  
    }
    
    private final Entry<String, String> delegate;
    
    private KeyValuePair(final String key, final String value) {
        delegate = ImmutableMapEntry.create(key, value);
    }

    @Override
    protected Entry<String, String> delegate() {
        return delegate;
    }
}