package restlib.data;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import restlib.impl.CodePointMatcher;
import restlib.impl.ImmutableMapEntry;
import restlib.impl.Optionals;
import restlib.impl.Parser;
import restlib.impl.Parsers;
import restlib.impl.Tokenizer;
import restlib.net.UriEncoding;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * ListMultimap representation of an x-www-form-urlencoded form.
 * @see <a href="http://www.whatwg.org/specs/web-apps/current-work/#application/x-www-form-urlencoded">http://www.whatwg.org/specs/web-apps/current-work/#application/x-www-form-urlencoded</a>
 */
@Immutable
public final class Form extends ForwardingMultimap<String, String>{
    private static final Predicate<Integer> ALLOWED_CHARS = 
            CodePointMatcher.inRange('a', 'z')
                            .or(CodePointMatcher.inRange('A', 'Z'))
                            .or(CodePointMatcher.inRange('1', '9'))
                            .or(CodePointMatcher.anyOf("*-._ "));
    
    private static final Form EMPTY = new Form(ImmutableMultimap.<String, String> of());
    
    private static final Function<Entry<String, String>,Entry<String, String>> ENTRY_ENCODER =   
            new Function<Entry<String, String>,Entry<String, String>>() {    
                private final Function<CharSequence, String> uriEncoder =
                        UriEncoding.utf8Encoder(ALLOWED_CHARS);
                
                @Override
                public Entry<String, String> apply(final Entry<String, String> entry) {
                    return ImmutableMapEntry.create(
                            uriEncoder.apply(entry.getKey()).replace(' ', '+'),
                            uriEncoder.apply(entry.getValue()).replace(' ', '+'));
                }           
        };
    
    private static final MapJoiner JOINER = Joiner.on('&').withKeyValueSeparator("=");

    
    private static final Splitter PARAMETER_SPLITTER =
            Splitter.on("&").omitEmptyStrings().trimResults(); 

    private static final Function<CharSequence, Form> UTF8_PARSER = parser(UriEncoding.UTF8_DECODE);
    
    /**
     * Returns a new FormBuilder instance.
     */
    public static FormBuilder builder() {
        return new FormBuilder();
    }
    
    /**
     * Returns an empty Form.
     */
    public static Form of() {
        return EMPTY;
    }
    
    /**
     * Returns a Form containing a single entry.
     */
    public static Form of(final String k, final String v) {
        return Form.builder().put(k, v).build();
    }
    
    /**
     * Returns a Form containing the given entries, in order.
     */
    public static Form of(
            final String k1, final String v1, 
            final String k2, final String v2) {
        return Form.builder().put(k1, v1).put(k2, v2).build();
    }
    
    /**
     * Returns a Form containing the given entries, in order.
     */
    public static Form of(
            final String k1, final String v1, 
            final String k2, final String v2,
            final String k3, final String v3) {
        return Form.builder()
                .put(k1, v1)
                .put(k2, v2)
                .put(k3, v3).build();
    }
    
    /**
     * Returns a Form containing the given entries, in order.
     */
    public static Form of(
            final String k1, final String v1, 
            final String k2, final String v2,
            final String k3, final String v3,
            final String k4, final String v4) {
        return Form.builder()
                .put(k1, v1)
                .put(k2, v2)
                .put(k3, v3)
                .put(k4, v4).build();
    }
    
    /**
     * Returns a Form containing the given entries, in order.
     */
    public static Form of(
            final String k1, final String v1, 
            final String k2, final String v2,
            final String k3, final String v3,
            final String k4, final String v4,
            final String k5, final String v5) {
        return Form.builder()
                .put(k1, v1)
                .put(k2, v2)
                .put(k3, v3)
                .put(k4, v4)
                .put(k5, v5).build();
    }
    
    /**
     * Parses a {@code Form} from its UTF-8 encoded string representation.
     * @param form an x-www-form-urlencoded string. Escaped characters must be encoded using UTF-8.
	 * @throws NullPointerException if {@code form} is null.
	 * @throws IllegalArgumentException if {@code form} is not parseable.
     */
    // FIXME: If we want to be strictly compliant, we would need to implement the
    // extremely complex charset handling defined in the HTML spec. Doesn't seem worth it
    // for now.
    public static Form parse(final CharSequence form) {
    	return UTF8_PARSER.apply(form);
    }
    
    private static Function<CharSequence, Form> parser(final Function<CharSequence, String> uriDecoder) {
        Preconditions.checkNotNull(uriDecoder);
        
        return new Function<CharSequence, Form>() {
            final Parser<Character> equalsCharReader = Parsers.charParser('=');
            final Parser<String> keyReader = Parsers.whileMatchesParser(CharMatcher.isNot('='));
            final Parser<String> valueReader = Parsers.whileMatchesParser(CharMatcher.isNot('&'));
            
            @Override
            public Form apply(final CharSequence form) {
                Preconditions.checkNotNull(form);
                
                final FormBuilder builder =
                        Form.builder();           
                
                final Iterable<String> parameters = 
                        PARAMETER_SPLITTER.split(form.toString().replace("+", "%20"));
                
                for (final String param : parameters) {
                    final List<Optional<Object>> tokens =
                            Tokenizer.create(CharBuffer.wrap(param))
                                     .read(keyReader)        // 0
                                     .read(equalsCharReader) // 1
                                     .read(valueReader)      // 2
                                     .tokens();
                    
                    Preconditions.checkArgument(
                            tokens.get(0).isPresent(),
                            "Invalid form: " + form);
                    
                    final String key = Optionals.toString(tokens.get(0));
                    final String value = Optionals.toStringOrEmpty(tokens.get(2));
                    
                    builder.put(uriDecoder.apply(key), uriDecoder.apply(value));
                }
                
                return builder.build();
            }           
        };
    }
    
    private final ImmutableMultimap<String, String> delegate;
    
    Form(final ImmutableMultimap<String, String> delegate){
        this.delegate = delegate;
    }
    
    @Override
    protected Multimap<String, String> delegate() {
        return delegate;
    }
    
    @Override
    public String toString() {
        return JOINER.join(Iterables.transform(this.entries(), ENTRY_ENCODER));
    }
}
