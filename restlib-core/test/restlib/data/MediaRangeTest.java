package restlib.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class MediaRangeTest { 
    private static void doTestCreate_withInvalid(
            final String type, final String subtype) {
        try {
            MediaRange.create(type, subtype);
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
    }
    
    private static void doTestCreate_withInvalid(
            final String type, final String subtype, 
            final Multimap<String, String> parameters) {
        try {
            MediaRange.create(type, subtype, parameters);
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
    }
    
    private static void doTestCreate_withInvalid(
            final String type, final String subtype, 
            final Charset charset) {
        try {
            MediaRange.create(type, subtype, charset);
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
    }
    
    private static void doTestCreate_withInvalid(
            final String type, final String subtype, 
            final Charset charset,
            final Multimap<String, String> parameters) {
        try {
            MediaRange.create(type, subtype, charset, parameters);
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
    }
    
    private static void doTestParse(
            final String test, final String type, 
            final String subtype, final Optional<Charset> charset,
            final Multimap<String, String> parameters) {
        final MediaRange result = MediaRange.parse(test);
        assertEquals(type, result.type());
        assertEquals(subtype, result.subtype());
        assertEquals(charset, result.charset());
        assertEquals(parameters, result.parameters());
    }
    
    private static void doTestParse_withInvalid(final String test){
        try {
            MediaRange.parse(test);
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){};
    }
    
    @Test
    public void testCreate() {
        doTestCreate_withInvalid("a", "@");
        doTestCreate_withInvalid("@", "b");
        doTestCreate_withInvalid("a", "b", Charset.ANY);
        doTestCreate_withInvalid("a", "b", ImmutableMultimap.of("charset", "UTF-8"));
        doTestCreate_withInvalid("a", "b", ImmutableMultimap.of("q", "1.0"));
    }
    
    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(
                MediaRanges.APPLICATION_ANY, 
                MediaRange.parse("application/*"), 
                MediaRange.create("application", "*"))
            .addEqualityGroup(
                MediaRanges.APPLICATION_ATOM,
                MediaRange.parse("aPpLiCaTioN/AtOm+xmL"),
                MediaRange.create("applicAtion", "atoM+xml"))
            .addEqualityGroup(
                MediaRanges.APPLICATION_ATOM_ENTRY,
                MediaRange.parse("application/atom+xml; type = \"entry\""),
                MediaRange.create("application", "atom+xml", ImmutableMultimap.of("type", "entry")))
            .addEqualityGroup(
                MediaRanges.APPLICATION_ATOM.withCharset(Charset.UTF_8),
                MediaRange.parse("application/atom+xml; charSet = utf-8"),
                MediaRange.create("application", "atom+xml", Charset.UTF_8))      
            .addEqualityGroup(
                MediaRanges.APPLICATION_ATOM_ENTRY.withCharset(Charset.UTF_8),
                MediaRange.parse("application/atom+xml; type = \"entry\"     \t\t\t; charSet = utf-8"),
                MediaRange.create("application", "atom+xml", Charset.UTF_8, ImmutableListMultimap.of("tyPe", "entry")))                
            .addEqualityGroup(
                MediaRanges.TEXT_HTML,
                MediaRange.parse("text/html"),
                MediaRange.create("text", "html"))
            .testEquals();
    }

    @Test
    public void testMatch() {
        assertEquals(0, MediaRanges.APPLICATION_ATOM.match(MediaRanges.APPLICATION_JSON));
        assertEquals(0, MediaRanges.APPLICATION_ATOM.match(MediaRanges.TEXT_HTML));
        assertEquals(250, MediaRange.ANY.match(MediaRanges.APPLICATION_ATOM_ENTRY));
        assertEquals(500, MediaRanges.APPLICATION_ANY.match(MediaRanges.APPLICATION_ATOM_ENTRY));
        assertEquals(750, MediaRange.parse("a/b; c=d; e=f").match(MediaRange.parse("a/b; c=d")));
        assertEquals(0, MediaRange.parse("a/b; e=f").match(MediaRange.parse("a/b; c=d")));
        assertEquals(1000, MediaRanges.APPLICATION_ATOM_ENTRY.match(MediaRanges.APPLICATION_ATOM_ENTRY));
        
        try {
            MediaRanges.APPLICATION_ATOM_ENTRY.withCharset(Charset.UTF_8).match(MediaRanges.APPLICATION_ATOM_ENTRY);
            fail("Expected IllegalStateException");
        } catch (final IllegalStateException expected){}
        
        // Ignore the charset parameter for matching.
        assertEquals(1000, MediaRanges.APPLICATION_ATOM_ENTRY.match(MediaRanges.APPLICATION_ATOM_ENTRY.withCharset(Charset.UTF_8)));
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
            .setDefault(Charset.class, Charset.UTF_8)
            .testAllPublicInstanceMethods(MediaRange.ANY);
        new NullPointerTester()
            .setDefault(Charset.class, Charset.UTF_8)
            .testAllPublicStaticMethods(MediaRange.class);
    }
    
    @Test
    public void testParse() {
        doTestParse("a/b", "a", "b", Optional.<Charset> absent(), ImmutableMultimap.<String, String> of());
        doTestParse("a/b; charset =  \t UTF-8", "a", "b", Optional.of(Charset.UTF_8), ImmutableMultimap.<String, String> of());
        doTestParse("a/b; c=d; e=f", "a", "b",  Optional.<Charset> absent(), ImmutableSetMultimap.<String, String> of("c", "d", "e", "f"));
        
        doTestParse_withInvalid("");
        doTestParse_withInvalid("a");
        doTestParse_withInvalid("a/");
        doTestParse_withInvalid("@/b");
        doTestParse_withInvalid("b/@");
        doTestParse_withInvalid("a/b; charset=\" \\\" \"" );
        doTestParse_withInvalid("a/b; c=d; q=1.0; e=\"f\"");
        doTestParse_withInvalid("a/b; charset=US-ASCII; c=d; charset=UTF-8");
        doTestParse_withInvalid("a/b; charset=*");
    }
    
    @Test
    public void testWithCharset() {
        try {
            MediaRanges.APPLICATION_ATOM.withCharset(Charset.ANY);
        } catch (final IllegalArgumentException expected){}
        
        try {
            MediaRange.ANY.withCharset(Charset.UTF_8);
        } catch (final IllegalStateException expected){}
        
        try {
            MediaRanges.TEXT_ANY.withCharset(Charset.UTF_8);
        } catch (final IllegalStateException expected){}
    }
}
