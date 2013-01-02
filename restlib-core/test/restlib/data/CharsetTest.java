package restlib.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.nio.CharBuffer;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class CharsetTest {
    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(
                    Charset.US_ASCII, 
                    Charset.create(Charsets.US_ASCII.toString()),
                    Charset.PARSER.parse(CharBuffer.wrap("US-ASCII")).get())
            .addEqualityGroup(
                    Charset.ANY, 
                    Charset.create("*"),
                    Charset.PARSER.parse(CharBuffer.wrap("*")).get())
            .addEqualityGroup(
                    Charset.UTF_16, 
                    Charset.create(Charsets.UTF_16.toString()),
                    Charset.PARSER.parse(CharBuffer.wrap("UTF-16")).get())
            .testEquals();
    }
    
    @Test
    public void testMatch() {
        assertEquals(0, Charset.create("UTF-8").match(Charset.fromNioCharset(Charsets.US_ASCII)));
        assertEquals(1000, Charset.create("UTF-8").match(Charset.fromNioCharset(Charsets.UTF_8)));
        assertEquals(500, Charset.ANY.match(Charset.fromNioCharset(Charsets.UTF_16LE)));
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
            .testAllPublicStaticMethods(Charset.class);
        new NullPointerTester()
            .testAllPublicInstanceMethods(Charset.PARSER);
        new NullPointerTester()
            .testAllPublicInstanceMethods(Charset.UTF_8);
    }
    
    @Test
    public void testPARSER$parse_withInvalid() {
        assertFalse(Charset.PARSER.parse(CharBuffer.wrap("")).isPresent());
        assertFalse(Charset.PARSER.parse(CharBuffer.wrap("@token")).isPresent());
    }
    
    @Test
    public void testToNioCharset() {
        assertEquals(Charsets.UTF_8, Charset.UTF_8.toNioCharset());
        
        try {
            Charset.ANY.toNioCharset();
        } catch (final IllegalStateException expected){}
    }
}
