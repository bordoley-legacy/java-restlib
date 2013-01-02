package restlib.impl;

import org.junit.Test;

import com.google.common.testing.NullPointerTester;

public final class GuavaCollectionHelpersTest {    
    @Test
    public void testNulls() {
        final NullPointerTester tester = new NullPointerTester();
        tester.testAllPublicStaticMethods(GuavaCollectionHelpers.class);
    }    
}
