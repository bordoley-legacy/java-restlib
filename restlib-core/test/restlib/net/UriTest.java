/*
 * Copyright (C) 2012 David Bordoley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package restlib.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.NullPointerTester;


public final class UriTest  {
    private static void doTestCanonicalize(final String expected, final String test) {
        assertEquals(expected, Uri.parse(test).canonicalize().toString());
    }
    
    private static void doTestNormalize(final String expected, final String test) {
        assertEquals(expected, Uri.parse(test).normalize().toString());
    }
    
    private static void doTestRelativeReference(final String expected, final String base, final String relative) {
        final Uri baseUri = Uri.parse(base);
        final Uri relativeUri = Uri.parse(relative);
        final Uri expectedUri = Uri.parse(expected);
        assertEquals(expectedUri, Uri.relativeReference(baseUri, relativeUri));     
    }
    
    @Test 
    public void testCanonicalize() {       
        doTestCanonicalize("/a/b/c/", "/a/b/c/");      
        doTestCanonicalize("example.com/a/b/c/", "example.com/a/b/c/");
        doTestCanonicalize("http://example.com/", "http://example.com");
        doTestCanonicalize("http://example.com/", "http://example.com/");
        doTestCanonicalize("http://example.com/", "http://example.com/////");
        doTestCanonicalize("http://example.com/a", "http://example.com/a/");
        doTestCanonicalize("http://example.com/a/b", "http://example.com/a//b/");
        doTestCanonicalize("http:a/b/c", "http:a/b/c/");
        doTestCanonicalize("http:/", "http:");
        doTestCanonicalize("http:a/b", "http:a//b");
        doTestCanonicalize("http://example.com/a/b/c", "http://example.com/a/b/c");
        doTestCanonicalize("tag:a", "tag:a/");
        doTestCanonicalize("tag:a/b", "tag:a/b");
        doTestCanonicalize("tag:a", "tag:a");
        doTestCanonicalize("http://www.example.com/", "http://www.example.com");
        doTestCanonicalize("http://www.example.com/test", "http://www.example.com/test/");
        
        // IRIs without a scheme aren't canonicalized.
        assertFalse(Uri.parse("www.example.com").canonicalize().toString().equals("www.example.org/"));
    }
    
    @Test
    public void testNormalize() {
        doTestNormalize("http://www.example.org", "HTTP://WWw.ExAmPlE.org");
        doTestNormalize("http://192.168.1.1", "http://192.168.1.1");
        doTestNormalize("http://[3ffe:2a00:100:7031::1]", "http://[3ffe:2a00:100:7031::1]");
        
        // Don't normalize non absolute IRIs
        assertFalse(Uri.parse("www.ExAmPlE.org").normalize().toString().equals("www.example.org"));
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
            .setDefault(Uri.class, Uri.parse("http://www.example.com"))
            .setDefault(IRI.class, IRI.parse("http://www.example.com"))
            .testAllPublicStaticMethods(Uri.class);
    }
    
    @Test
    public void testParse_withInvalid() {
        final List<String> tests =
                ImmutableList.of(
                        "http://www.example.org/red%09ros\u00E9#red",
                        "http://r\u00E9sum\u00E9.example.org");
        for (final String test : tests) {
            try {
                Uri.parse(test);
                fail("expected IllegalArgumentException");
            } catch (final IllegalArgumentException expected){}
        }
    }
    
    @Test
    public void testRelativeReference() {
        final String rfcTestsBase = "http://a/b/c/d;p?q";
        doTestRelativeReference("g:h", rfcTestsBase, "g:h"); 
        doTestRelativeReference("http://a/b/c/g", rfcTestsBase, "g");
        doTestRelativeReference("http://a/b/c/g", rfcTestsBase, "./g");
        doTestRelativeReference("http://a/b/c/g/", rfcTestsBase, "g/");
        doTestRelativeReference("http://a/g", rfcTestsBase, "/g");
        doTestRelativeReference("http://g", rfcTestsBase, "//g");
        doTestRelativeReference("http://a/b/c/d;p?y", rfcTestsBase, "?y");
        doTestRelativeReference("http://a/b/c/g?y", rfcTestsBase, "g?y");
        doTestRelativeReference("http://a/b/c/d;p?q#s", rfcTestsBase, "#s");
        doTestRelativeReference("http://a/b/c/g#s", rfcTestsBase, "g#s");
        doTestRelativeReference("http://a/b/c/g?y#s", rfcTestsBase, "g?y#s");
        doTestRelativeReference("http://a/b/c/;x", rfcTestsBase, ";x");
        doTestRelativeReference("http://a/b/c/g;x", rfcTestsBase, "g;x");
        doTestRelativeReference("http://a/b/c/g;x?y#s", rfcTestsBase, "g;x?y#s");
        doTestRelativeReference("http://a/b/c/d;p?q", rfcTestsBase, "");
        doTestRelativeReference("http://a/b/c/", rfcTestsBase, ".");
        doTestRelativeReference("http://a/b/c/", rfcTestsBase, "./");
        doTestRelativeReference("http://a/b/", rfcTestsBase, "..");
        doTestRelativeReference("http://a/b/", rfcTestsBase, "../");
        
        // Abnormal Examples
        doTestRelativeReference("http://a/b/g", rfcTestsBase, "../g");
        doTestRelativeReference("http://a/", rfcTestsBase, "../..");
        doTestRelativeReference("http://a/", rfcTestsBase, "../../");
        doTestRelativeReference("http://a/g", rfcTestsBase, "../../g");
        doTestRelativeReference("http://a/g", rfcTestsBase, "../../../g");
        doTestRelativeReference("http://a/g", rfcTestsBase, "../../../../g");
        doTestRelativeReference("http://a/g", rfcTestsBase, "/./g");
        doTestRelativeReference("http://a/g", rfcTestsBase, "/../g");
        doTestRelativeReference("http://a/b/c/g.", rfcTestsBase, "g.");
        doTestRelativeReference("http://a/b/c/.g", rfcTestsBase, ".g");
        doTestRelativeReference("http://a/b/c/g..", rfcTestsBase, "g..");
        doTestRelativeReference("http://a/b/c/..g", rfcTestsBase, "..g"); 
        doTestRelativeReference("http://a/b/g", rfcTestsBase, "./../g");
        doTestRelativeReference("http://a/b/c/g/", rfcTestsBase, "./g/.");
        doTestRelativeReference("http://a/b/c/g/h", rfcTestsBase, "g/./h");
        doTestRelativeReference("http://a/b/c/h", rfcTestsBase, "g/../h");
        doTestRelativeReference("http://a/b/c/g;x=1/y", rfcTestsBase, "g;x=1/./y");
        doTestRelativeReference("http://a/b/c/y", rfcTestsBase, "g;x=1/../y");
        doTestRelativeReference("http://a/b/c/g?y/./x", rfcTestsBase, "g?y/./x");
        doTestRelativeReference("http://a/b/c/g?y/../x", rfcTestsBase, "g?y/../x");
        doTestRelativeReference("http://a/b/c/g#s/./x", rfcTestsBase, "g#s/./x");
        doTestRelativeReference("http://a/b/c/g#s/../x", rfcTestsBase, "g#s/../x");
        doTestRelativeReference("http:g", rfcTestsBase, "http:g");
        
        doTestRelativeReference("http://www.example.com/a/b/c", "http://www.example.com", "a/b/c");
    }
}
