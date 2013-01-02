package restlib.data;

import java.nio.CharBuffer;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.impl.CaseInsensitiveString;
import restlib.impl.CommonParsers;
import restlib.impl.Parser;
import restlib.impl.Registry;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

@Immutable
public final class CacheDirective {
    private static final Joiner ITERABLE_JOINER = Joiner.on(", ");
    
    private static final Registry<CacheDirective> _REGISTERED = new Registry<CacheDirective>();
    
    private static final String MAX_AGE_NAME = "max-age";
    private static final String MAX_STALE_NAME = "max-stale";
    private static final String MIN_FRESH_NAME = "min-fresh";
    private static final String MUST_REVALIDATE_NAME = "must-revalidate";
    private static final String NO_CACHE_NAME = "no-cache";
    private static final String NO_STORE_NAME = "no-store";
    private static final String NO_TRANSFORM_NAME = "no-transform";
    private static final String ONLY_IF_CACHED_NAME = "only-if-cached";
    private static final String PRIVATE_NAME = "private";
    private static final String PROXY_REVALIDATE_NAME = "proxy-revalidate";
    private static final String PUBLIC_NAME = "public";
    private static final String SHARED_MAX_AGE_NAME = "s-maxage";

    public static final CacheDirective MAX_STALE = register(create(MAX_STALE_NAME));
    public static final CacheDirective MUST_REVALIDATE = register(create(MUST_REVALIDATE_NAME));
    public static final CacheDirective NO_CACHE = register(create(NO_CACHE_NAME));
    public static final CacheDirective NO_STORE = register(create(NO_STORE_NAME));
    public static final CacheDirective NO_TRANSFORM = register(create(NO_TRANSFORM_NAME));
    public static final CacheDirective ONLY_IF_CACHED = register(create(ONLY_IF_CACHED_NAME));
    public static final CacheDirective PRIVATE = register(create(PRIVATE_NAME));
    public static final CacheDirective PROXY_REVALIDATE = register(create(PROXY_REVALIDATE_NAME));
    public static final CacheDirective PUBLIC = register(create(PUBLIC_NAME));
    
    static final Parser<CacheDirective> PARSER = new Parser<CacheDirective>() {
        @Override
        public Optional<CacheDirective> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final Optional<KeyValuePair> entry = KeyValuePair.PARSER.parse(buffer);
            if (entry.isPresent()) {
                final KeyValuePair kv = entry.get();
                final String value = kv.getValue(); 
                
                try {
                    final long valueAsLong = CommonParsers.parseUnsignedLong(value);
                    return Optional.of(CacheDirective.create(kv.getKey(), valueAsLong));
                } catch (final IllegalArgumentException e) {}          
                
                // FIXME: Would probably be good to split , seperated tokens         
                return Optional.of(CacheDirective.create(kv.getKey(), value));               
            } else {
                return Optional.absent();
            }             
        }    
    };
  
    public static CacheDirective create(final String token) {
        Preconditions.checkNotNull(token);
        Preconditions.checkArgument(Primitives.IS_TOKEN.apply(token));
        
        return _REGISTERED.getIfPresent(new CacheDirective(CaseInsensitiveString.wrap(token), ""));
    }
    
    public static CacheDirective create(final String token, final long value) {
        Preconditions.checkNotNull(token);
        Preconditions.checkArgument(Primitives.IS_TOKEN.apply(token));
        Preconditions.checkArgument(value >= 0);
        Preconditions.checkArgument(value <= 2147483648L);
        return new CacheDirective(CaseInsensitiveString.wrap(token), Long.toString(value));
    }
    
    public static CacheDirective create(final String token, final String value) {
        Preconditions.checkNotNull(token);
        Preconditions.checkNotNull(value);
        Preconditions.checkArgument(Primitives.IS_TOKEN.apply(token));
        Preconditions.checkArgument(Primitives.IS_QUOTABLE.apply(value));
        return new CacheDirective(CaseInsensitiveString.wrap(token), value);
    }
    
    private static CacheDirective register(final CacheDirective cacheDirective) {
        return _REGISTERED.register(cacheDirective);
    }
    
    public static CacheDirective maxAge(final long seconds) {
        Preconditions.checkArgument(seconds >= 0);
        return create(MAX_AGE_NAME, seconds);
    }
    
    public static CacheDirective maxStale(final long seconds) {
        Preconditions.checkArgument(seconds >= 0);
        return create(MAX_STALE_NAME, seconds);
    }
    
    public static CacheDirective minFresh(final long seconds) {
        Preconditions.checkArgument(seconds >= 0);
        return create(MIN_FRESH_NAME, seconds);
    }
    
    public static CacheDirective noCache(final Iterable<Header> fields) {
        Preconditions.checkNotNull(fields);
        if (Iterables.isEmpty(fields)) {
            return CacheDirective.NO_CACHE;
        }
        return create(NO_CACHE_NAME, ITERABLE_JOINER.join(fields));
    }
    
    public static CacheDirective privateInfo(final Iterable<Header> fields) {
        Preconditions.checkNotNull(fields);
        if (Iterables.isEmpty(fields)) {
            return CacheDirective.PRIVATE;
        }
        return create(PRIVATE_NAME, ITERABLE_JOINER.join(fields));
    }
    
    public static CacheDirective sharedMaxAge(final long seconds) {
        Preconditions.checkArgument(seconds > 0);
        return create(SHARED_MAX_AGE_NAME, seconds);
    }
    
    private final CaseInsensitiveString key;
    private final String value;
    
    private CacheDirective(final CaseInsensitiveString key, final String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof CacheDirective) {
            final CacheDirective that = (CacheDirective) obj;
            return this.key.equals(that.key) && this.value.equals(that.value);
        }
        
        return false;
    }
    
    public String getKey() {
        return key.toString();
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.key, this.value);
    }
    
    @Override
    public String toString() {        
        if (value instanceof String) {
            if (value.toString().isEmpty()) {
                return key.toString();
            }
            return  key + "=" + Primitives.encodeWord(value.toString());
        } 
        throw new IllegalStateException();
    }
}
