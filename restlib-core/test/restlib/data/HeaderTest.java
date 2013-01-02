package restlib.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.nio.CharBuffer;

import org.junit.Test;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class HeaderTest {
    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(HttpHeaders.ACCEPT, HttpHeaders.ACCEPT)
            .addEqualityGroup(Header.create("X-Fake"), Header.create("X-Fake"))
            .testEquals();
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
            .testAllPublicStaticMethods(Header.class);
        new NullPointerTester()
            .testAllPublicInstanceMethods(HttpHeaders.ACCEPT);
        new NullPointerTester()
            .testAllPublicInstanceMethods(Header.PARSER);
    }
    
    @Test
    public void testPARSER$parse() {
        assertEquals("X-Test", Header.PARSER.parse(CharBuffer.wrap("X-Test")).get().toString());
        
        assertFalse(Header.PARSER.parse(CharBuffer.wrap("@X-test")).isPresent());
    }
}
