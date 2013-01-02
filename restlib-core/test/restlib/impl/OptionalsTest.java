package restlib.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.testing.NullPointerTester;

public final class OptionalsTest {
    @Test
    public void testIsAbsent() {
        assertTrue(Optionals.isAbsent(Optional.absent()));
        assertFalse(Optionals.isAbsent(Optional.of("test")));
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
            .testAllPublicStaticMethods(Optionals.class);
    }
    
    @Test
    public void testToString() {
        try {
            Optionals.toString(Optional.absent());
            fail("Expected IllegalArgumentException.");
        } catch (final IllegalArgumentException expected){}
        
        assertEquals("test", Optionals.toString(Optional.of("test")));
    }
    
    
    @Test
    public void testToStringOrEmpty() {
        assertEquals("", Optionals.toStringOrEmpty(Optional.absent()));
        assertEquals("test", Optionals.toStringOrEmpty(Optional.of("test")));
    }
}
