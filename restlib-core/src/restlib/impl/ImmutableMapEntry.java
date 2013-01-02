package restlib.impl;

import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * A {@code Map.Entry} that maintains an immutable key and value.
 */
@Immutable
public final class ImmutableMapEntry<K, V> implements Entry<K,V> {
    /**
     * Returns a new {@code ImmutablMapEntry} representing the mapping of key to value.
     * @param key a non-null key
     * @param value a non-null value
     * @throws NullPointerException if key is null.
     */
    public static <K,V> ImmutableMapEntry<K,V> create(final K key, final V value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        return new ImmutableMapEntry<K,V>(key,value);
    }
    
    private final K key;
    private final V value;
    
    private ImmutableMapEntry(final K key, final V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Entry) {
            final Entry<?, ?> that = (Entry<?, ?>) obj;
            return Objects.equal(this.getKey(), that.getKey())
                    && Objects.equal(this.getValue(), that.getValue());
        } else {
            return false;
        }
    }
    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }
    
    @Override
    public int hashCode() {
        // Only contains non-null key and value 
        return getKey().hashCode() ^ getValue().hashCode();
    }

    /**
     * Guaranteed to throw an {@code UnsupportedOperationException}.
     */
    @Override
    public V setValue(@Nullable final V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return key + "=" + "[" + value + "]";
    }
}
