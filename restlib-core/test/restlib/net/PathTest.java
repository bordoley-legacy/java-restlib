package restlib.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class PathTest {
    private static void doTestCanonicalize(final String expected, final String test) {
        assertEquals(Path.parse(expected), Path.parse(test).canonicalize());
    }
    
    private static void doTestMerge(final String expected, final String base, final String relative) {
        assertEquals(Path.parse(expected), Path.merge(IRI.parse(base), IRI.parse(relative)));
    }
    
    private static void doTestRemoveDotSegments(final String expected, final String test) {
        assertEquals(Path.parse(expected), Path.parse(test).removeDotSegments());
    }
    
    private static void doTestToIRI(final String expected, final String test) {
        assertEquals(Path.parse(expected), Path.parse(test).toIRIPath());
    }
    
    private static void doTestToUri(final String expected, final String test) {
        assertEquals(Path.parse(expected), Path.parse(test).toUriPath());
    }
    
    @Test
    public void tesCanonicalize() {
        doTestCanonicalize("/a/b/c", "/a/b/c");
        
        // FIXME: Should Path.NONE really canonicalize to '/' ?
        assertEquals(Path.of().canonicalize(), Path.copyOf(ImmutableList.of("")).canonicalize());
        doTestCanonicalize("a","a");
        doTestCanonicalize("/", "/");
        doTestCanonicalize("a", "a/");
        doTestCanonicalize("/a", "/a/");
        doTestCanonicalize("a/b", "a/b");
        
        doTestCanonicalize("/a/b", "/a/b/");
        doTestCanonicalize("/", "///////");
        doTestCanonicalize("a/b/c", "a////b///c");      
    }
    
    @Test
    public void testEquals(){
        new EqualsTester()
            .addEqualityGroup(
                    Path.of(), Path.parse(""), Path.copyOf(ImmutableList.<String> of()))
            .addEqualityGroup(
                    Path.parse("a/b/c"), Path.copyOf(ImmutableList.of("a","b","c")))
            .addEqualityGroup(
                    Path.parse("/a/b/"), Path.copyOf(ImmutableList.of("","a","b","")))
            .addEqualityGroup(
                    Path.parse("/a/b"), Path.copyOf(ImmutableList.of("","a","b")))     
             .addEqualityGroup(
                    Path.parse("/"), Path.copyOf(ImmutableList.of("","")))         
            .testEquals();                    
    }
    
    @Test
    public void testIsNormalized() {
        assertTrue(Path.parse("/a/b/c").isNormalized());
        assertFalse(Path.parse("/a/./b/c").isNormalized());
        assertFalse(Path.parse("/a/../b/c").isNormalized());
    }
    
    @Test
    public void testIsPathAbEmpty() {
        assertTrue(Path.of().isPathAbEmpty());
        assertTrue(Path.parse("/").isPathAbEmpty());
        assertTrue(Path.parse("/a").isPathAbEmpty());
        assertFalse(Path.parse("a/").isPathAbEmpty());
    }
    
    @Test
    public void testIsPathAbsolute() {
        assertFalse(Path.of().isPathAbsolute());
        
        assertTrue(Path.parse("/").isPathAbsolute());
        assertTrue(Path.parse("/a").isPathAbsolute());
        assertFalse(Path.parse("a/").isPathAbsolute());
        
        assertTrue(Path.parse("/a/b/c").isPathAbsolute());
        assertFalse(Path.parse("//a/b/c").isPathAbsolute());
        assertFalse(Path.parse("a//a/b/c").isPathAbsolute());
    }
    
    @Test
    public void testIsPathNoScheme() {
        assertTrue(Path.of().isPathNoScheme());
        assertTrue(Path.parse("/a/b/c").isPathNoScheme());
        assertFalse(Path.parse("a:b/c").isPathNoScheme());      
    }
    
    @Test
    public void testIsIRI() {
        assertTrue(Path.parse("/a/b/c").isIRIPath());
        assertTrue(Path.parse("/a/b\u00E9/c").isIRIPath());
 
        // Path with iprivate char
        assertFalse(Path.parse("/a/b\u00E9/\uDB80\uDC00").isIRIPath());
    }
    
    @Test
    public void testIsUri() {
        assertTrue(Path.parse("/a/b/c").isUriPath());
        assertFalse(Path.parse("/a/b\u00E9/c").isUriPath());
        assertFalse(Path.parse("/a/b\u00E9/\uDB80\uDC00").isIRIPath());
    }
    
    @Test
    public void testMerge() {
        // FIXME: Is this right?
        doTestMerge("//","http://example.com","/");
        
        doTestMerge("/a/b/c/d","http://example.com","a/b/c/d");
        doTestMerge("/a/b/c/d/e/f", "http://example.com/a/b/c/", "d/e/f");
        doTestMerge("/a/b/d/e/f", "http://example.com/a/b/c", "d/e/f");
        doTestMerge("/a/b//d/e/f", "http://example.com/a/b/c", "/d/e/f");
   
        doTestMerge("bob@example.com/a/b/c", "mailto:bob@example.com/", "a/b/c");
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
            .setDefault(Path.class, Path.of())
            .testAllPublicStaticMethods(Path.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testOf_withInvalid() {
        Path.copyOf(ImmutableList.of("a", "a/b", "c"));
    }
    
    @Test
    public void testRemoveDotSegments() {
        doTestRemoveDotSegments("","");
        doTestRemoveDotSegments("/a/b/c/d", "/a/b/c/d");
        doTestRemoveDotSegments("/a/b/c/d", "/a/b/c/d");
        doTestRemoveDotSegments("/a/b/c/d", "/a/b/c/d");
        doTestRemoveDotSegments("/a/c","/a/./c");
        doTestRemoveDotSegments("/c","/a/../c");
        doTestRemoveDotSegments("/a/","/a/.");
        doTestRemoveDotSegments("//","//../");
        doTestRemoveDotSegments("//","//..");
        
        // FIXME: Is this right?
        doTestRemoveDotSegments("/","..//");       
    }
    
    @Test
    public void testSegments() {
        assertTrue(
                Iterables.elementsEqual(
                        ImmutableList.of("","a","b","c"), Path.parse("/a/b/c").segments()));
    }
    
    @Test
    public void testStartsWithDoubleSlash() {
        assertTrue(Path.parse("//").startsWithDoubleSlash());
        assertTrue(Path.parse("//a/b").startsWithDoubleSlash());
        assertFalse(Path.parse("/").startsWithDoubleSlash());
        assertFalse(Path.parse("a//b").startsWithDoubleSlash());
    }
    
    @Test
    public void testStartsWithSlash() {
        assertTrue(Path.parse("/").startsWithSlash());
        assertFalse(Path.parse("a/").startsWithSlash());
        assertFalse(Path.of().startsWithSlash());
    }
    
    @Test
    public void testToIRI() {
        doTestToIRI("/a/b/c", "/a/b/c");
        doTestToIRI("/a/b%20/c", "/a/b /c");
        doTestToIRI("/a/b/c", "/%61/b/c");
        doTestToIRI("/a/b\u00E9/c", "/a/b%C3%A9/c");
    }
    
    @Test
    public void testToUri() {
        doTestToUri("/a/b/c", "/a/b/c");
        doTestToUri("/a/b%20/c", "/a/b /c");
        doTestToUri("/a/b/c", "/%61/b/c");
        doTestToUri("/a/b%C3%A9/c", "/a/b\u00E9/c");
    }
    
    
}
