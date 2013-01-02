package restlib.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.nio.CharBuffer;

import org.junit.Test;

import restlib.net.HostPort;

import com.google.common.base.Optional;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class WarningTest {
    private static void doTestPARSER$parse(
            final String test, final int warnCode,
            final HostPort warnAgent, final String warnText, final Optional<HttpDate> warnDate) {
        final CharBuffer buffer = CharBuffer.wrap(test);
        final Warning warning = Warning.PARSER.parse(buffer).get();
        assertEquals(warnCode, warning.warnCode());
        assertEquals(warnAgent, warning.warnAgent());
        assertEquals(warnText, warning.warnText());
        assertEquals(warnDate, warning.warnDate());
    }
    
    private static void doTestPARSER$parse_withInvalid(final String test) {
        final CharBuffer buffer = CharBuffer.wrap(test);
        try {
            final Optional<Warning> warning = Warning.PARSER.parse(buffer);
            assertFalse(warning.isPresent());
        } catch (final IllegalArgumentException expected) {}
    }
    
    @Test
    public void testCreate() {       
        try {
            Warning.create(-100, "www.example.com", "warn text");
            fail("Excected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
        
        try {
            Warning.create(10000, "www.example.com", "warn text");
            fail("Excected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
        
        try {
            Warning.create(100, "", "warn text");
            fail("Excected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
    }
    
	@Test
	public void testEquals() {
		new EqualsTester()
			.addEqualityGroup(
					Warning.create(100, 
							HostPort.hostOnly("www.example.com"),
							"warn text"),
					Warning.create(100, 
							HostPort.hostOnly("www.example.com"),
							"warn text"))
			.addEqualityGroup(
                    Warning.create(100, 
                            HostPort.hostOnly("www.example.com"),
                            "warn text",
                            HttpDate.create(123)),
                    Warning.create(100, 
                            HostPort.hostOnly("www.example.com"),
                            "warn text",
                            HttpDate.create(123)))				
			.addEqualityGroup(
					Warning.create(101, 
							"www.example.com",
							"warn text"),
					Warning.create(101, 
							"www.example.com",
							"warn text"))	
			.addEqualityGroup(
                    Warning.create(101, 
                            "www.example.com",
                            "warn text2"),
                    Warning.create(101, 
                            "www.example.com",
                            "warn text2"))   				
			.addEqualityGroup(
                    Warning.create(101, 
                            "www.example.com",
                            "warn text",
                            HttpDate.create(123)),
                    Warning.create(101, 
                            "www.example.com",
                            "warn text",
                            HttpDate.create(123)))		
            .addEqualityGroup(
                    Warning.create(101, 
                            "example.com",
                            "warn text",
                            HttpDate.create(123)),
                    Warning.create(101, 
                            "example.com",
                            "warn text",
                            HttpDate.create(123)))                       
			.testEquals();
	}
	
    @Test
    public void testNulls() {
        new NullPointerTester()
                .testAllPublicInstanceMethods(
                        Warning.create(101, 
                            "www.example.com",
                            "warn text",
                            HttpDate.create(123)));
        new NullPointerTester()
                .setDefault(HostPort.class, HostPort.hostOnly("www.example.com"))
                .setDefault(HttpDate.class, HttpDate.now())
        		.testAllPublicStaticMethods(Warning.class);
    }
    
    @Test
    public void testPARSER$parse() {
        doTestPARSER$parse("100 www.example.com \"warn text\"", 100, HostPort.hostOnly("www.example.com"), "warn text", Optional.<HttpDate> absent());
        doTestPARSER$parse("100 www.example.com \"warn text\" \"Sun, 06 Nov 1994 08:49:37 GMT\"", 100, HostPort.hostOnly("www.example.com"), "warn text", Optional.of(HttpDate.parse("Sun, 06 Nov 1994 08:49:37 GMT")));
        doTestPARSER$parse_withInvalid("");
        doTestPARSER$parse_withInvalid("-100 www.example.com \"warn text\"");
        doTestPARSER$parse_withInvalid("100 <>@ \"warn text\"");
        doTestPARSER$parse_withInvalid("100 example.com warntext");
    }
}
