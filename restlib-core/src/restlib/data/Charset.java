package restlib.data;

import java.nio.CharBuffer;
import java.nio.charset.IllegalCharsetNameException;

import javax.annotation.Nullable;

import restlib.impl.CaseInsensitiveString;
import restlib.impl.Parser;
import restlib.impl.Registry;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * Representation of an HTTP charset token.
 */
public final class Charset implements Matcheable<Charset> {
    private static final Registry<Charset> _REGISTERED = new Registry<Charset>();
    
    public static final Charset ANY = register(create("*"));
    public static final Charset ISO_8859_1 = register(fromNioCharset(Charsets.ISO_8859_1));
    
    static final Parser<Charset> PARSER = new Parser<Charset>() {
        @Override
        public Optional<Charset> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final Optional<String> token = Primitives.TOKEN_PARSER.parse(buffer);
            if (token.isPresent()) {
                return Optional.of(Charset.create(token.get()));
            } else {
                return Optional.absent();
            }
        }     
    };
    
    public static final Charset US_ASCII  = register(fromNioCharset(Charsets.US_ASCII));
    public static final Charset UTF_16 = register(fromNioCharset(Charsets.UTF_16));
    public static final Charset UTF_16BE = register(fromNioCharset(Charsets.UTF_16BE));
    public static final Charset UTF_16LE = register(fromNioCharset(Charsets.UTF_16LE));
    public static final Charset UTF_8 = register(fromNioCharset(Charsets.UTF_8));
    
    
    public static Iterable<Charset> available() {
        return _REGISTERED.registered();
    }
    
    /**
     * Returns a new Charset.
     * @param charset a non-null HTTP token
     * @throws NullPointerException if {@code charset} is null.
     * @throws IllegalArgumentException if {@code charset} is not a valid HTTP token.
     */
    public static Charset create(final String charset)  {
        Preconditions.checkNotNull(charset);        
        Preconditions.checkArgument(Primitives.IS_TOKEN.apply(charset));   
        return _REGISTERED.getIfPresent(new Charset(CaseInsensitiveString.wrap(charset)));
    }
    
    /**
     * Returns a Charset instance that wraps an instance of {@link java.nio.charset.Charset}.
     * @param charset a non-null {@link java.nio.charset.Charset}.
     * @throws NullPointerException if {@code charset} is null.
     */
    public static Charset fromNioCharset(final java.nio.charset.Charset charset) {
        Preconditions.checkNotNull(charset);
        return Charset.create(charset.toString());
    }
    
    private static Charset register(final Charset charset) {
        return _REGISTERED.register(charset);
    }
    
    private final CaseInsensitiveString value;
    
    private Charset(final CaseInsensitiveString value){
        this.value = value;
    }
    
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Charset) {
            final Charset that = (Charset) obj;
            return this.value.equals(that.value);
        }      
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }
    
    @Override
    public int match(final Charset that) {
        Preconditions.checkNotNull(that);
        if (this.equals(that)) {
            return 1000;
        } else if (this.equals(Charset.ANY)) {
            return 500;
        }
        return 0;
    }
    
    /**
     * Returns this charset as an instance of {@link java.nio.charset.Charset}..
     * @throws IllegalStateException if this Charset cannot be
     * represented as a {@link java.nio.charset.Charset}.
     */
    public java.nio.charset.Charset toNioCharset() {
        try {
            return java.nio.charset.Charset.forName(this.toString());
        } catch (final IllegalCharsetNameException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public String toString() {
        return this.value.toString();
    }
}
