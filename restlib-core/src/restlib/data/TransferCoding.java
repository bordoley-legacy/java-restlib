package restlib.data;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.impl.GuavaCollectionHelpers;
import restlib.impl.Optionals;
import restlib.impl.Parser;
import restlib.impl.Parsers;
import restlib.impl.Registry;
import restlib.impl.Tokenizer;

import com.google.common.base.Ascii;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * An encoding transformation that has been, can be, or might need to be applied to
 * a HTTP payload body in order to ensure "safe transport" through a network.
 */
@Immutable
public final class TransferCoding implements Matcheable<TransferCoding> {
    private static final Registry<TransferCoding> _REGISTERED = new Registry<TransferCoding>();
              
    /**
     * The transfer-coding wildcard.
     */
    public static final TransferCoding ANY = register(create("*"));  
    
    /**
     * The chunked transfer-coding.
     */
    public static final TransferCoding CHUNKED = register(create("chunked"));
    
    /**
     * The compress transfer-coding.
     */
    public static final TransferCoding COMPRESS = register(create("compress"));    
    
    /**
     * The deflate transfer-coding
     */
    public static final TransferCoding DEFLATE = register(create("deflate"));
    
    /**
     * The gzip transfer-coding.
     */
    public static final TransferCoding GZIP = register(create("gzip")); 
    
    static final Parser<TransferCoding> PARSER = new Parser<TransferCoding>() {
        @Override
        public Optional<TransferCoding> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            
            final List<Optional<Object>> tokens = 
                    Tokenizer.create(buffer)
                        .read(Primitives.TOKEN_PARSER) // 0
                        .readWhileAvailable(
                                Primitives.OWS_SEMICOLON_OWS_PARSER, KeyValuePair.KEY_NOT_Q_PARSER) // 2
                        .tokens();
            
            if (Optionals.isAbsent(tokens.get(0))) {
                return Optional.absent();
            } else {
                final String token = Optionals.toString(tokens.get(0));
                final Iterable<KeyValuePair> params = 
                        Iterables.filter(Optional.presentInstances(tokens), KeyValuePair.class);

                return Optional.of(TransferCoding.create(token, params));
            }
        }    
    };
    
    /**
     * The trailers transfer-coding.
     */
    public static final TransferCoding TRAILERS = register(create("trailers"));

    /**
     * Creates a new transfer coding.
     * @param token an HTTP token string.
     * @throws NullPointerException if {@code token} is null.
     * @throws IllegalArgumentException if {@code token} if not a valid HTTP token.
     */
    static TransferCoding create(final String token) {        
        return TransferCoding.create(token, ImmutableListMultimap.<String, String> of());
    }
    
    static TransferCoding create(final String token, 
            final Iterable<? extends Map.Entry<String, String>> parameters) {
        Preconditions.checkArgument(Primitives.IS_TOKEN.apply(token));   
        final TransferCoding transferCoding = 
                new TransferCoding(
                        Ascii.toLowerCase(token), 
                        GuavaCollectionHelpers.immutableSetMultimapFromEntries(
                                parameters, KeyValuePair.VALIDATE_TO_LOWER_CASE_KEY_VALUES_NOT_Q));   
        return _REGISTERED.getIfPresent(transferCoding);
    }    
    
    /**
     * Creates a new transfer coding.
     * @param token an HTTP token string.
     * @param parameters a multimap of key value pairs where keys 
     * are valid HTTP tokens and values are valid HTTP words.
     * @throws NullPointerException if {@code token} or {@code parameters} are null, 
     *  or if any key or value in {@code parameters} is null.
     * @throws IllegalArgumentException if {@code token} is not a valid HTTP token. Also
     * if any key in {@code parameters} is not a valid token or if any value in 
     * {@code parameters} is not a valid HTTP word. Also if any key in {@code parameters} equals either "q" or "Q".
     */
    static TransferCoding create(final String token, 
            final Multimap<String, String> parameters) {
        Preconditions.checkNotNull(parameters);     
        return TransferCoding.create(token, parameters.entries());       
    }
    
    static TransferCoding parse(final CharSequence in) {
        return Parsers.parseWithParser(in, PARSER);
    }
    
    private static TransferCoding register(final TransferCoding transferCoding) {
        return _REGISTERED.register(transferCoding);
    }
    
    private final ImmutableSetMultimap<String, String> parameters;
    private final String value;
    
    private TransferCoding(
            final String value,
            final ImmutableSetMultimap<String, String> parameters) {
        this.value = value;
        this.parameters = parameters;
    }
    
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof TransferCoding) {
            final TransferCoding that = (TransferCoding) obj;
            return this.value.equals(that.value) &&
                    this.parameters.equals(that.parameters);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.value, this.parameters);
    }
    
    @Override
    public int match(final TransferCoding that) {
        Preconditions.checkNotNull(that);
        
        if (this.equals(that)) {
            return 1000;
        } else if (this.equals(TransferCoding.ANY)) {
            return 500;
        } else if (!this.value.equals(that.value)) {
            return 0;            
        } else if (this.parameters.entries().containsAll(that.parameters.entries())) {
            return 700;
        } else {
            return 0;
        }
    }
    
    /**
     * Returns the transfer coding parameters.
     * <p> Note: All keys and values in the multimap are lower case to preserve case-insensitivity.
     */
    public Multimap<String, String> parameters() {
        return this.parameters;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.value);
        
        if (!this.parameters.isEmpty()) {
            builder.append("; ").append(KeyValuePair.toString(this.parameters.entries()));
        }
        
        return builder.toString();
    }
    
    /**
     * Returns the transfer coding value.
     * <p> Note: The transfer-coding value is always lower case to preserve case-insensitivity.
     */
    public String value() {
        return this.value;
    }
}
