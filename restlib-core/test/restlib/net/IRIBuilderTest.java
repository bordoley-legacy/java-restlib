package restlib.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;
import com.google.common.testing.NullPointerTester;

public final class IRIBuilderTest {
    private static void doTestBuild_withInvalidState(final IRIBuilder builder) {
        try {
            builder.build();
            fail("expected IllegalStateException");
        } catch (final IllegalStateException expected){}
    }
    
    private static void doTestSetAuthority_withInvalid(final String authority) {
        try {
            IRI.builder().setAuthority(authority);
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
    }
    
    private static void doTestSetPort_withInvalidPortsNumbers(final int port) {
        try {
            IRI.builder().setPort(port);
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
    }
    
    @Test
    public void testBuild() {
        assertEquals(
                IRI.parse("http://userinfo@host:80/path?query#fragment"),
                IRI.builder()
                    .setScheme(UriSchemes.HTTP)
                    .setUserinfo("userinfo")
                    .setHost("host")
                    .setPort(80)
                    .setPath("/path")
                    .setQuery("query")
                    .setFragment("fragment")
                    .build());
        
        assertEquals(
                IRI.parse("http://userinfo@host:80/path?query#fragment"),
                IRI.builder()
                    .setScheme(UriSchemes.HTTP)
                    .setAuthority("userinfo@host:80")
                    .setPath("/path")
                    .setQuery("query")
                    .setFragment("fragment")
                    .build());
        
        assertEquals(
                IRI.parse("http://userinfo@host/path?query#fragment"),
                IRI.builder()
                    .setScheme(UriSchemes.HTTP)
                    .setAuthority("userinfo@host")
                    .setPath("/path")
                    .setQuery("query")
                    .setFragment("fragment")
                    .build());
        
        assertEquals(
                IRI.parse("http://host:80/path?query#fragment"),
                IRI.builder()
                    .setScheme(UriSchemes.HTTP)
                    .setAuthority("host:80")
                    .setPath("/path")
                    .setQuery("query")
                    .setFragment("fragment")
                    .build());
        
        assertEquals(
                IRI.parse("http:/path?query#fragment"),
                IRI.builder()
                    .setScheme(UriSchemes.HTTP)
                    .setAuthority("")
                    .setPath("/path")
                    .setQuery("query")
                    .setFragment("fragment")
                    .build());
        
        assertEquals(
                IRI.parse("tag:path"),
                IRI.builder()
                    .setScheme(UriSchemes.TAG)
                    .setPath("path")
                    .build());
        
        assertEquals(
                IRI.parse("http://192.168.1.1"),
                IRI.builder()
                    .setScheme(UriSchemes.HTTP)
                    .setHost("192.168.1.1")
                    .build());
        
        assertEquals(
                IRI.parse("http://[3ffe:2a00:100:7031::1]"),
                IRI.builder()
                    .setScheme(UriSchemes.HTTP)
                    .setHost("[3ffe:2a00:100:7031::1]")
                    .build());
        
        assertEquals(
                IRI.parse("http://www.example.com"),
                IRI.builder()
                    .setScheme(UriSchemes.HTTP)
                    .setHost(InternetDomainName.from("www.example.com"))
                    .build());
        
        assertEquals(
                IRI.parse("http://[3ffe:2a00:100:7031::1]"),
                IRI.builder()
                    .setScheme(UriSchemes.HTTP)
                    .setHost(InetAddresses.forUriString("[3ffe:2a00:100:7031::1]"))
                    .build());
    }
    
    @Test
    public void testBuild_withInvalidState() {
        doTestBuild_withInvalidState(
                IRI.builder().setHost("www.example.com").setPath("a/b/c"));
        doTestBuild_withInvalidState(
                IRI.builder().setPath("//a/b/c"));
        doTestBuild_withInvalidState(
                IRI.builder().setPath("a:b/c"));
        doTestBuild_withInvalidState(
                IRI.builder().setPort(80));
        doTestBuild_withInvalidState(
                IRI.builder().setUserinfo("userinfo"));
        doTestBuild_withInvalidState(
                IRI.builder().setUserinfo("userinfo").setPort(80));       
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
            .testAllPublicInstanceMethods(IRI.builder());
    }
    
    @Test
    public void testSetAuthority_withInvalid() {
        doTestSetAuthority_withInvalid("userinfo@");
        doTestSetAuthority_withInvalid("userinfo@:80");
        doTestSetAuthority_withInvalid(":80");
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testSetHost_withInvalid() {
        IRI.builder().setHost("[invalid]");
    }
    
    @Test
    public void testSetPort_withInvalidPortsNumbers() {
        doTestSetPort_withInvalidPortsNumbers(-1);
        doTestSetPort_withInvalidPortsNumbers(66000);
    }
}
