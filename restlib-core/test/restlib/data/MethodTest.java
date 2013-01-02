package restlib.data;

import org.junit.Test;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class MethodTest {
    @Test
    public void testEquals() {    
        new EqualsTester()
            .addEqualityGroup(Method.GET, Method.GET)
            .addEqualityGroup(Method.POST, Method.POST)
            .testEquals();
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
                .testAllPublicInstanceMethods(Method.GET);
        new NullPointerTester()    
                .testAllPublicStaticMethods(Method.class);
    }
    
}
