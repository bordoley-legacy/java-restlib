package restlib.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.nio.CharBuffer;

import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class ProductTest {
    private static void doTestCreate_withInvalid(final String name, final String version) {
        try {
            Product.create(name, version);
        } catch (final IllegalArgumentException expected){}
    }
    
    private static void doTestPARSER$parse(final String test, final String name, final String version) {
        final Product product = Product.PARSER.parse(CharBuffer.wrap(test)).get();
        assertEquals(name, product.name());
        assertEquals(version, product.version());
    }
    
    private static void doTestPARSER$parse_withInvalid(final String test) {
        final Optional<Product> product = Product.PARSER.parse(CharBuffer.wrap(test));
        assertFalse(product.isPresent());
    }
    
    @Test
    public void testCreate() {
        doTestCreate_withInvalid("", "");
    }
    
    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(Product.create("CERN-LineMode", "2.15"), Product.create("CERN-LineMode", "2.15"))
            .addEqualityGroup(Product.create("CERN-LineMode", ""), Product.create("CERN-LineMode", ""))
            .addEqualityGroup(Product.create("libwww", ""), Product.create("libwww", ""))
            .addEqualityGroup(Product.create("libwww", "2.17b3"), Product.create("libwww", "2.17b3"))
            .testEquals();
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
                .testAllPublicInstanceMethods(Product.create("CERN-LineMode", "2.15"));
        new NullPointerTester()    
                .testAllPublicStaticMethods(Product.class);
    }
    
    @Test
    public void testPARSER$parse() {
        doTestPARSER$parse("CERN-LineMode/2.15", "CERN-LineMode", "2.15");
        doTestPARSER$parse("TOKEN", "TOKEN", "");
        
        doTestPARSER$parse_withInvalid("TOKEN/");
        doTestPARSER$parse_withInvalid("@TOKEN/");
        
    }
}
