package restlib.data;

import org.junit.Test;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class RangeUnitTest {
    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(RangeUnit.ACCEPT_NONE, RangeUnit.create("none"))
            .addEqualityGroup(RangeUnit.BYTES)
            .addEqualityGroup(RangeUnit.create("other"), RangeUnit.create("OTHER"))
            .testEquals();
    }
    @Test
    public void testNulls() {
        new NullPointerTester()
            .testAllPublicInstanceMethods(RangeUnit.ACCEPT_NONE);
        new NullPointerTester()
            .testAllPublicStaticMethods(RangeUnit.class);
    }
}
