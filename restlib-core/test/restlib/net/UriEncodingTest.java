package restlib.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map.Entry;

import org.junit.Test;

import restlib.impl.CodePointMatcher;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.testing.NullPointerTester;

public final class UriEncodingTest {
    private static void doTestApply_withInvalid(final Function<CharSequence,String> func, final String test) {
        try {
            func.apply(test);
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
    }
    
    @Test
    public void testCharsetPercentDecoder$apply_withInvalid() {
        final Function<CharSequence,String> decoder = 
                UriEncoding.charsetPercentDecoder(Charsets.UTF_8);     
        
        doTestApply_withInvalid(decoder, "abc"); 
        doTestApply_withInvalid(decoder, "%a"); 
        doTestApply_withInvalid(decoder, "%xx"); 
        
        // Decode a high byte character using ascii
        doTestApply_withInvalid(
                UriEncoding.charsetPercentDecoder(Charsets.US_ASCII), "%F0%90%80%80"); 
    }
    
    @Test
    public void testCharsetPercentEncoder(){
        final Function<Integer, String> encoder = 
                UriEncoding.charsetPercentEncoder(Charsets.UTF_8); 
        assertEquals(encoder.apply(0), "");
        assertEquals(encoder.apply(9), "%09");
    }
    
    @Test 
    public void testDecoder() {
        final ImmutableMap<String,String> tests =
                ImmutableMap.<String,String> builder()
                    .put("\uD800\uDC00", "%F0%90%80%80")
                    .put("\uD800\uDFFF", "%F0%90%8F%BF")
                    .put("\uDBFF\uDC00", "%F4%8F%B0%80")
                    .put("\uDBFF\uDFFF", "%F4%8F%BF%BF")
                    .put("1\uDBFF\uDC00", "1%F4%8F%B0%80")
                    .put("@\uDBFF\uDC00", "%40%F4%8F%B0%80")
                    .put("\uDBFF\uDC001", "%F4%8F%B0%801")
                    .put("\uDBFF\uDC00@", "%F4%8F%B0%80%40")
                    .put("\u0101\uDBFF\uDC00", "%C4%81%F4%8F%B0%80")
                    .put("\uDBFF\uDC00\u0101", "%F4%8F%B0%80%C4%81")
                    .build();
        
        for (final Entry<String,String> test : tests.entrySet()) {
            assertEquals(test.getKey(), UriEncoding.UTF8_DECODE.apply(test.getValue()));
        }
    }
    
    @Test 
    public void testDecoder_withInvalid() {
        final ImmutableList<String> invalidTests = 
                ImmutableList.of("%", "%A", "Hello%", "%xy", "%az", "%ab%q");
        for(final String test : invalidTests){
            doTestApply_withInvalid(UriEncoding.UTF8_DECODE, test);
        }
    }
    
    @Test
    public void testEncoder() {
        final String test = "Hello +%-_.!~*\'()@\u00ae\u0101\u10a0";
        final String expected = "Hello%20%2B%25-_.%21%7E*%27%28%29%40%C2%AE%C4%81%E1%82%A0";
        final Predicate<Integer> safeChars = 
                CodePointMatcher.inRange('a', 'z')
                                .or(CodePointMatcher.inRange('A', 'Z'))
                                .or(CodePointMatcher.anyOf("*-._"));
        
        assertEquals(expected, UriEncoding.utf8Encoder(safeChars).apply(test));
    }
    
    @Test
    public void testNulls() {
        final NullPointerTester tester = new NullPointerTester();
        tester.testAllPublicInstanceMethods(UriEncoding.UTF8_DECODE);
        tester.testAllPublicInstanceMethods(UriEncoding.UTF8_DECODE_FRAGMENT);
        tester.testAllPublicInstanceMethods(UriEncoding.UTF8_DECODE_IFRAGMENT);
        tester.testAllPublicInstanceMethods(UriEncoding.UTF8_DECODE_IQUERY);   
        tester.testAllPublicInstanceMethods(UriEncoding.UTF8_DECODE_IUSER_INFO);
        tester.testAllPublicInstanceMethods(UriEncoding.UTF8_DECODE_PATH_ISEGMENT);
        tester.testAllPublicInstanceMethods(UriEncoding.UTF8_DECODE_PATH_SEGMENT);
        tester.testAllPublicInstanceMethods(UriEncoding.UTF8_DECODE_QUERY);
        tester.testAllPublicInstanceMethods(UriEncoding.UTF8_DECODE_USER_INFO);      
    }
    
    @Test
    public void testStaticNulls() {
        new NullPointerTester()
            .testAllPublicStaticMethods(UriEncoding.class);
    }
}
