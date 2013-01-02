package restlib.data;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * A builder for generating {@code Form} instances. FormBuilder instances can be reused; 
 * it is safe to call build() multiple times to build multiple Form instances.
 */
@NotThreadSafe
public final class FormBuilder {
    private final ImmutableListMultimap.Builder<String, String> builder =  ImmutableListMultimap.builder();
    
    FormBuilder(){};
    
    /**
     * Returns a newly-created Form instance based on the contents of the FormBuilder.
     */
    public Form build() {
        final ImmutableMultimap<String, String> multimap = builder.build();
        if (multimap.isEmpty()) {
            return Form.of();
        }
        
        return new Form(multimap);
    }
    
    /**
     * Adds an entry to the built Form.
     * @throws NullPointerException if {@code entry} is null or contains a null key or value.
     * @throws IllegalArgumentException if entry.getKey() is an empty String.
     */
    public FormBuilder put(final Map.Entry<String,String> entry) {
    	Preconditions.checkNotNull(entry.getKey());
    	Preconditions.checkArgument(!entry.getKey().isEmpty());
        builder.put(entry);
        return this;
    }
    
    /**
     * Adds a key-value mapping to the built form.
     * @throws NullPointerException if key or value are null.
     * @throws IllegalArgumentException if key is an empty String.
     */
    public FormBuilder put(final String key, final  String value) {
    	Preconditions.checkNotNull(key);
    	Preconditions.checkNotNull(value);
    	Preconditions.checkArgument(!key.isEmpty());
        builder.put(key, value);
        return this;
    }
    
    /**
     * Stores a multimap's entries in the built form. The generated
     * multimap's key and value orderings correspond to the iteration ordering
     * of the {@code multimap.asMap()} view, with new keys and values following
     * any existing keys and values.
     * @throws NullPointerException if any key or value in {@code multimap} is
     *     null. The builder is left in an invalid state.
     * @throws IllegalArgumentException if any entry key in {@code multimap} is an 
     *     empty String. The builder is left in an invalid state.
     */
    public FormBuilder putAll(final Multimap<String,String> multimap) {
    	for (final Entry<String,Collection<String>> entry : multimap.asMap().entrySet()) {
    		putAll(entry.getKey(), entry.getValue());	
    	}
        return this;
    }
    
    /**
     * Stores a collection of values with the same key in the built form.
     *
     * @throws NullPointerException if {@code key}, {@code values}, or any
     *     element in {@code values} is null. The builder is left in an invalid
     *     state.
     * @throws IllegalArgumentException if key is an empty String. The builder
     *     is left in an invalid state. 
     */
    public FormBuilder putAll(final String key, final Iterable<String> values) {
    	Preconditions.checkNotNull(key);
    	Preconditions.checkNotNull(values);
    	Preconditions.checkArgument(!key.isEmpty());
    	
        builder.putAll(key, values);
        return this;
    }
    
    /**
     * Stores an array of values with the same key in the built form.
     *
     * @throws NullPointerException if the key or any value is null. The builder
     *     is left in an invalid state.
     * @throws IllegalArgumentException if key is an empty String. The builder
     *     is left in an invalid state.      
     */
    public FormBuilder putAll(final String key, final String... values) {
    	Preconditions.checkNotNull(key);
    	Preconditions.checkNotNull(values);
    	Preconditions.checkArgument(!key.isEmpty());
    	
        builder.putAll(key, values);
        return this;
    }
}
