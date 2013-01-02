package restlib.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;
import com.google.common.testing.NullPointerTester;

public final class TagBuilderTest {
    private static void doTestBadDate(final int year, final int month, final int day) {
        try {
            Uri.tagBuilder().setDate(year, month, day);
            fail ("expected IllegalArgumentException.");
        } catch (final IllegalArgumentException expected){}
    }
    
    @Test
    public void testBuild() {
          assertEquals(
                  Uri.parse("tag:www.example.com,2001:/a/b/c"),
                  Uri.tagBuilder()
                      .setAuthorityName("www.example.com")
                      .setDate(2001)
                      .setSpecificPath("/a/b/c")
                      .build());
          assertEquals(
                  Uri.parse("tag:www.example.com,2001-12:/a/b/c"),
                  Uri.tagBuilder()
                      .setAuthorityName("www.example.com")
                      .setDate(2001, 12)
                      .setSpecificPath("/a/b/c")
                      .build());
          assertEquals(
                  Uri.parse("tag:www.example.com,2001-12-05:/a/b/c"),
                  Uri.tagBuilder()
                      .setAuthorityName("www.example.com")
                      .setDate(2001, 12, 5)
                      .setSpecificPath("/a/b/c")
                      .build());
          assertEquals(
                  Uri.parse("tag:www.example.com,2001-12-05:/a/b/c?query#fragment"),
                  Uri.tagBuilder()
                      .setAuthorityName("www.example.com")
                      .setDate(2001, 12, 5)
                      .setSpecificPath("/a/b/c")
                      .setSpecificQuery("query")
                      .setFragment("fragment")
                      .build());
          assertEquals(
                  Uri.parse("tag:www.example.com,2001-12-05:?query#fragment"),
                  Uri.tagBuilder()
                      .setAuthorityName("www.example.com")
                      .setDate(2001, 12, 5)
                      .setSpecificQuery("query")
                      .setFragment("fragment")
                      .build());
          assertEquals(
                  Uri.parse("tag:www.example.com,2001-12-05:a?query#fragment"),
                  Uri.tagBuilder()
                      .setAuthorityName("www.example.com")
                      .setDate(2001, 12, 5)
                      .setSpecificPath("a")
                      .setSpecificQuery("query")
                      .setFragment("fragment")
                      .build());
          assertEquals(
                  Uri.parse("tag:192.168.1.1,2001-12-05:a?query#fragment"),
                  Uri.tagBuilder()
                      .setAuthorityName(InetAddresses.forString("192.168.1.1"))
                      .setDate(2001, 12, 5)
                      .setSpecificPath("a")
                      .setSpecificQuery("query")
                      .setFragment("fragment")
                      .build());
          
          // Test with international domain names
          assertEquals(
                  Uri.parse("tag:xn--rsum-bpad.example.org,2001-12-05:a?query#fragment"),
                  Uri.tagBuilder()
                      .setAuthorityName(InternetDomainName.from("r\u00E9sum\u00E9.example.org"))
                      .setDate(2001, 12, 5)
                      .setSpecificPath("a")
                      .setSpecificQuery("query")
                      .setFragment("fragment")
                      .build());
          
          // Test for no authority name set
          try {
              Uri.tagBuilder().build();
              fail ("expected IllegalStateException");
          } catch (final IllegalStateException expected){}
    }
    @Test
    public void testNulls() {
        new NullPointerTester()
            .testAllPublicInstanceMethods(Uri.tagBuilder());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetAuthorityName_withInvalidName() {
        Uri.tagBuilder().setAuthorityName("r\u00E9sum\u00E9.example.org");
    }
    
    @Test
    public void testSetDate_withInvalidDates() {
        doTestBadDate(10000,1,1); // Invalid year
        doTestBadDate(-12,1,1); // Invalid year
        doTestBadDate(2002, 30, 10); // Invalid month
        doTestBadDate(2002, -5, 10); // Invalid month
        doTestBadDate(2002, 5, 100); // Invalid day
        doTestBadDate(2002, 5, -10); // Invalid day
        doTestBadDate(2002, -1, 10); // Invalid day due to unspecified month
    }
    
    @Test
    public void testSetSpecificPath_withInvalidPath() {
        try {
            Uri.tagBuilder().setSpecificPath("/\uD800\uDF00\uD800\uDF01\uD800\uDF02");
        } catch (final IllegalArgumentException expected){}
    }
}
