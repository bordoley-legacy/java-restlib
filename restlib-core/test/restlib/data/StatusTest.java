package restlib.data;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import restlib.Response;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class StatusTest {
	@Test
	public void testEquals() {
		new EqualsTester()
			.addEqualityGroup(Status.CLIENT_ERROR_BAD_REQUEST)
			.addEqualityGroup(Status.INFORMATIONAL_CONTINUE)
			.addEqualityGroup(Status.SERVER_ERROR_BAD_GATEWAY)
			.addEqualityGroup(Status.SUCCESS_ACCEPTED)
			.addEqualityGroup(Status.forCode(550), Status.forCode(550))
			.testEquals();
	}
	
	@Test
	public void testForCode_withInvalid() {
		try {
			Status.forCode(1000);
			fail("Expected IllegalArgumentException");
		} catch (final IllegalArgumentException expected){}
		
		try {
			Status.forCode(10);
			fail("Expected IllegalArgumentException");
		} catch (final IllegalArgumentException expected){}
		
		try {
			Status.forCode(-200);
			fail("Expected IllegalArgumentException");
		} catch (final IllegalArgumentException expected){}
	}
	
	@Test
	public void testNulls() {
		new NullPointerTester()
        	.testAllPublicInstanceMethods(Status.CLIENT_ERROR_BAD_REQUEST);
		new NullPointerTester()
			.testAllPublicStaticMethods(Status.class);
	}
	
	@Test
    public void testStatusClass() {
        for(final Status status : Status.REGISTERED_STATUSES.values()) {
            if (status.statusClass().equals(Status.Class.INFORMATIONAL)) {
                assertTrue((status.code() >= 100) && (status.code() < 200));
            } else if (status.statusClass().equals(Status.Class.SUCCESS)) {
                assertTrue((status.code() >= 200) && (status.code() < 300));
            } else if (status.statusClass().equals(Status.Class.REDIRECTION)) {
                assertTrue((status.code() >= 300) && (status.code() < 400));
            } else if (status.statusClass().equals(Status.Class.CLIENT_ERROR)) {
                assertTrue((status.code() >= 400) && (status.code() < 500));
            } else if (status.statusClass().equals(Status.Class.SERVER_ERROR)) {
                assertTrue((status.code() >= 500) && (status.code() < 600));
            } else {
                throw new RuntimeException("");
            }
        }
    }
	
    @Test
    public void testToResponse() {
    	for(final Status status : Status.REGISTERED_STATUSES.values()) {
            final Response response = status.toResponse();
            assertTrue(response.status().equals(status));
            assertTrue(response.entity().get().equals(status.message()));
        }
    }
	
}
