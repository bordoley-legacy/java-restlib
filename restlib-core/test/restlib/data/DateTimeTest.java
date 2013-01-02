package restlib.data;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class DateTimeTest {
    private static class MockDateTime extends DateTime {
        public MockDateTime(long date) {
            super(date);
        }

        @Override
        public String toString() {
            return String.valueOf(this.time());
        }    
    }
    
    @Test
    public void testCompareTo() {
        assertTrue(HttpDate.create(123).compareTo(HttpDate.create(0)) > 0);
        assertTrue(HttpDate.create(0).compareTo(HttpDate.create(123)) < 0);
        assertTrue(HttpDate.create(123).compareTo(HttpDate.create(123)) == 0);
    }
    
    @Test
    public void testEquals() {
        new EqualsTester() 
            .addEqualityGroup(new MockDateTime(123), HttpDate.create(123))
            .addEqualityGroup(new MockDateTime(321), new MockDateTime(321))
            .testEquals();
    }
    

    @Test
    public void testNulls() {
        new NullPointerTester()
            .testAllPublicInstanceMethods(new MockDateTime(123));
    }
}
