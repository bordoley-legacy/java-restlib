package restlib.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.testing.EqualsTester;

public final class WrapperTester<T> extends MethodTester<T>{
    public static<T> WrapperTester<T> create(
            final Class<T> clss, final Function<T,T> wrapper) {
        Preconditions.checkNotNull(clss);
        Preconditions.checkNotNull(wrapper);
        return new WrapperTester<T>(clss, wrapper);
    }

    private boolean testEquals = false;
    private final Function<T,T> wrapper;
    
    private WrapperTester(final Class<T> clss, final Function<T,T> wrapper){
        super(clss);
        this.wrapper = wrapper;
    }
    
    public <I> WrapperTester<T> addDefaultInstance(final Class<I> clss, final I obj) {
        super.addDefaultInstance(clss, obj);
        return this;
    }
    
    @Override
    protected void doTest(final Method method, final T test, final Object[] args) {
        try {
            assertEquals(method.getName(),
                    method.invoke(test, args),
                    method.invoke(wrapper.apply(test), args));
        } catch (final Exception e) {
            fail(e.getMessage() + " method: " + method.getName());
        }
    }
    
    public void executeTests(final T...tests) {
        Preconditions.checkNotNull(tests);
        final EqualsTester tester = new EqualsTester();
        super.executeTests(tests);
        
        if (testEquals) {
            for (final T test : tests){
                tester.addEqualityGroup(test, wrapper.apply(test));
            }
            tester.testEquals();
        }
    }

    public WrapperTester<T> includingEquals() {
        this.testEquals = true;
        return this;
    }
    
    public WrapperTester<T> useDefaultInstances() {
        super.useDefaultInstances();
        return this;
    }
}
