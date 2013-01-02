package restlib.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.common.testing.NullPointerTester;

public final class CommonParsersTest {
    @Test
    public void testNulls() {
        final NullPointerTester tester = new NullPointerTester();
        tester.testAllPublicStaticMethods(CommonParsers.class);
    }
    
    @Test
    public void testParseUnsignedInteger() {
        final String test = "1234";
        final int expected = 1234;
        assertEquals(expected, CommonParsers.parseUnsignedInteger(test));
        
        try {
            CommonParsers.parseUnsignedInteger("+1234");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expectd){}
        
        try {
            CommonParsers.parseUnsignedInteger("");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expectd){}
    }
    
    @Test
    public void testParseUnsignedLong() {
        final String test = "1234";
        final long expected = 1234;
        assertEquals(expected, CommonParsers.parseUnsignedLong(test));
        
        try {
            CommonParsers.parseUnsignedLong("+1234");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expectd){}
        
        try {
            CommonParsers.parseUnsignedLong("");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expectd){}
    }
}
