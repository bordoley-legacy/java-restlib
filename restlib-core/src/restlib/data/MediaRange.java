package restlib.data;

import java.nio.CharBuffer;
import java.util.List;

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
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * Represents an Internet Media Type (also known as a MIME Type or Content Type). 
 * This class also supports the concept of media ranges defined by HTTP/1.1. 
 */
@Immutable
public final class MediaRange implements Matcheable<MediaRange>{  
    private static final Registry<MediaRange> _REGISTERED = new Registry<MediaRange>();
    
    /**
     * The wildcard {@link MediaRange} "*&#47;*" that matches any {@link MediaRange}.
     */
    public static final MediaRange ANY = register(create("*","*"));
    
    static final Parser<MediaRange> PARSER = new Parser<MediaRange>() {
        @Override
        public Optional<MediaRange> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final int startPos = buffer.position();
            final List<Optional<Object>> tokens = 
                    Tokenizer.create(buffer)
                        .read(Primitives.TOKEN_PARSER) // 0
                        .read(Parsers.charParser('/')) // 1
                        .read(Primitives.TOKEN_PARSER) // 2
                        .readWhileAvailable(
                                Primitives.OWS_SEMICOLON_OWS_PARSER, 
                                KeyValuePair.KEY_NOT_Q_PARSER) // 3
                        .tokens();
            
            if (Optionals.isAbsent(tokens.get(0)) || Optionals.isAbsent(tokens.get(2))) {
                buffer.position(startPos);
                return Optional.absent();
            } else {           
                final String type = Ascii.toLowerCase(tokens.get(0).get().toString());
                final String subtype = Ascii.toLowerCase(tokens.get(2).get().toString());
                Optional<Charset> charset = Optional.absent();
                final ImmutableSetMultimap.Builder<String, String> parameters = ImmutableSetMultimap.builder();
                for (final KeyValuePair kvp : Iterables.filter(Optional.presentInstances(tokens), KeyValuePair.class)) {
                    if (Ascii.toLowerCase(kvp.getKey()).equals("charset")) {
                        if (charset.isPresent()) {
                            buffer.position(startPos);
                            return Optional.absent();
                        }
                        
                        try {
                            charset = Optional.of(Charset.create(kvp.getValue()));
                        } catch (final IllegalArgumentException e) {
                            buffer.position(startPos);
                            return Optional.absent();
                        }
                        
                        if (charset.get().equals(Charset.ANY)) {
                            buffer.position(startPos);
                            return Optional.absent();
                        }
                        
                        continue;
                    }
                    parameters.put(KeyValuePair.TO_LOWER_CASE_KEYS.apply(kvp));
                }

                return Optional.of(MediaRange.create(type, subtype, charset, parameters.build()));
            }
        }       
    };
    
    /**
     * Returns a new {@link MediaRange} with the given type and subtype .
     * @param type a non-null HTTP token.
     * @param subtype a non-null HTTP token.
     * @throws NullPointerException if {@code type}, {@code subtype} is null.
     * @throws IllegalArgumentException if {@code type} or {@code subtype} are not valid HTTP tokens.
     */
    public static MediaRange create(
            final String type, final String subtype) {
        return MediaRange.create(type, subtype, Optional.<Charset>absent(), ImmutableMultimap.<String,String> of());
    }
    
    /**
     * Returns a new {@link MediaRange} with the given type, subtype and charset.
     * @param type a non-null HTTP token.
     * @param subtype a non-null HTTP token.
     * @param charset any non-null {@link Charset} instance except {@link Charset.ANY}.
     * @throws NullPointerException if {@code type}, {@code subtype}, {@code charset} is null.
     * @throws IllegalArgumentException if {@code type} or {@code subtype} are not valid HTTP tokens 
     * or are wildcards, or if {@code charset} is equal to {@link Charset.ANY}.  
     */
    public static MediaRange create(
            final String type, final String subtype, final Charset charset) {
        return MediaRange.create(type, subtype, charset, ImmutableMultimap.<String,String> of());
    }
    
    /**
     * Returns a new {@link MediaRange} with the given type, subtype, charset, and parameters.
     * @param type a non-null HTTP token.
     * @param subtype a non-null HTTP token.
     * @param charset any non-null {@link Charset} instance except {@link Charset.ANY}.
     * @param parameters a multimap of non-null HTTP tokens to HTTP words, where no 
     * key is case-insensitively equal to the strings "q" or "charset".
     * @throws NullPointerException if {@code type}, {@code subtype}, {@code charset}, or {@code parameters} are null.
     * Also if {@code parameters} contains any null keys or values.
     * @throws IllegalArgumentException if {@code type} or {@code subtype} are not valid HTTP tokens or are wildcards, 
     * or if {@code charset} is equal to {@link Charset.ANY}, or if any key in {@code parameters} is a 
     * not a valid HTTP token, or if any value in {@code parameters} is not a valid HTTP word, or if any 
     * key is case-insensitively equals to the strings "q" or "charset".  
     */
    public static MediaRange create(
            final String type, final String subtype, 
            final Charset charset, final Multimap<String,String> parameters) {
        return MediaRange.create(type, subtype, Optional.of(charset), parameters);
    }

    /**
     * Returns a new {@link MediaRange} with the given type, subtype and parameters.
     * @param type a non-null HTTP token.
     * @param subtype a non-null HTTP token.
     * @param parameters a multimap of non-null HTTP tokens to HTTP words, where no 
     * key is case-insensitively equal to the strings "q" or "charset".
     * @throws NullPointerException if {@code type}, {@code subtype}, or {@code parameters} are null.
     * Also if {@code parameters} contains any null keys or values.
     * @throws IllegalArgumentException if {@code type} or {@code subtype} are not valid HTTP tokens or are wildcards, 
     * or if any key in {@code parameters} is a not a valid HTTP token, or if any value in
     * {@code parameters} is not a valid HTTP word, or if any key is case-insensitively equals to the strings
     * "q" or "charset".  
     */
    public static MediaRange create(
            final String type, final String subtype, 
            final Multimap<String,String> parameters) {
        return MediaRange.create(type, subtype, Optional.<Charset> absent(), parameters);
    }
    
    private static MediaRange create(
            final String type, final String subtype, 
            final Optional<Charset> charset, final ImmutableSetMultimap<String,String> parameters) {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(subtype);
        Preconditions.checkNotNull(charset);
        Preconditions.checkNotNull(parameters);
        
        final MediaRange mr = new MediaRange(type, subtype, charset, parameters);   
        return _REGISTERED.getIfPresent(mr);
    }    
    
    private static MediaRange create(
            final String type, final String subtype, 
            final Optional<Charset> charset, final Multimap<String,String> parameters) {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(subtype);
        Preconditions.checkNotNull(charset);
        Preconditions.checkNotNull(parameters);
        
        Preconditions.checkArgument(Primitives.IS_TOKEN.apply(type));
        Preconditions.checkArgument(Primitives.IS_TOKEN.apply(subtype));
        Preconditions.checkArgument(!parameters.containsKey("q"));
        Preconditions.checkArgument(!parameters.containsKey("charset"));
        
        if (type.equals("*")) {
            Preconditions.checkArgument(subtype.equals("*"));
            Preconditions.checkArgument(Optionals.isAbsent(charset));
            Preconditions.checkArgument(parameters.isEmpty());
        } 
        
        if (subtype.equals("*")) {
            Preconditions.checkArgument(Optionals.isAbsent(charset));
            Preconditions.checkArgument(parameters.isEmpty());
        }
        
        if (charset.isPresent()) {
            Preconditions.checkArgument(!charset.get().equals(Charset.ANY));
        }
        
        return MediaRange.create(
                        Ascii.toLowerCase(type), 
                        Ascii.toLowerCase(subtype), 
                        charset, 
                        GuavaCollectionHelpers.immutableSetMultimapFromEntries(parameters.entries(), KeyValuePair.VALIDATE_TO_LOWER_CASE_KEY_NOT_Q));  
    }
    
    
    /**
     * Parses a {@link MediaRange} from its String representation.
     * @throws NullPointerException if {@code mediaRange} is null.
     * @throws IllegalArgumentException if {@code mediaRange} is not parseable.
     */
    public static MediaRange parse(final String mediaRange) {
        return Parsers.parseWithParser(mediaRange, PARSER);
    }
    
    static MediaRange register(final MediaRange mediaRange) {
        return _REGISTERED.register(mediaRange);
    }
    
    private final Optional<Charset> charset;      
    private final ImmutableSetMultimap<String, String> parameters;
    
    private final String subtype;
    private final String type;
    
    private MediaRange(
            final String type, 
            final String subtype,
            final Optional<Charset> charset,
            final ImmutableSetMultimap<String, String> parameters) {
        this.type = type;
        this.subtype = subtype;
        this.charset = charset;
        this.parameters = parameters;
    }
    
    /**
     * Returns an optional charset for the value of the charset parameter if it is specified.
     */
    public Optional<Charset> charset() {
        return this.charset;
    }
    
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof MediaRange){
            final MediaRange that = (MediaRange) obj;
            return this.type.equals(that.type) &&
                    this.subtype.equals(that.subtype) &&
                    this.charset.equals(that.charset) &&
                    this.parameters.equals(that.parameters);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.type, this.subtype, this.charset, this.parameters);
    }

    /* (non-Javadoc)
     * @see restlib.data.Matcheable#match(java.lang.Object)
     * @throws IllegalStateException if this MediaRange has a charset.
     */
    @Override
    public int match(final MediaRange that) {
        Preconditions.checkNotNull(that);
        Preconditions.checkState(Optionals.isAbsent(this.charset()));

        if (this.equals(that)) {
            return 1000;
        } else if (this.equals(MediaRange.ANY)) {
            return 250;
        } else if (!this.type.equals(that.type)) {
            return 0;            
        } else if (this.subtype.equals("*")) {
            return 500;
        } else if (!this.subtype.equals(that.subtype)) {
            return 0;
        } else if (this.parameters.equals(that.parameters)) {
            return 1000;
        } else if (this.parameters.entries().containsAll(that.parameters.entries())) {
            return 750;
        } else {
            return 0;
        }
    }
      
    /**
     * Returns a multimap containing the parameters of this media type.
     */
    public Multimap<String, String> parameters() {
        return this.parameters;
    } 
    
    /**
     * Returns the media subtype.
     */
    public String subtype() {
        return this.subtype;
    }

    @Override
    public String toString() {        
        final StringBuilder builder = new StringBuilder();
        builder.append(this.type).append('/').append(this.subtype);

        if (this.charset.isPresent()) {
            builder.append("; ").append("charset").append('=').append(charset);
        }
        
        if (!this.parameters.isEmpty()) {
            builder.append("; ").append(KeyValuePair.toString(this.parameters.entries()));
        }
        
        return builder.toString();
    }
    
    /**
     * Returns the top-level media type.
     */
    public String type() {
        return this.type;
    }
    
    /**
     * Returns a new instance with the same type and subtype as this instance, with the charset parameter 
     * @param charset  any non-null {@link Charset} instance except {@link Charset.ANY}.
     * @throws NullPointerException if {@code charset} is null.
     * @throws IllegalArgumentException if {@code charset} equals {@link Charset.ANY}.
     * @throws IllegalStateException if either the type or subtype of the media range are wildcards.
     */
    public MediaRange withCharset(final Charset charset) {
        Preconditions.checkNotNull(charset);
        Preconditions.checkArgument(!charset.equals(Charset.ANY));
        Preconditions.checkState(!this.type.equals("*") && !this.subtype().equals("*"));
        return MediaRange.create(
                this.type, this.subtype, 
                Optional.of(charset), this.parameters);
    }
}
