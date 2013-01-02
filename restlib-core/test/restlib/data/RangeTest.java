package restlib.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.CharBuffer;
import java.util.List;

import org.junit.Test;

import restlib.impl.Optionals;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class RangeTest {    
    private static void doTestOtherRange_withInvalid(
            final RangeUnit rangeUnit, final String range) {
        try {
            Range.otherRange(rangeUnit, range);
            fail("Expected IllegalArgumentException.");
        } catch (final IllegalArgumentException expected){}
    }
    
    private static void doTestPARSER$parse_withBytes(final String test, List<ByteRangeSpec> byteRanges) {     
        final Range.Bytes range = (Range.Bytes) Range.PARSER.parse(CharBuffer.wrap(test)).get();
        assertEquals(RangeUnit.BYTES, range.unit());
        assertEquals(byteRanges, range.byteRanges());
    }
    
    private static void doTestPARSER$parse_withInvalid(final String test) {     
        assertTrue(Optionals.isAbsent(Range.PARSER.parse(CharBuffer.wrap(test))));
    }
    
    private static void doTestPARSER$parse_withOther(final String test, final RangeUnit rangeUnit, final String rangeStr) {     
        final Range.Other range = (Range.Other) Range.PARSER.parse(CharBuffer.wrap(test)).get();
        assertEquals(rangeUnit, range.unit());
        assertEquals(rangeStr, range.range());
    }
    
    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(
                    Range.byteRange(ImmutableList.<ByteRangeSpec> of()),
                    Range.byteRange(ImmutableList.<ByteRangeSpec> of()))
            .addEqualityGroup(
                    Range.byteRange(ImmutableList.<ByteRangeSpec> of(ByteRangeSpec.range(1, 2))),
                    Range.byteRange(ImmutableList.<ByteRangeSpec> of(ByteRangeSpec.range(1, 2))))   
            .addEqualityGroup(
                    Range.otherRange(RangeUnit.create("test"), "ABC"),
                    Range.otherRange(RangeUnit.create("test"), "ABC"))
            .addEqualityGroup(
                    Range.otherRange(RangeUnit.create("test"), "DEF"),
                    Range.otherRange(RangeUnit.create("test"), "DEF"))     
            .addEqualityGroup(
                    Range.otherRange(RangeUnit.create("test2"), "DEF"),
                    Range.otherRange(RangeUnit.create("test2"), "DEF"))                      
            .testEquals();        
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
                .testAllPublicInstanceMethods(Range.byteRange(ImmutableList.<ByteRangeSpec> of()));
        new NullPointerTester()
            .testAllPublicInstanceMethods(Range.otherRange(RangeUnit.create("other"), "abcd"));
        new NullPointerTester()
                .setDefault(RangeUnit.class, RangeUnit.BYTES)
                .testAllPublicStaticMethods(Range.class);
    }
    
    @Test
    public void testOtherRangeUnit_withInvalid() {
        doTestOtherRange_withInvalid(RangeUnit.BYTES, "ABC");
        doTestOtherRange_withInvalid(RangeUnit.ACCEPT_NONE, "ABC");
        doTestOtherRange_withInvalid(RangeUnit.create("test"), "");
        doTestOtherRange_withInvalid(RangeUnit.create("test"), "\u1000");
        doTestOtherRange_withInvalid(RangeUnit.create("test"), "\u0000");
    }
    
    @Test
    public void testPARSER$parse() {
        doTestPARSER$parse_withBytes("bytes=500-600,-1,900-",
                ImmutableList.of(
                        ByteRangeSpec.range(500, 600),
                        ByteRangeSpec.suffix(1),
                        ByteRangeSpec.startingAt(900)));
        
        doTestPARSER$parse_withInvalid("");
        doTestPARSER$parse_withInvalid("@Unit=fjdkljdkfjl");
        doTestPARSER$parse_withInvalid("other=");
        doTestPARSER$parse_withInvalid("bytes=");       
        
        doTestPARSER$parse_withOther("Other=abcd", RangeUnit.create("other"), "abcd");
    }
}
