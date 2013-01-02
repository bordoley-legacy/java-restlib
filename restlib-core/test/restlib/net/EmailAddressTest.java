package restlib.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class EmailAddressTest {    
    private static final String[] VALID_EMAIL_ADDRESSES = 
        {"niceandsimple@example.com",
         "example@192.168.1.1",
         "niceandimple@example.example.com",
         "very.common@example.com",
         "a.little.lengthy.but.fine@dept.example.com",
         "disposable.style.email.with+symbol@example.com",
         "0@a",
         "postbox@com",
         "!#$%&'*+-/=?^_`{}|~@example.org",
         "user@[IPv6:2001:db8:1ff::a0b:dbd0]"};
    @Test
    public void testCreate() {
        try {
            EmailAddress.create("", "test.com");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
        
        try {
            EmailAddress.create("test", "");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}    
    }
    
    @Test
    public void testEquals() {
        final EqualsTester tester = new EqualsTester();
        for (final String test : VALID_EMAIL_ADDRESSES) {
            tester.addEqualityGroup(EmailAddress.parse(test), EmailAddress.parse(test));
        }
        tester.testEquals();
    }
    
    @Test
    public void testNulls() {
        final NullPointerTester tester = new NullPointerTester();
        tester.testAllPublicStaticMethods(EmailAddress.class);
    }
    
    @Test
    public void testParse_withInvalid() {
        final String[] invalidTests =
            {"Abc.example.com", // (an @ character must separate the local and domain parts)
             "Abc.@example.com", // (character dot(.) is last in local part)
             "Abc..123@example.com", // (character dot(.) is double)
             "A@b@c@example.com", // (only one @ is allowed outside quotation marks)
             "a\"b(c)d,e:f;g<h>i[j\\k]l@example.com", //  (none of the special characters in this local part is allowed outside quotation marks)
             "just\"not\"right@example.com", // (quoted strings must be dot separated, or the only element making up the local-part)
             "this is\"not\\allowed@example.com", // (spaces, quotes, and backslashes may only exist when within quoted strings and preceded by a slash)
             "this\\ still\\\"not\\\\allowed@example.com", // (even if escaped (preceded by a backslash), spaces, quotes, and backslashes must still be contained by quotes)
             "test@[192.168.1.1" // IP in brackets missing trailing bracket
            };
        
        for (final String invalidEmail : invalidTests) {
            try {
                EmailAddress.parse(invalidEmail);
                fail ("expected IllegalArgumentException");
            } catch(final IllegalArgumentException expected){}
        }

    }
    
    @Test
    public void testParse_withValid() { 
        for (final String test : VALID_EMAIL_ADDRESSES) {
            assertEquals(test, EmailAddress.parse(test).toString());
        }
    }
    
    @Test
    public void testParse_withValidUnsupported() {
        final String[] validUnsupportedTests =
            {"\"much.more unusual\"@example.com",
             "\"very.unusual.@.unusual.com\"@example.com",
             "\"very.(),:;<>[]\\\".VERY.\\\"very@\\\\ \\\"very\\\".unusual\\\"@strange.example.com",
             "\"()<>[]:,;@\\\\\\\"!#$%&'*+-/=?^_`{}| ~  ? ^_`{}|~.a\"@example.org",
             "\"\"@example.org",
            };
        
        for (final String invalidEmail : validUnsupportedTests) {
            try {
                EmailAddress.parse(invalidEmail);
                fail ("expected IllegalArgumentException");
            } catch(final IllegalArgumentException expected){}
        } 
    }
}
