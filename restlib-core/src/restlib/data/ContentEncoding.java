package restlib.data;

import java.nio.CharBuffer;

import restlib.impl.CaseInsensitiveString;
import restlib.impl.Parser;
import restlib.impl.Parsers;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;


public final class ContentEncoding implements Matcheable<ContentEncoding> {    
    public static final ContentEncoding ANY = ContentEncoding.create("*");   
    public static final ContentEncoding COMPRESS = ContentEncoding.create("compress");
    public static final ContentEncoding DEFLATE = ContentEncoding.create("deflate");
    public static final ContentEncoding GZIP = ContentEncoding.create("gzip");
   
    static final Parser<ContentEncoding> PARSER = new Parser<ContentEncoding>() {
        @Override
        public Optional<ContentEncoding> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final Optional<String> token = Primitives.TOKEN_PARSER.parse(buffer);
            if (token.isPresent()) {
                return Optional.of(ContentEncoding.create(token.get()));
            } else {
                return Optional.absent();
            }
        }      
    };
    
    static final Parser<Iterable<ContentEncoding>> LIST_PARSER =
            Parsers.listParser(
                    ContentEncoding.PARSER,
                    Primitives.OWS_COMMA_OWS_PARSER,
                    ContentEncoding.class);
    
    public static ContentEncoding create(final String in) {
        Preconditions.checkNotNull(in);
        // FIXME: Validate in
        return new ContentEncoding(CaseInsensitiveString.wrap(in));  
    }
    
    private final CaseInsensitiveString delegate;
    
    private ContentEncoding(final CaseInsensitiveString delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof ContentEncoding) {
            final ContentEncoding that = (ContentEncoding) obj;
            return this.delegate.equals(that.delegate);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.delegate);
    }
    
    @Override
    public int match(final ContentEncoding that) {
        if (this.equals(that)) {
            return 1000;
        } else if (this.equals(ContentEncoding.ANY)) {
            return 500;
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return this.delegate.toString();
    }
}
