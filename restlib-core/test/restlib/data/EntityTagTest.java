package restlib.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.CharBuffer;

import org.junit.Test;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class EntityTagTest {
    private static void doTestStrongTag_withInvalid(final String test) {
        try {
            EntityTag.strongTag(test);
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
    }
    
    private static void doTestWeakTag_withInvalid(final String test) {
        try {
            EntityTag.weakTag(test);
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
    }
    
    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(EntityTag.weakTag("abc"), EntityTag.weakTag("abc"), EntityTag.PARSER.parse(CharBuffer.wrap("W/\"abc\"")).get())
            .addEqualityGroup(EntityTag.weakTag("def"), EntityTag.weakTag("def"), EntityTag.PARSER.parse(CharBuffer.wrap("W/\"def\"")).get())
            .addEqualityGroup(EntityTag.strongTag("abc"), EntityTag.strongTag("abc"), EntityTag.PARSER.parse(CharBuffer.wrap("\"abc\"")).get())
            .addEqualityGroup(EntityTag.strongTag("def"), EntityTag.strongTag("def"), EntityTag.PARSER.parse(CharBuffer.wrap("\"def\"")).get())
            .testEquals();
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
            .setDefault(EntityTag.class, EntityTag.strongTag("abc"))
            .testAllPublicStaticMethods(EntityTag.class);
        new NullPointerTester()
            .testAllPublicInstanceMethods(EntityTag.weakTag("abc"));
        new NullPointerTester()
            .testAllPublicInstanceMethods(EntityTag.strongTag("abc"));
        new NullPointerTester()
            .testAllPublicInstanceMethods(EntityTag.PARSER);
    }
    
    @Test
    public void testPARSER$parse() {
        assertFalse(EntityTag.PARSER.parse(CharBuffer.wrap("abc")).isPresent());
        assertFalse(EntityTag.PARSER.parse(CharBuffer.wrap("W/abc")).isPresent());
    }

    @Test
    public void testStrongCompare() {
        assertFalse(EntityTag.strongCompare(EntityTag.weakTag("1"), EntityTag.weakTag("1")));
        assertFalse(EntityTag.strongCompare(EntityTag.weakTag("1"), EntityTag.weakTag("2")));
        assertFalse(EntityTag.strongCompare(EntityTag.weakTag("1"), EntityTag.strongTag("1")));
        assertFalse(EntityTag.strongCompare(EntityTag.strongTag("1"), EntityTag.weakTag("1")));
        assertTrue(EntityTag.strongCompare(EntityTag.strongTag("1"), EntityTag.strongTag("1")));
    }
    
    @Test
    public void testStrongTag_withInvalid() {
        doTestStrongTag_withInvalid("");
        doTestStrongTag_withInvalid("a@\"");
    }
    
    @Test
    public void testWeakCompare() {
        assertTrue(EntityTag.weakCompare(EntityTag.weakTag("1"), EntityTag.weakTag("1")));
        assertFalse(EntityTag.weakCompare(EntityTag.weakTag("1"), EntityTag.weakTag("2")));
        assertTrue(EntityTag.weakCompare(EntityTag.weakTag("1"), EntityTag.strongTag("1")));
        assertTrue(EntityTag.weakCompare(EntityTag.strongTag("1"), EntityTag.strongTag("1")));
    }
    
    @Test
    public void testWeakTag_withInvalid() {
        doTestWeakTag_withInvalid("");
        doTestWeakTag_withInvalid("a@\"");
    }
}
