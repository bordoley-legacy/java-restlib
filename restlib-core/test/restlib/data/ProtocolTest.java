package restlib.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.nio.CharBuffer;

import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class ProtocolTest {
    private static void doTestCreate_withInvalid(final String name, final String version) {
        try {
            Protocol.create(name, version);
        } catch (final IllegalArgumentException expected){}
    }
    
    private static void doTestPARSER$parse(final String test, final String name, final String version) {
        final Protocol protocol = Protocol.PARSER.parse(CharBuffer.wrap(test)).get();
        assertEquals(name, protocol.name());
        assertEquals(version, protocol.version());
    }
    
    private static void doTestPARSER$parse_withInvalid(final String test) {
        final CharBuffer buffer = CharBuffer.wrap(test);
        final Optional<Protocol> protocol = Protocol.PARSER.parse(buffer);
        assertFalse(protocol.isPresent());
    }
    
    @Test
    public void testCreat() {
        doTestCreate_withInvalid("", "");
    }
    
    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(Protocol.HTTP_1_1, Protocol.HTTP_1_1)
            .addEqualityGroup(Protocol.HTTP_1_0, Protocol.HTTP_1_0)
            .addEqualityGroup(Protocol.HTTP_0_9, Protocol.HTTP_0_9)
            .addEqualityGroup(Protocol.create("SSH", "1.0"), Protocol.create("SSH", "1.0"))
            .addEqualityGroup(Protocol.create("SSH", "2.0"), Protocol.create("SSH", "2.0"))
            .addEqualityGroup(Protocol.create("HTTP", ""), Protocol.create("HTTP", ""))
            .testEquals();
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
                .testAllPublicInstanceMethods(Protocol.HTTP_1_1);
        new NullPointerTester()    
                .testAllPublicStaticMethods(Protocol.class);
    }
    
    @Test
    public void testPARSER$parse() {
        doTestPARSER$parse("HTTP/1.1", "HTTP", "1.1");
        doTestPARSER$parse("HTTP", "HTTP", "");
        
        doTestPARSER$parse_withInvalid("HTTP/");
        doTestPARSER$parse_withInvalid("@HTTP/");
        
    }
}
