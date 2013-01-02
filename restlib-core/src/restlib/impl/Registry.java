package restlib.impl;

import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

@ThreadSafe
public final class Registry<T> {
    private final ConcurrentMap<T,T> registered = Maps.newConcurrentMap();
    
    @GuardedBy("this")
    private volatile Iterable<T> registeredList = ImmutableList.of();
    
    public synchronized T register(final T item) {
        Preconditions.checkNotNull(item);
        final T retval = 
                Objects.firstNonNull(registered.putIfAbsent(item, item), item);
        registeredList = ImmutableList.copyOf(registered.keySet());
        return retval;
    }
    
    public T getIfPresent(final T item) {
        return Objects.firstNonNull(registered.get(item), item);
    }
    
    public Iterable<T> registered() {
        return registeredList;
    }
}
