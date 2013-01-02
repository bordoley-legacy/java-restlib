package restlib.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.common.testing.NullPointerTester;

public final class HttpDateTest {
    private static void doTestParse_valieObsoleteUnsupported(final String test) {
        try {
            HttpDate.parse(test);
            fail ("Though valie, expected IlleglArgumentException.");
        } catch (final IllegalArgumentException expected){}
    }
    
    @Test
    public void testCopyOf() {
        final HttpDate test1 = HttpDate.now();
        assertTrue(test1 == HttpDate.copyOf(test1));
        
        final DateTime test2 = new DateTime(123) {
            @Override
            public String toString() {
                return String.valueOf(this.time());
            }           
        };
        
        assertFalse(test2 == HttpDate.copyOf(test2));
        assertEquals(test2, HttpDate.copyOf(test2));
    }
    
    @Test
    public void testNow() {
        final HttpDate start = HttpDate.now();
        try {
            Thread.sleep(10);
        } catch (final InterruptedException e) { /* do nothing */ }
        assertTrue(start.time() < HttpDate.now().time());
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
            .testAllPublicStaticMethods(HttpDate.class);
    }
    
    @Test
    public void testParse() {
        assertEquals(HttpDate.create(784887151000L), HttpDate.parse("Tue, 15 Nov 1994 08:12:31 GMT"));
        
        doTestParse_valieObsoleteUnsupported("Sun Sep 16 01:03:52 1973");
    }
}
