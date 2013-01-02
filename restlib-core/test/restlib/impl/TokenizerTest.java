package restlib.impl;

import java.nio.CharBuffer;

import org.junit.Test;

import com.google.common.testing.NullPointerTester;

public final class TokenizerTest {
    @Test
    public void testNulls() {
        new NullPointerTester()
            .testAllPublicStaticMethods(Tokenizer.class);
        new NullPointerTester()
            .testAllPublicInstanceMethods(Tokenizer.create(CharBuffer.wrap("")));
    }
}
