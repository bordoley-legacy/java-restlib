package restlib.test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

abstract class MethodTester<T> {
    private final Class<T> clss;
    private final Map<Class<?>,Object> defaultInstances = Maps.newHashMap();
   
    protected MethodTester(final Class<T> clss){
        this.clss = clss;
    }
    
    public <I> MethodTester<T> addDefaultInstance(final Class<I> clss, final I obj) {
        Preconditions.checkNotNull(clss);
        Preconditions.checkNotNull(obj);
        defaultInstances.put(clss, obj);
        return this;
    }
    
    protected abstract void doTest(final Method method, final T test, final Object[] args);
    
    private void doTest(final T test) {
        Preconditions.checkNotNull(test);
        final Iterable<Method> methods = Arrays.asList(this.clss.getDeclaredMethods());
        for (final Method method : methods) {  
            // Skip static methods
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            } else if (!Modifier.isPublic(method.getModifiers())) {
                // FIXME: Add config to choose the modifier level.
                continue;
            }
            
            final Iterable<Object> args =
                    Iterables.transform(
                            Arrays.asList(method.getParameterTypes()), 
                            new Function<Class<?>, Object>() {
                                @Override
                                public Object apply(final Class<?> clss) {
                                    Preconditions.checkArgument(
                                            defaultInstances.containsKey(clss),
                                            "No default instance for: " + clss.getName() + ", while testing method: " + method.getName());
                                    return defaultInstances.get(clss);
                                }});
            doTest(method, test, Iterables.toArray(args, Object.class));
        }
    }
    
    public void executeTests(final T...tests) {
        Preconditions.checkNotNull(tests);
        
        for (final T test : tests){
            doTest(test);
        }
    }
    
    public MethodTester<T> useDefaultInstances() {
        this.defaultInstances.put(Object.class, new Object());
        this.defaultInstances.put(Iterable.class, ImmutableList.of());
        return this;
    }
}
