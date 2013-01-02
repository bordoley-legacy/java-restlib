package restlib.impl;

import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSetMultimap;

public final class GuavaCollectionHelpers {       
    /**
     * Returns an {@code ImmutableBiMap} containing the given {@code entries}.
     * @throws NullPointerExcepction if {@code entries} is null. Also if any key
     * or value is null.
     * @throws IllegalArgumentException if entries includes any duplicate keys
     * or values.
     */
    public static <K,V> ImmutableBiMap<K,V> immutableBiMapFromEntries(
            final Iterable<? extends Entry<K,V>> entries) {
        return immutableBiMapFromEntries(entries, Functions.<Entry<K,V>> identity());
    }
    
    /**
     * Returns an {@code ImmutableBiMap} containing the given {@code entries} 
     * transformed by {@code transform}.
     * @throws NullPointerExcepction if either {@code entries} or {@code transform} is null. 
     * Also if any key or value is null.
     * @throws IllegalArgumentException if entries includes any duplicate keys
     * or values.
     */
    public static <K1,K2,V1,V2> ImmutableBiMap<K2,V2> immutableBiMapFromEntries(
            final Iterable<? extends Entry<K1,V1>> entries,
            final Function<Entry<K1,V1>, Entry<K2,V2>> transform) {
        Preconditions.checkNotNull(entries);
        Preconditions.checkNotNull(transform);
        
        final ImmutableBiMap.Builder<K2,V2> builder = ImmutableBiMap.builder();
        for (final Entry<K1,V1> entry : entries) {
            builder.put(transform.apply(entry));
        }       
        return builder.build();
    }
    
    /**
     * Returns an {@code ImmutableListMultimap} containing the given {@code entries}.
     * @throws NullPointerExcepction if {@code entries} is null. Also if any key
     * or value is null.
     */
    public static <K,V> ImmutableListMultimap<K,V> immutableListMultimapFromEntries(
            final Iterable<? extends Entry<K, V>> entries) {
        return immutableListMultimapFromEntries(entries, Functions.<Entry<K,V>> identity());
    }
    
    /**
     * Returns an {@code ImmutableListMultimap} containing the given {@code entries} 
     * transformed by {@code transform}.
     * @throws NullPointerExcepction if either {@code entries} or {@code transform} is null. 
     * Also if any key or value is null.
     */
    public static <K1,K2,V1,V2> ImmutableListMultimap<K2,V2> immutableListMultimapFromEntries(
            final Iterable<? extends Entry<K1, V1>> entries, 
            final Function<Entry<K1,V1>, Entry<K2,V2>> transform) {
        Preconditions.checkNotNull(entries);
        Preconditions.checkNotNull(transform);
        
        final ImmutableListMultimap.Builder<K2,V2> builder = 
                ImmutableListMultimap.builder();
        
        for (final Entry<K1, V1> entry : entries) {
            builder.put(transform.apply(entry));
        }      
        return builder.build();
    }
    
    /**
     * Returns an {@code ImmutableSetMultimap} containing the given {@code entries}.
     * @throws NullPointerExcepction if {@code entries} is null. Also if any key
     * or value is null.
     */
    public static <K,V> ImmutableSetMultimap<K,V> immutableSetMultimapFromEntries(
            final Iterable<? extends Entry<K, V>> entries) {
        return immutableSetMultimapFromEntries(entries, Functions.<Entry<K,V>> identity());
    }
    
    /**
     * Returns an {@code ImmutableSetMultimap} containing the given {@code entries} 
     * transformed by {@code transform}.
     * @throws NullPointerExcepction if either {@code entries} or {@code transform} is null. 
     * Also if any key or value is null.
     */
    public static <K1,K2,V1,V2> ImmutableSetMultimap<K2,V2> immutableSetMultimapFromEntries(
            final Iterable<? extends Entry<K1, V1>> entries, 
            final Function<Entry<K1,V1>, Entry<K2,V2>> transform) {
        Preconditions.checkNotNull(entries);
        Preconditions.checkNotNull(transform);
        
        final ImmutableSetMultimap.Builder<K2,V2> builder = 
                ImmutableSetMultimap.builder();
        
        for (final Entry<K1, V1> entry : entries) {
            builder.put(transform.apply(entry));
        }      
        return builder.build();
    }
    
    private GuavaCollectionHelpers(){}
}
