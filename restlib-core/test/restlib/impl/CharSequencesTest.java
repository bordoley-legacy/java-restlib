package restlib.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Chars;
import com.google.common.testing.NullPointerTester;

public final class CharSequencesTest {
    @Test
    public void testCharacters() {
        final String[] tests = 
            {"",  // Empty String
             "a", // Single character   
             "abcdefghijklmnopqrstuvhwxyzABCDEFGHIJKLMNOPQRSTUVXYZ0123456789 \n\t\b\r\f\'\"\\", // ASCII characters
             "\uDB7F\uDFFD" // Double byte characters
             };
        for (final String test : tests) {
            assertTrue(test,
                    Iterables.elementsEqual(
                            CharSequences.characters(test), 
                            Chars.asList(test.toCharArray())));
        }
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testCharacters$iterator$next_withEmptyString() {
        CharSequences.characters("").iterator().next();
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testCodePoints$iterator$next_withEmptyString() {
        CharSequences.codePoints("").iterator().next();
    }
    
    @Test
    public void testCodePointsAsString() {
        final String test = 
                "\uDB7F\uDFFDabcd\uDB7F\uDFFD"; 
        final List<String> expected =
                ImmutableList.of("\uDB7F\uDFFD","a", "b", "c","d", "\uDB7F\uDFFD");
        
        assertTrue(test,
                Iterables.elementsEqual(
                        CharSequences.codePointsAsStrings(test), 
                        expected));       
    }
    
    @Test
    public void testIsEmpty() {
    	assertTrue(CharSequences.isEmpty(""));
    	assertFalse(CharSequences.isEmpty("not empty"));
    }
    
    @Test
    public void testNulls() {
        final NullPointerTester tester = new NullPointerTester();
        tester.testAllPublicStaticMethods(CharSequences.class);
    }
    
    @Test
    public void testToCodePoint() {
        final Map<String, Integer> testCases =
                ImmutableMap.<String, Integer> builder()
                    .put("a", (int) 'a')
                    .build();
        for (final Map.Entry<String, Integer> testCase : testCases.entrySet()) {
            assertTrue(
                    Objects.equal(
                            testCase.getValue(), 
                            CharSequences.toCodePoint(testCase.getKey())));
        }
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testToCodePoint_withEmpty() {
        CharSequences.toCodePoint("");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testToCodePoint_withMultipleCodePoints() {
        CharSequences.toCodePoint("\uDB7F\uDFFDa");
    }
}
