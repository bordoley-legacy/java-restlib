package restlib.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.nio.CharBuffer;
import java.util.Locale;

import org.junit.Test;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class LanguageTest {
    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(Language.ANY, Language.create("*"))
            .addEqualityGroup(Language.create("en-us"), Language.forLocale(Locale.US))
            .addEqualityGroup(Language.create("fr-fr"), Language.forLocale(Locale.FRANCE))
            .testEquals();
    }
    
    @Test
    public void testMatch() {
        assertEquals(0, Language.create("en-us").match(Language.create("fr-fr")));
        assertEquals(1000, Language.create("en-us").match(Language.create("en-us")));
        assertEquals(500, Language.ANY.match(Language.create("en-us")));
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
            .testAllPublicStaticMethods(Language.class);
        new NullPointerTester()
            .testAllPublicInstanceMethods(Language.create("en-us"));
        new NullPointerTester()
            .testAllPublicInstanceMethods(Language.PARSER);
    }
    
    @Test
    public void testPARSER$parse() {
        assertEquals(
                Language.create("en-us"), 
                Language.PARSER.parse(CharBuffer.wrap("en-us")).get());
        assertFalse(
                Language.PARSER.parse(CharBuffer.wrap("")).isPresent());
        assertFalse(
                Language.PARSER.parse(CharBuffer.wrap("@abc")).isPresent());
    }
    
    @Test
    public void testToLocale() {
        assertEquals(Locale.US, Language.create("en-us").toLocale());
    }
}
