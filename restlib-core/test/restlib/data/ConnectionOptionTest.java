package restlib.data;

import java.nio.CharBuffer;

import org.junit.Test;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class ConnectionOptionTest {
    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(
                    ConnectionOption.CLOSE, 
                    ConnectionOption.create("CLOSE"), 
                    ConnectionOption.PARSER.parse(CharBuffer.wrap("CLOSE")).get())
            .addEqualityGroup(
                    ConnectionOption.KEEP_ALIVE,
                    ConnectionOption.create("keep-alive"))
            .addEqualityGroup(ConnectionOption.create("abc"), ConnectionOption.create("abc"))
            .testEquals();
    }
    @Test
    public void testNulls() {
        new NullPointerTester()
            .testAllPublicStaticMethods(ConnectionOption.class);
        new NullPointerTester()
            .testAllPublicInstanceMethods(ConnectionOption.CLOSE);
    }
}
