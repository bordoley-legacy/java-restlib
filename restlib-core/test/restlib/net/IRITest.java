package restlib.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class IRITest {
    private static void doTestCanonicalize(final String expected, final String test) {
        assertEquals(expected, IRI.parse(test).canonicalize().toString());
    }
    
    private static void doTestNormalize(final String expected, final String test) {
        assertEquals(expected, IRI.parse(test).normalize().toString());
    }
    
    private static void doTestParse(
            final String test, final String scheme, 
            final String userinfo, final String host,
            final Optional<Integer> port, final String path,
            final String query, final String fragment) {
        final IRI testIRI = IRI.parse(test);
        
        assertEquals(scheme, testIRI.scheme());
        assertEquals(userinfo, testIRI.userinfo());
        assertEquals(host, testIRI.host());
        assertEquals(port, testIRI.port());
        assertEquals(path, testIRI.path().toString());
        assertEquals(query, testIRI.query());
        assertEquals(fragment, testIRI.fragment());
    }
    
    private static void doTestRelativeReference(final String expected, final String base, final String relative) {
        final IRI baseIRI = IRI.parse(base);
        final IRI relativeIRI = IRI.parse(relative);
        final IRI expectedIRI = IRI.parse(expected);
        assertEquals(expectedIRI, IRI.relativeReference(baseIRI, relativeIRI));             
    }
    
    private static void doTestToIRI(final String expected, final String test){
        assertEquals(IRI.parse(expected), Uri.parse(test).toIRI());
    }
    
    private static void doTestToUri(final String expected, final String test){
        assertEquals(Uri.parse(expected), IRI.parse(test).toUri());
    }
    
    @Test 
    public void testCanonicalize() {       
        doTestCanonicalize("/a/b/c/", "/a/b/c/");      
        doTestCanonicalize("example.com/a/b/c/", "example.com/a/b/c/");
        doTestCanonicalize("http://example.com/", "http://example.com");
        doTestCanonicalize("http://example.com/", "http://example.com/");
        doTestCanonicalize("http://example.com/", "http://example.com/////");
        doTestCanonicalize("http://example.com/a", "http://example.com/a/");
        doTestCanonicalize("http://example.com/a/b", "http://example.com/a//b/");
        doTestCanonicalize("http:a/b/c", "http:a/b/c/");
        doTestCanonicalize("http:/", "http:");
        doTestCanonicalize("http:a/b", "http:a//b");
        doTestCanonicalize("http://example.com/a/b/c", "http://example.com/a/b/c");
        doTestCanonicalize("tag:a", "tag:a/");
        doTestCanonicalize("tag:a/b", "tag:a/b");
        doTestCanonicalize("tag:a", "tag:a");
        doTestCanonicalize("http://www.example.com/", "http://www.example.com");
        doTestCanonicalize("http://www.example.com/test", "http://www.example.com/test/");
        
        // IRIs without a scheme aren't canonicalized.
        assertFalse(IRI.parse("www.example.com").canonicalize().toString().equals("www.example.org/"));
    }
    
    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(
                    IRI.parse("http://test@example.com:80/path?query#fragment"),
                    IRI.parse("http://test@example.com:80/path?query#fragment"))
            .addEqualityGroup(
                    IRI.parse("http://test@example.com:80/path?query"),
                    IRI.parse("http://test@example.com:80/path?query"))
            .addEqualityGroup(
                    IRI.parse("http://test@example.com:80/path"),
                    IRI.parse("http://test@example.com:80/path"))
            .addEqualityGroup(
                    IRI.parse("http://test@example.com:80"),
                    
                    IRI.parse("http://test@example.com:80"))
            .addEqualityGroup(
                    IRI.parse("http://test@example.com"),
                    IRI.parse("http://test@example.com"))     
            .addEqualityGroup(
                    IRI.parse("http://example.com"),
                    IRI.parse("http://example.com"))                      
            .addEqualityGroup(
                    IRI.parse("http://"),
                    IRI.parse("http://"))
            .testEquals();        
    }
    
    @Test 
    public void testIsAbsolute() {
        assertTrue(IRI.parse("http://www.example.com").isAbsolute());
        assertFalse(IRI.parse("http://www.example.com#fragment").isAbsolute());
        assertFalse(IRI.parse("www.example.com").isAbsolute());
    }
    
    @Test
    public void testNormalize() {
        doTestNormalize("http://www.example.org", "HTTP://WWw.ExAmPlE.org");
        doTestNormalize("http://r\u00E9sum\u00E9.example.org", "http://r\u00E9sum\u00E9.eXaMpLe.org");
        doTestNormalize("http://192.168.1.1", "http://192.168.1.1");
        doTestNormalize("http://[3ffe:2a00:100:7031::1]", "http://[3ffe:2a00:100:7031::1]");
        
        // Don't normalize non absolute IRIs
        assertFalse(IRI.parse("www.ExAmPlE.org").normalize().toString().equals("www.example.org"));
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
            .setDefault(IRI.class, IRI.parse("http://www.example.com"))
            .testAllPublicStaticMethods(IRI.class);
    }
    
    @Test
    public void testParse() {
        // Absolute IRI with authority path-abempty
        doTestParse(
                "scheme://userinfo@host:80/path?query#fragment",
                "scheme", "userinfo", "host", Optional.of(80), "/path", "query", "fragment");
        
        // Absolute IRI with path-absolute
        doTestParse(
                "scheme:/a",
                "scheme", "", "", Optional.<Integer>absent(), "/a", "", "");
        
        // Absolute IRI with path-rootless
        doTestParse(
                "scheme:a",
                "scheme", "", "", Optional.<Integer>absent(), "a", "", "");
        
        // Absolute IRI with path-empty
        doTestParse(
                "scheme:?query#fragment",
                "scheme", "", "", Optional.<Integer>absent(), "", "query", "fragment");
        
        // Relative reference with authority (network path)
        doTestParse(
                "//userinfo@host:80/path?query#fragment",
                "", "userinfo", "host", Optional.of(80), "/path", "query","fragment");
        
        // Relative reference with path absolute
        doTestParse("/path?query#fragment",
                "", "", "", Optional.<Integer>absent(), "/path", "query", "fragment");
        
        // Relative reference with path noscheme
        doTestParse("path?query#fragment",
                "", "", "", Optional.<Integer>absent(), "path", "query", "fragment");
        
        // Relative reference with path empty
        doTestParse("?query#fragment",
                "", "", "", Optional.<Integer>absent(), "", "query", "fragment");
    }
    
    @Test
    public void testRelativeReference() {
        final String rfcTestsBase = "http://a/b/c/d;p?q";
        doTestRelativeReference("g:h", rfcTestsBase, "g:h"); 
        doTestRelativeReference("http://a/b/c/g", rfcTestsBase, "g");
        doTestRelativeReference("http://a/b/c/g", rfcTestsBase, "./g");
        doTestRelativeReference("http://a/b/c/g/", rfcTestsBase, "g/");
        doTestRelativeReference("http://a/g", rfcTestsBase, "/g");
        doTestRelativeReference("http://g", rfcTestsBase, "//g");
        doTestRelativeReference("http://a/b/c/d;p?y", rfcTestsBase, "?y");
        doTestRelativeReference("http://a/b/c/g?y", rfcTestsBase, "g?y");
        doTestRelativeReference("http://a/b/c/d;p?q#s", rfcTestsBase, "#s");
        doTestRelativeReference("http://a/b/c/g#s", rfcTestsBase, "g#s");
        doTestRelativeReference("http://a/b/c/g?y#s", rfcTestsBase, "g?y#s");
        doTestRelativeReference("http://a/b/c/;x", rfcTestsBase, ";x");
        doTestRelativeReference("http://a/b/c/g;x", rfcTestsBase, "g;x");
        doTestRelativeReference("http://a/b/c/g;x?y#s", rfcTestsBase, "g;x?y#s");
        doTestRelativeReference("http://a/b/c/d;p?q", rfcTestsBase, "");
        doTestRelativeReference("http://a/b/c/", rfcTestsBase, ".");
        doTestRelativeReference("http://a/b/c/", rfcTestsBase, "./");
        doTestRelativeReference("http://a/b/", rfcTestsBase, "..");
        doTestRelativeReference("http://a/b/", rfcTestsBase, "../");
        
        // Abnormal Examples
        doTestRelativeReference("http://a/b/g", rfcTestsBase, "../g");
        doTestRelativeReference("http://a/", rfcTestsBase, "../..");
        doTestRelativeReference("http://a/", rfcTestsBase, "../../");
        doTestRelativeReference("http://a/g", rfcTestsBase, "../../g");
        doTestRelativeReference("http://a/g", rfcTestsBase, "../../../g");
        doTestRelativeReference("http://a/g", rfcTestsBase, "../../../../g");
        doTestRelativeReference("http://a/g", rfcTestsBase, "/./g");
        doTestRelativeReference("http://a/g", rfcTestsBase, "/../g");
        doTestRelativeReference("http://a/b/c/g.", rfcTestsBase, "g.");
        doTestRelativeReference("http://a/b/c/.g", rfcTestsBase, ".g");
        doTestRelativeReference("http://a/b/c/g..", rfcTestsBase, "g..");
        doTestRelativeReference("http://a/b/c/..g", rfcTestsBase, "..g"); 
        doTestRelativeReference("http://a/b/g", rfcTestsBase, "./../g");
        doTestRelativeReference("http://a/b/c/g/", rfcTestsBase, "./g/.");
        doTestRelativeReference("http://a/b/c/g/h", rfcTestsBase, "g/./h");
        doTestRelativeReference("http://a/b/c/h", rfcTestsBase, "g/../h");
        doTestRelativeReference("http://a/b/c/g;x=1/y", rfcTestsBase, "g;x=1/./y");
        doTestRelativeReference("http://a/b/c/y", rfcTestsBase, "g;x=1/../y");
        doTestRelativeReference("http://a/b/c/g?y/./x", rfcTestsBase, "g?y/./x");
        doTestRelativeReference("http://a/b/c/g?y/../x", rfcTestsBase, "g?y/../x");
        doTestRelativeReference("http://a/b/c/g#s/./x", rfcTestsBase, "g#s/./x");
        doTestRelativeReference("http://a/b/c/g#s/../x", rfcTestsBase, "g#s/../x");
        doTestRelativeReference("http:g", rfcTestsBase, "http:g");
        
        doTestRelativeReference("http://www.example.com/a/b/c", "http://www.example.com", "a/b/c");
    }
    
    @Test
    public void testToIRI() {
        doTestToIRI(
                "http://example.com/\uD800\uDF00\uD800\uDF01\uD800\uDF02",
                "http://example.com/%F0%90%8C%80%F0%90%8C%81%F0%90%8C%82");
        doTestToIRI(      
                "http://www.example.org/red%09ros\u00E9#red",
                "http://www.example.org/red%09ros%C3%A9#red");
        doTestToIRI(    
                "http://r\u00E9sum\u00E9.example.org",
                "http://xn--rsum-bpad.example.org");
    }
    
    @Test
    public void testToString() {
        final List<String> tests =
                ImmutableList.of(
                        "http://test@example.com:80/path?query#fragment",
                        "http://test@example.com:80/path?query",
                        "http://test@example.com:80/path",
                        "http://test@example.com:80",
                        "http://test@example.com",
                        "http:",
                        "ftp://example.com/",
                        "ftp://example.com/a/",
                        "http://a@example.com:80/a/b/c?d#e",
                        "#a/b/",
                        "/a/b/c/?d#e",
                        "mailto:chris@example.com",
                        "mailto:infobot@example.com?subject=current-issue",
                        "mailto:infobot@example.com?body=send%20current-issue",
                        "mailto:infobot@example.com?body=send%20current-issue%0D%0Asend%20index",
                        "mailto:foobar@example.com?In-Reply-To=%3C3469A91.D10AF4C@example.com",
                        "mailto:majordomo@example.com?body=subscribe%20bamboo-l",
                        "mailto:joe@example.com?cc=bob@example.com&body=hello",
                        "mailto:?to=joe@example.com&cc=bob@example.com&body=hello",
                        "mailto:gorby%25kremvax@example.com",
                        "mailto:unlikely%3Faddress@example.com?blat=foop",
                        "http://[fedc:ba98:7654:3210:fedc:ba98:7654:3210]:80/index.html",
                        "http://[1080::8:800:200c:417a]/index.html",
                        "http://[3ffe:2a00:100:7031::1]",
                        "http://[1080::8:800:200c:417a]/foo",
                        "http://[::192.9.5.5]/ipng",
                        "http://[::FFFF:129.144.52.38]:80/index.html",                   
                        "http://[2010:836b:4179::836b:4179]",
                        "http://192.168.1.1",
                        "ABC.com",
                        "?q=A%26B",
                        "#q=A%26B",
                        "/a/b%26/",
                        "tag:timothy@hpl.hp.com,2001:web/externalHome",
                        "tag:sandro@w3.org,2004-05:Sandro", 
                        "tag:my-ids.com,2001-09-15:TimKindberg:presentations:UBath2004-05-19",
                        "tag:blogger.com,1999:blog-555", 
                        "tag:yaml.org,2002:int",                 
                        "http://www.example.org/red%09ros\u00E9#red",
                        "http://www.example.org/red%09ros%C3%A9#red",
                        "http://r\u00E9sum\u00E9.example.org",
                        "http://xn--rsum-bpad.example.org");
        for (final String test : tests) {
            assertEquals(test, IRI.parse(test).toString());
        }
    }
    
    @Test
    public void testToUri() {
        doTestToUri(
                "http://example.com/%F0%90%8C%80%F0%90%8C%81%F0%90%8C%82",
                "http://example.com/\uD800\uDF00\uD800\uDF01\uD800\uDF02");
        doTestToUri(
                "http://www.example.org/red%09ros%C3%A9#red",
                "http://www.example.org/red%09ros\u00E9#red");
        doTestToUri(                     
                "http://xn--rsum-bpad.example.org",
                "http://r\u00E9sum\u00E9.example.org");
        doTestToUri(                         
                "http://192.168.1.1",
                "http://192.168.1.1");   
        doTestToUri(                         
                "http://[3ffe:2a00:100:7031::1]",
                "http://[3ffe:2a00:100:7031::1]");        
    }
}
