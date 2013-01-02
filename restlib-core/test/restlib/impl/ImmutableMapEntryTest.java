package restlib.impl;

import static restlib.impl.ImmutableMapEntry.create;

import java.util.AbstractMap;

import org.junit.Test;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;


public final class ImmutableMapEntryTest {
    @Test
    public void testEqualsAndHashCode() {        
        new EqualsTester()
            .addEqualityGroup(
                    create("a","b"), 
                    create("a", "b"), 
                    new AbstractMap.SimpleEntry<Object, Object>("a", "b"))
            .addEqualityGroup(
                    create("a","c"), 
                    create("a", "c"), 
                    new AbstractMap.SimpleEntry<Object, Object>("a", "c"))
            .testEquals();
    }
    
    @Test
    public void testNulls() {
        final NullPointerTester tester = new NullPointerTester();
        tester.testAllPublicStaticMethods(ImmutableMapEntry.class);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testSetValue() {
        create("a","b").setValue("c");
    }
}
