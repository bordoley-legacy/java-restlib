package restlib.data;

import static org.junit.Assert.assertFalse;

import java.nio.CharBuffer;

import org.junit.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class ExpectationTest {
    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(
                    Expectation.EXPECTS_100_CONTINUE, 
                    Expectation.create(ImmutableMultimap.of("100-Continue", "")),
                    Expectation.create(ImmutableMultimap.of("100-coNtinUe", "")),
                    Expectation.PARSER.parse(CharBuffer.wrap("100-CONTINUE")).get())
            .addEqualityGroup(
                    Expectation.create(ImmutableMultimap.of("token", "word1", "token", "word2")), 
                    Expectation.create(ImmutableMultimap.of("token", "word2", "toKen", "word1")),
                    Expectation.PARSER.parse(CharBuffer.wrap("token=word1 ;    token =   \"word2\"")).get())
            .addEqualityGroup(
                    Expectation.create(ImmutableMultimap.of("token", "WORD1", "token", "WORD2")), 
                    Expectation.create(ImmutableMultimap.of("token", "WORD2", "toKen", "WORD1")),      
                    Expectation.PARSER.parse(CharBuffer.wrap("token=WORD1 ;    token =   \"WORD2\"")).get())
            .testEquals();        
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
            .testAllPublicStaticMethods(Expectation.class);
        new NullPointerTester()
            .testAllPublicInstanceMethods(Expectation.EXPECTS_100_CONTINUE);
        new NullPointerTester()
            .testAllPublicInstanceMethods(Expectation.PARSER);
    }
    
    @Test
    public void testPARSER$parse() {
        assertFalse(Expectation.PARSER.parse(CharBuffer.wrap("")).isPresent());
        assertFalse(Expectation.PARSER.parse(CharBuffer.wrap("@ab=ui")).isPresent());
    }
}
