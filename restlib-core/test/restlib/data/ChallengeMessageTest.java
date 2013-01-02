package restlib.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.CharBuffer;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class ChallengeMessageTest {
    private static void doTestPARSER$Parse_withBase64(){}
    
    private static void doTestPARSER$Parse_withInvalid(final String test){
        assertFalse(ChallengeMessage.PARSER.parse(CharBuffer.wrap(test)).isPresent());
    }
    
    private static void doTestPARSER$Parse_withParameters(
            final String test, final String scheme, final Map<String, String> parameters){
        final ChallengeMessage message = ChallengeMessage.PARSER.parse(CharBuffer.wrap(test)).get();
        assertTrue(message instanceof ChallengeMessage.Parameters);
        assertEquals(scheme, message.scheme());
        assertEquals(parameters, ((ChallengeMessage.Parameters) message).parameters());
    }
    
    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(
                    ChallengeMessage.base64ChallengeMessage("Basic", "abcd=="),
                    ChallengeMessage.base64ChallengeMessage("Basic", "abcd=="))
            .addEqualityGroup(
                    ChallengeMessage.base64ChallengeMessage("Basic2", "abcd=="),
                    ChallengeMessage.base64ChallengeMessage("Basic2", "abcd==")) 
            .addEqualityGroup(
                    ChallengeMessage.base64ChallengeMessage("Basic2", "efgh=="),
                    ChallengeMessage.base64ChallengeMessage("Basic2", "efgh=="))                      
            .addEqualityGroup(
                    ChallengeMessage.parameterChallengeMessage("Auth", ImmutableMap.of("a", "b")),
                    ChallengeMessage.parameterChallengeMessage("Auth", ImmutableMap.of("a", "b"))) 
            .addEqualityGroup(
                    ChallengeMessage.parameterChallengeMessage("Auth", ImmutableMap.of("c", "d")),
                    ChallengeMessage.parameterChallengeMessage("Auth", ImmutableMap.of("c", "d")))                     
            .addEqualityGroup(
                    ChallengeMessage.basicAuthenticationChallenge("www.example.com"),
                    ChallengeMessage.parameterChallengeMessage("Basic", ImmutableMap.of("realm", "www.example.com")))                     
            .testEquals();
    }
    
    @Test 
    public void testNulls() {
        new NullPointerTester()
            .testAllPublicStaticMethods(ChallengeMessage.class);
        new NullPointerTester()
            .testAllPublicInstanceMethods(ChallengeMessage.PARSER);
        new NullPointerTester()
            .testAllPublicInstanceMethods(
                    ChallengeMessage.base64ChallengeMessage("Basic", "abcd==="));
        new NullPointerTester()
            .testAllPublicInstanceMethods(
                ChallengeMessage.parameterChallengeMessage(
                        "AuthScheme", ImmutableMap.of("a","b")));
    }
    
    /**
     * see: http://greenbytes.de/tech/tc/httpauth/
     */
    @Test
    public void testPARSER$parse() {
        
        doTestPARSER$Parse_withParameters("Basic realm=\"foo\"", "Basic", ImmutableMap.of("realm", "foo"));
        
        // Technically should fail per spec
        doTestPARSER$Parse_withParameters("Basic realm=foo", "Basic", ImmutableMap.of("realm", "foo"));
        
        //doTestPARSER$Parse_withParameters("Basic , realm=\"foo\"", "Basic", ImmutableMap.of("realm", "foo"));
        
        
        doTestPARSER$Parse_withInvalid("Basic, realm=\"foo\"");
        doTestPARSER$Parse_withInvalid("Basic");
        
        doTestPARSER$Parse_withInvalid("Basic realm=\"foo\", realm=\"bar\"");
        
        doTestPARSER$Parse_withParameters("Basic realm = \"foo\"", "Basic", ImmutableMap.of("realm", "foo"));
        doTestPARSER$Parse_withParameters("Basic realm = \"\\f\\o\\o\"", "Basic", ImmutableMap.of("realm", "foo"));
        doTestPARSER$Parse_withParameters("Basic realm=\"\\\"foo\\\"\"", "Basic", ImmutableMap.of("realm", "\"foo\""));
        //doTestPARSER$Parse_withParameters("Basic realm=\"foo\", bar=\"xyz\",, a=b,,,c=d", "Basic", ImmutableMap.of("realm", "foo", "bar", "xyz", "a", "b", "c", "d"));
        doTestPARSER$Parse_withParameters("Basic bar=\"xyz\", realm=\"foo\"", "Basic", ImmutableMap.of("bar", "xyz", "realm", "foo"));
        doTestPARSER$Parse_withParameters("Basic realm=\"foo-\u00E4\"", "Basic", ImmutableMap.of("realm", "foo-\u00E4"));
        doTestPARSER$Parse_withParameters("Basic realm=\"foo-\u00A4\"", "Basic", ImmutableMap.of("realm", "foo-\u00A4"));       
        doTestPARSER$Parse_withParameters("Basic realm=\"=?ISO-8859-1?Q?foo-=E4?=\"", "Basic", ImmutableMap.of("realm", "=?ISO-8859-1?Q?foo-=E4?="));
        
        // Skip multichallenge tests here.
        
        doTestPARSER$Parse_withParameters("Newauth realm=\"newauth\"", "Newauth", ImmutableMap.of("realm", "newauth"));
        doTestPARSER$Parse_withParameters("Basic foo=\"realm=nottherealm\", realm=\"basic\"", "Basic", ImmutableMap.of("foo", "realm=nottherealm", "realm", "basic"));
        doTestPARSER$Parse_withParameters("Basic nottherealm=\"nottherealm\", realm=\"basic\"", "Basic", ImmutableMap.of("nottherealm", "nottherealm", "realm", "basic"));
    }
}
