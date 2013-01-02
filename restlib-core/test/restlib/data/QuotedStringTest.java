package restlib.data;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableBiMap;

public final class QuotedStringTest {
    private final ImmutableBiMap<String, String> tests = ImmutableBiMap
            .<String, String> builder()
            .put("", "\"\"")
            .put("test", "\"test\"")
            .put("test\"test", "\"test\\\"test\"")
            .put("test\\test", "\"test\\\\test\"").build();

    private final ImmutableBiMap<String, String> decodeTests = ImmutableBiMap
            .<String, String> builder().put("test", "\"\\t\\e\\s\\t\"").build();

    @Test(expected = IllegalArgumentException.class)
    public void decode_withNoLeadingQuote() {
        Primitives.decodeQuotedString("test\"");
    }

    @Test(expected = IllegalArgumentException.class)
    public void decode_withNoTrailingQuote() {
        Primitives.decodeQuotedString("\"test");
    }

    @Test(expected = NullPointerException.class)
    public void decode_withNull() {
        Primitives.decodeQuotedString(null);
    }

    @Test
    public void decode_withTests() {
        for (final Map.Entry<String, String> test : tests.entrySet()) {
            assertEquals(test.getKey(),
                    Primitives.decodeQuotedString(test.getValue()));
        }

        for (final Map.Entry<String, String> test : decodeTests.entrySet()) {
            assertEquals(test.getKey(),
                    Primitives.decodeQuotedString(test.getValue()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void decode_withUnquoteString() {
        Primitives.decodeQuotedString("test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void decode_withUnsupportedChars() {
        Primitives.decodeQuotedString("\"test\uFF10test\"");
    }

    @Test(expected = NullPointerException.class)
    public void encode_withNull() {
        Primitives.encodeQuotedString(null);
    }

    @Test
    public void encode_withTests() {
        for (final Map.Entry<String, String> test : tests.entrySet()) {
            assertEquals(Primitives.encodeQuotedString(test.getKey()),
                    test.getValue());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void encode_withUnsupportedChars() {
        Primitives.encodeQuotedString("test\u0000test");
    }
}
