package restlib.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;
import com.google.common.testing.NullPointerTester;

public final class UriBuilderTest {  
    @Test
    public void testBuild() {  
        assertEquals(
                Uri.parse("http://userinfo@host:80/path?query#fragment"),
                Uri.builder()
                    .setScheme(UriSchemes.HTTP)
                    .setUserinfo("userinfo")
                    .setHost("host")
                    .setPort(80)
                    .setPath("/path")
                    .setQuery("query")
                    .setFragment("fragment")
                    .build());
        assertEquals(
                Uri.parse("http://userinfo@host:80/path?query#fragment"),
                Uri.builder()
                    .setScheme(UriSchemes.HTTP)
                    .setAuthority("userinfo@host:80")
                    .setPath("/path")
                    .setQuery("query")
                    .setFragment("fragment")
                    .build());
        
        assertEquals(
                Uri.parse("http://192.168.1.1"),
                Uri.builder()
                    .setScheme(UriSchemes.HTTP)
                    .setHost(InetAddresses.forUriString("192.168.1.1"))
                    .build());
        assertEquals(
                Uri.parse("http://xn--rsum-bpad.example.org"),
                Uri.builder()
                    .setScheme(UriSchemes.HTTP)
                    .setHost(InternetDomainName.from("r\u00E9sum\u00E9.example.org"))
                    .build());
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
            .testAllPublicInstanceMethods(Uri.builder());
    }
    
    @Test
    public void testSetFragment_withInvalid() {
        try {
            Uri.builder().setFragment("\u00E9");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){};
    }
    
    @Test
    public void testSetHost_withInvalid() {
        try {
            Uri.builder().setHost("r\u00E9sum\u00E9.example.org");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){};
    }
    
    @Test
    public void testSetPath_withInvalid() {
        try {
            Uri.builder().setPath("/red%09ros\u00E9#red");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){};
        
        try {
            Uri.builder().setPath("/a?b");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){};
        
        
        try {
            Uri.builder().setPath("/a#b");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){};
    }
    
    @Test
    public void testSetQuery_withInvalid() {
        try {
            Uri.builder().setQuery("#");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){};
        
        try {
            Uri.builder().setQuery("\u00E9");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){};
    }
    
    @Test
    public void testSetUserinfo_withInvalid() {
        try {
            Uri.builder().setUserinfo("@");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){};
        try {
            Uri.builder().setUserinfo("\u00E9");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){};
    }
}
