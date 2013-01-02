package restlib.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class HostPortTest {
    
    private static boolean validateHostPort(final HostPort hp, final String host, final int port) {
        return hp.host().equals(host) && 
                (hp.port().isPresent() ? hp.port().get().equals(port) : port == -1);
    }
    
    @Test
    public void testCreate_withInvalidHosts() {
        try {
            // Invalid almost IP Address
            HostPort.create("192.168.1.1.1", 80);
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
        
        try {
            // IPv6 address without brackets
            HostPort.create("3ffe:2a00:100:7031::1", 80);
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
        
        try {
            // Empty host string
            HostPort.create("", 80);
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
    }
    
    @Test
    public void testCreate_withInvalidPorts() {
        try {
            HostPort.create("www.example.com", -2);
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
        
        try {
            HostPort.create("www.example.com", 70000);
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
        
        try {
            HostPort.create("www.example.com", 0);
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
    }
    
    @Test
    public void testCreate_withValid() {
        assertTrue(validateHostPort(HostPort.create("www.example.com", 80), "www.example.com", 80));
        assertTrue(validateHostPort(HostPort.create("www.example.com", -1), "www.example.com", -1));
        assertTrue(validateHostPort(HostPort.create("192.168.1.1", -1), "192.168.1.1", -1));
        assertTrue(validateHostPort(HostPort.create("[3ffe:2a00:100:7031::1]", -1), "[3ffe:2a00:100:7031::1]", -1));
    }
    
    @Test
    public void testEquals() {
        new EqualsTester()
                .addEqualityGroup(
                        HostPort.parse("example.com:80"), 
                        HostPort.create("example.com", 80))
                .addEqualityGroup(
                        HostPort.parse("example.com"), 
                        HostPort.hostOnly("example.com"))
                .addEqualityGroup(
                        HostPort.parse("192.168.1.1:80"), 
                        HostPort.create("192.168.1.1", 80))  
                .addEqualityGroup(
                        HostPort.parse("[1080::8:800:200c:417a]:80"), 
                        HostPort.create("[1080::8:800:200c:417a]", 80))        
                .testEquals();
    }
    
    @Test
    public void testNulls() {
        final NullPointerTester tester = new NullPointerTester();
        tester.testAllPublicStaticMethods(HostPort.class);
    }
    
    @Test
    public void testToString() {
        final String[] tests = 
            {"[1080::8:800:200c:417a]:80",
             "[1080::8:800:200c:417a]",
             "192.168.1.1:80",
             "192.168.1.1",
             "example.com:80",
             "example.com"
            };
        for (final String test : tests){
            assertEquals(test, HostPort.parse(test).toString());
        }
    }
}
