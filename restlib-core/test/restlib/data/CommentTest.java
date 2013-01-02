package restlib.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class CommentTest {   
    private static void doTestParse_withInvalid(final String comment) {
        try {
            Comment.parse(comment);
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected){}
    }
    
    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(
                    Comment.parse("()"), 
                    Comment.parse("()"))
            .addEqualityGroup(
                    Comment.parse("(test)"),
                    Comment.parse("(test)"))
            .addEqualityGroup(
                    Comment.parse("(test, test)"),
                    Comment.parse("(test, test)"))
            .addEqualityGroup(
                    Comment.parse("(test, test(test))"),
                    Comment.parse("(test, test(test))"))      
            .testEquals();
    }

    @Test
    public void testNulls() {
        new NullPointerTester()
                .testAllPublicStaticMethods(Comment.class);
        new NullPointerTester()
                .testAllPublicInstanceMethods(Comment.parse("()"));
    }
    
    @Test
    public void testParse() {
        final String test = "(test\\(\\)\\\\ test(test test(test test)test)(test))";
        assertEquals(test, Comment.parse(test).toString());
        
        doTestParse_withInvalid("test");
        doTestParse_withInvalid("(test)   test");
        doTestParse_withInvalid("test (test)");
        doTestParse_withInvalid("((((test)");
        doTestParse_withInvalid("(test)))))");
    }
}
