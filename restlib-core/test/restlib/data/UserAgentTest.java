package restlib.data;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class UserAgentTest {
    private static void doTestParse_withInvalid(final String test) {
        try {
            UserAgent.parse(test);
            fail("Expected IllegalArgumentException.");
        } catch (final IllegalArgumentException expected){};
    }
    
    @Test
    public void testEquals() {
        final Product product = Product.create("Mozilla", "5.0");
        final Comment comment = Comment.parse("(test)");
        new EqualsTester()  
                .addEqualityGroup(
                        UserAgent.of(product),
                        UserAgent.of(product))
                .addEqualityGroup(
                        UserAgent.of(product, comment),
                        UserAgent.of(product, comment))
                .addEqualityGroup(
                        UserAgent.of(product, comment, product),                            
                        UserAgent.of(product, comment, product))
                .addEqualityGroup(
                        UserAgent.of(product, comment, product, comment),                            
                        UserAgent.of(product, comment, product, comment))     
                .addEqualityGroup(
                        UserAgent.of(product, comment, product, comment, product),                            
                        UserAgent.of(product, comment, product, comment, product))                   
                .addEqualityGroup(
                        UserAgent.of(product, comment, product, comment, product, comment),                            
                        UserAgent.of(product, comment, product, comment, product, comment))    
                .addEqualityGroup(
                        UserAgent.of(product, comment, product, comment, product, comment, product),                            
                        UserAgent.of(product, comment, product, comment, product, comment, product))
                .addEqualityGroup(
                        UserAgent.of(product, comment, product, comment, product, comment, product, comment),                            
                        UserAgent.of(product, comment, product, comment, product, comment, product, comment))      
                .addEqualityGroup(
                        UserAgent.of(product, comment, product, comment, product, comment, product, comment, product),                            
                        UserAgent.of(product, comment, product, comment, product, comment, product, comment, product))        
                .addEqualityGroup(
                        UserAgent.of(product, comment, product, comment, product, comment, product, comment, product, comment),                            
                        UserAgent.of(product, comment, product, comment, product, comment, product, comment, product, comment))   
                .addEqualityGroup(
                        UserAgent.of(product, comment, product, comment, product, comment, product, comment, product, comment, product),                            
                        UserAgent.of(product, comment, product, comment, product, comment, product, comment, product, comment, product))    
                .addEqualityGroup(
                        UserAgent.of(product, comment, product, comment, product, comment, product, comment, product, comment, product, comment),                            
                        UserAgent.of(product, comment, product, comment, product, comment, product, comment, product, comment, product, comment))  
                .addEqualityGroup(
                        UserAgent.of(product, comment, product, comment, product, comment, product, comment, product, comment, product, comment, product, comment),                            
                        UserAgent.of(product, comment, product, comment, product, comment, product, comment, product, comment, product, comment, product, comment))           
                .addEqualityGroup(
                        UserAgent.parse("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2)"),
                        UserAgent.parse("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2)"))
                .addEqualityGroup(
                        UserAgent.parse("AppleWebKit/535.7 (KHTML, like Gecko)"),
                        UserAgent.parse("AppleWebKit/535.7 (KHTML, like Gecko)"))   
                .addEqualityGroup(
                        UserAgent.parse("Mozilla/5.0 (Linux; U; Android 2.1-update1; en-us; SGH-T959 Build/ECLAIR) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17"),
                        UserAgent.parse("Mozilla/5.0 (Linux; U; Android 2.1-update1; en-us; SGH-T959 Build/ECLAIR) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17"))
                .testEquals();        
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
                .testAllPublicInstanceMethods(
                        UserAgent.parse("Restlib/1.0"));
        new NullPointerTester()
                .setDefault(Product.class, Product.create("Restlib", "1.0"))
                .testAllPublicStaticMethods(UserAgent.class);
    }
    
    @Test
    public void testParse() {
        doTestParse_withInvalid("");
        doTestParse_withInvalid("   ");
        doTestParse_withInvalid("A=B/3.9");
        doTestParse_withInvalid(" AppleWebKit/535.7 ");
        doTestParse_withInvalid("AppleWebKit/ ");
        doTestParse_withInvalid("AppleWebKit/");
    }
}
