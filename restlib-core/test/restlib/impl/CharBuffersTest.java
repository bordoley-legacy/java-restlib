package restlib.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.CharBuffer;

import org.junit.Test;

import com.google.common.testing.NullPointerTester;

public final class CharBuffersTest {
    @Test
    public void testNulls() {
        new NullPointerTester()
            .testAllPublicStaticMethods(CharBuffers.class);
    }
    
    @Test
    public void testPushback() {
        final CharBuffer buffer = CharBuffer.wrap("abc");
        
        try {
            CharBuffers.pushback(buffer);
            fail("Expected IllegalStateException.");
        } catch (final IllegalStateException expected) {}
        
        final int originalPosition = buffer.position();
        buffer.get();
        CharBuffers.pushback(buffer);
        assertEquals(originalPosition, buffer.position());
    }
}
