package restlib.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.nio.CharBuffer;

import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class TransferCodingTest {
    private static void doTestPARSER$parse(final String test, final String value, final SetMultimap<String, String> parameters) {
        final CharBuffer buffer = CharBuffer.wrap(test);
        final TransferCoding transferCoding = TransferCoding.PARSER.parse(buffer).get();
        assertEquals(value, transferCoding.value());
        assertEquals(parameters, transferCoding.parameters());        
    }
    
    private static void doTestPARSER$parse_withInvalid(final String test) {
        final CharBuffer buffer = CharBuffer.wrap(test);
        final Optional<TransferCoding> transferCoding = TransferCoding.PARSER.parse(buffer);
        assertFalse(transferCoding.isPresent());
    }
    
    @Test
    public void testEquals() {
        new EqualsTester()
                .addEqualityGroup(TransferCoding.CHUNKED, TransferCoding.create(TransferCoding.CHUNKED.toString()))
                .addEqualityGroup(TransferCoding.COMPRESS, TransferCoding.create(TransferCoding.COMPRESS.toString()))
                .addEqualityGroup(TransferCoding.create("test"), TransferCoding.create("test"))
                .addEqualityGroup(
                        TransferCoding.create("test", ImmutableSetMultimap.of("key", "value")),
                        TransferCoding.create("TEST", ImmutableSetMultimap.of("KEY", "VALUE")))
                .testEquals();
    }
    
    @Test
    public void testMatch() { 
        assertEquals(1000, TransferCoding.CHUNKED.match(TransferCoding.CHUNKED));
        assertEquals(700, TransferCoding.parse("t;a=b;c=d").match(TransferCoding.parse("t;c=d")));
        assertEquals(500, TransferCoding.ANY.match(TransferCoding.CHUNKED));
        assertEquals(0, TransferCoding.GZIP.match(TransferCoding.CHUNKED));
        assertEquals(0, TransferCoding.GZIP.match(TransferCoding.ANY));  
        assertEquals(0, TransferCoding.parse("t;a=b;c=d").match(TransferCoding.parse("t;c=d;e-f")));
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
                .testAllPublicInstanceMethods(
                        TransferCoding.CHUNKED);
        new NullPointerTester()
                .testAllPublicStaticMethods(TransferCoding.class);
    }
    
    @Test
    public void testPARSER$parse() {
        doTestPARSER$parse("GZIP", "gzip", ImmutableSetMultimap.<String, String> of());
        doTestPARSER$parse("TEST;a=b;c=d", "test", ImmutableSetMultimap.of("a", "b","c", "d"));
        doTestPARSER$parse("TEST;a=b;C=\"D\";Q=1;not=param", "test", ImmutableSetMultimap.of("a", "b","c", "d"));
        
        doTestPARSER$parse_withInvalid("");
    }
}
