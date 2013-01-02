package restlib.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.nio.CharBuffer;

import org.junit.Test;

import restlib.net.HostPort;

import com.google.common.base.Optional;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class ViaTest {
    private static void doTestPARSER$parse(
            final CharSequence test, final Protocol protocol, 
            final String pseudonym, final Optional<Comment> comment) {
        final CharBuffer buffer = CharBuffer.wrap(test);
        final Via via = Via.PARSER.parse(buffer).get();
        assertEquals(protocol, via.receivedProtocol());
        assertEquals(pseudonym, via.receivedBy());
        assertEquals(comment, via.comment());
    }
    
    private static void doTestPARSER$parse(
            final CharSequence test, final Protocol protocol, 
            final HostPort hostPort, final Optional<Comment> comment) {
        final CharBuffer buffer = CharBuffer.wrap(test);
        final Via via = Via.PARSER.parse(buffer).get();
        assertEquals(protocol, via.receivedProtocol());
        assertEquals(hostPort, via.receivedBy());
        assertEquals(comment, via.comment());
    }
    
    private static void doTestPARSER$parse_withInvalid(final CharSequence test) {
        final CharBuffer buffer = CharBuffer.wrap(test);
        final Optional<Via> via = Via.PARSER.parse(buffer);
        assertFalse(via.isPresent());
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testCreate_withInvalid() {
        Warning.create(100, "<>@", "");
    }
    
    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(
                    Via.create(Protocol.HTTP_1_1, HostPort.hostOnly("www.example.com")),
                    Via.create(Protocol.HTTP_1_1, HostPort.hostOnly("www.example.com")))
            .addEqualityGroup(
                    Via.create(Protocol.HTTP_1_0, HostPort.hostOnly("www.example.com")),
                    Via.create(Protocol.HTTP_1_0, HostPort.hostOnly("www.example.com")))
            .addEqualityGroup(
                    Via.create(Protocol.HTTP_1_0, HostPort.hostOnly("example.com")),
                    Via.create(Protocol.HTTP_1_0, HostPort.hostOnly("example.com"))) 
            .addEqualityGroup(
                    Via.create(Protocol.HTTP_1_1, HostPort.hostOnly("www.example.com"), Comment.parse("(test)")),
                    Via.create(Protocol.HTTP_1_1, HostPort.hostOnly("www.example.com"), Comment.parse("(test)")))
            .addEqualityGroup(
                    Via.create(Protocol.HTTP_1_1, HostPort.hostOnly("www.example.com"), Comment.parse("(test2)")),
                    Via.create(Protocol.HTTP_1_1, HostPort.hostOnly("www.example.com"), Comment.parse("(test2)")))
            .addEqualityGroup(
                    Via.create(Protocol.HTTP_1_1, "pseudonym+++"),
                    Via.create(Protocol.HTTP_1_1, "pseudonym+++"))
            .addEqualityGroup(
                    Via.create(Protocol.HTTP_1_1, "pseudonym+++", Comment.parse("(test)")),
                    Via.create(Protocol.HTTP_1_1, "pseudonym+++", Comment.parse("(test)")))     
            .addEqualityGroup(
                    Via.create(Protocol.HTTP_1_1, "example.com", Comment.parse("(test)")),
                    Via.create(Protocol.HTTP_1_1, "example.com", Comment.parse("(test)")))            
            .testEquals();                   
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
                .testAllPublicInstanceMethods(
                        Via.create(Protocol.HTTP_1_1, HostPort.hostOnly("www.exmample.com")));
        new NullPointerTester()
                .setDefault(Protocol.class, Protocol.HTTP_1_1)
                .setDefault(Comment.class, Comment.parse("()"))
                .setDefault(HostPort.class, HostPort.hostOnly("www.example.com"))
                .testAllPublicStaticMethods(Via.class);
    }
    
    @Test
    public void testPARSER$parse() {
        doTestPARSER$parse("HTTP/1.1 www.example.com:80", 
                Protocol.HTTP_1_1, HostPort.parse("www.example.com:80"), Optional.<Comment> absent());
        doTestPARSER$parse("1.1 www.example.com:80", 
                Protocol.HTTP_1_1, HostPort.parse("www.example.com:80"), Optional.<Comment> absent());
        doTestPARSER$parse("1.1 www.example.com:80", 
                Protocol.HTTP_1_1, HostPort.parse("www.example.com:80"), Optional.<Comment> absent());
        doTestPARSER$parse("1.1 www.example.com:80 (test comment)", 
                Protocol.HTTP_1_1, HostPort.parse("www.example.com:80"), Optional.of(Comment.parse("(test comment)")));
        doTestPARSER$parse("1.1 www.example.com:80 (test comment)", 
                Protocol.HTTP_1_1, HostPort.parse("www.example.com:80"), Optional.of(Comment.parse("(test comment)")));
        doTestPARSER$parse("1.1 pseudonym++ (test comment)", 
                Protocol.HTTP_1_1, "pseudonym++", Optional.of(Comment.parse("(test comment)")));
        doTestPARSER$parse_withInvalid("");
        doTestPARSER$parse_withInvalid("1.1");
        doTestPARSER$parse_withInvalid("1.1 <>@");
    }
}
