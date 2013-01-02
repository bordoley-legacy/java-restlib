package restlib.impl;

import org.junit.Test;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class CaseInsensitiveStringTest {
    @Test 
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(CaseInsensitiveString.wrap(""), CaseInsensitiveString.wrap(""))
            .addEqualityGroup(
                    CaseInsensitiveString.wrap("abc"), 
                    CaseInsensitiveString.wrap("Abc"),
                    CaseInsensitiveString.wrap("AbC"))
            .addEqualityGroup(
                    CaseInsensitiveString.wrap("a.bc"), 
                    CaseInsensitiveString.wrap("A.bc"),
                    CaseInsensitiveString.wrap("A.bC"))
            .testEquals();
    }
    
    @Test
    public void testNulls(){
        new NullPointerTester()
            .testAllPublicStaticMethods(CaseInsensitiveString.class);
        new NullPointerTester()
            .testAllPublicInstanceMethods(CaseInsensitiveString.wrap("abc"));    
    }
}
