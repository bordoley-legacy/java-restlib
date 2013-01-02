package restlib.impl;

import java.nio.CharBuffer;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

@NotThreadSafe
public final class Tokenizer { 
    private static final Parser<?>[] EMPTY_PARSER_ARRAY = new Parser[0];
    
    public static Tokenizer create(final CharBuffer buffer) {
        Preconditions.checkNotNull(buffer);
        return new Tokenizer(buffer);
    }
    
    private boolean readAdditional = true;
    private final CharBuffer buffer;   
    private final List<Optional<Object>> tokens = Lists.newArrayList();  
    
    private Tokenizer(final CharBuffer buffer) {
        this.buffer = buffer;
    }
    
    public Tokenizer read(final Parser<?> parser) {
        if (!readAdditional) {
            tokens.add(Optional.absent());
            return this;
        }
        
        @SuppressWarnings("unchecked")
        final Optional<Object> o = (Optional<Object>) parser.parse(buffer);
        if (Optionals.isAbsent(o)) {
            readAdditional = false;
        }
        
        tokens.add(o);
        return this;
    }
    
    public Tokenizer readOptional(final Parser<?> parser) {
        if (!readAdditional) {
            tokens.add(Optional.absent());
            return this;
        }
        
        @SuppressWarnings("unchecked")
        final Optional<Object> o = (Optional<Object>) parser.parse(buffer);        
        tokens.add(o);
        return this;
    }
    
    public Tokenizer readWhileAvailable(final Parser<?> parser) {
        return readWhileAvailable(parser, EMPTY_PARSER_ARRAY);
    }
    
    public Tokenizer readWhileAvailable(final Parser<?> parser, final Parser<?>...others) {
        Preconditions.checkNotNull(parser);
        Preconditions.checkNotNull(others);
        while(true) {
            final int startPos = buffer.position();
            @SuppressWarnings("unchecked")
            final Optional<Object> o = (Optional<Object>) parser.parse(buffer);
            if (Optionals.isAbsent(o)) {
                readAdditional = false;
                break;
            }
            
            tokens.add(o);
            
            for (final Parser<?> other: others) {
                @SuppressWarnings("unchecked")
                final Optional<Object> o2 = (Optional<Object>) other.parse(buffer);
                if (Optionals.isAbsent(o2)) {
                    readAdditional = false;     
                    break;
                }
                
                tokens.add(o2);
            }
            
            if (!readAdditional) {
                buffer.position(startPos);
                break;
            }
        }
        
        return this;
    }
    
    public List<Optional<Object>> tokens() {
        return Collections.unmodifiableList(this.tokens);
    }
}
