package restlib.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.junit.Test;

import com.google.common.base.CharMatcher;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.NullPointerTester;


public final class CodePointMatcherTest {
    private void assertFalseForAllCodePoints(final CodePointMatcher matcher) {
        for (int i = Character.MIN_CODE_POINT; i <= Character.MAX_CODE_POINT; i++) {
            assertFalse(matcher.apply(i));
        }
    }
    
    private void assertFalseForAllCodePointStrings(final Predicate<CharSequence> matcher) {
        for (int i = Character.MIN_CODE_POINT; i <= Character.MAX_CODE_POINT; i++) {
            assertFalse(matcher.apply(Character.toString((char) i)));
        }
    }
    
    private void assertTrueForAllCodePoints(final CodePointMatcher matcher) {
        for (int i = Character.MIN_CODE_POINT; i <= Character.MAX_CODE_POINT; i++) {
            assertTrue(matcher.apply(i));
        }
    }
    
    private void assertTrueForAllCodePointStrings(final Predicate<CharSequence> matcher) {
        for (int i = Character.MIN_CODE_POINT; i <= Character.MAX_CODE_POINT; i++) {
            assertTrue(matcher.apply(Character.toString((char) i)));
        }
    }
    
    private void doTestNull(final CodePointMatcher matcher) {
        final NullPointerTester tester = new NullPointerTester();
        tester.testAllPublicInstanceMethods(matcher);
    }
    
    @Test
    public void testANY() {
        assertTrueForAllCodePoints(CodePointMatcher.ANY);
        assertTrueForAllCodePointStrings(CodePointMatcher.ANY.matchesAllOf());
        assertFalseForAllCodePointStrings(CodePointMatcher.ANY.matchesNoneOf());
        
        // Not required by the API specification, but works nonetheless.
        assertEquals(CodePointMatcher.ANY.and(CodePointMatcher.NONE), CodePointMatcher.NONE);
        assertEquals(CodePointMatcher.ANY.negate(), CodePointMatcher.NONE);
        assertEquals(CodePointMatcher.ANY.or(CodePointMatcher.NONE), CodePointMatcher.ANY);   
    }
    
    @Test
    public void testAnyOf() {
        // Not required by the API specification, but works nonetheless.
        assertEquals(CodePointMatcher.anyOf(""), CodePointMatcher.NONE);
        
        final String matches = "abcdefghABCDEFGH";
        final Set<Integer> matchSet = ImmutableSet.copyOf(CharSequences.codePoints(matches));
        
        final CodePointMatcher matcher = CodePointMatcher.anyOf(matches);
        for (int i = Character.MIN_CODE_POINT; i <= Character.MAX_CODE_POINT; i++) {
            assertTrue(matchSet.contains(i) ? matcher.apply(i) : !matcher.apply(i));
        }
        
        for (int i = Character.MIN_CODE_POINT; i <= Character.MAX_CODE_POINT; i++) {
            assertTrue("Failed on: " + i, matchSet.contains(i) ? 
                    matcher.matchesAllOf(CharSequences.fromCodepoint(i)) : 
                        !matcher.matchesAllOf(CharSequences.fromCodepoint(i)));
        }
        
        for (int i = Character.MIN_CODE_POINT; i <= Character.MAX_CODE_POINT; i++) {
            assertTrue(matchSet.contains(i) ? 
                    !matcher.matchesNoneOf(CharSequences.fromCodepoint(i)) : 
                        matcher.matchesNoneOf(CharSequences.fromCodepoint(i)));
        }
        
        final CodePointMatcher negated = matcher.negate();
        for (int i = Character.MIN_CODE_POINT; i <= Character.MAX_CODE_POINT; i++) {
            assertTrue(matchSet.contains(i) ? !negated.apply(i) : negated.apply(i));
        }
        
        final CodePointMatcher matcherAndNONE = matcher.and(CodePointMatcher.NONE);
        assertFalseForAllCodePoints(matcherAndNONE);
        
        final CodePointMatcher matcherOrANY = matcher.or(CodePointMatcher.ANY);
        assertTrueForAllCodePoints(matcherOrANY);
    }
    
    @Test
    public void testFromCharacterPredicate() {
        final String matches = "abcdefghABCDEFGH";
        final Set<Integer> matchSet = ImmutableSet.copyOf(CharSequences.codePoints(matches));
        
        final CodePointMatcher matcher = CodePointMatcher.fromCharacterPredicate(CharMatcher.anyOf(matches));
        for (int i = Character.MIN_CODE_POINT; i <= Character.MAX_CODE_POINT; i++) {
            assertTrue(matchSet.contains(i) ? matcher.apply(i) : !matcher.apply(i));
        }
        
        for (int i = Character.MIN_CODE_POINT; i <= Character.MAX_CODE_POINT; i++) {
            assertTrue("Failed on: " + i, matchSet.contains(i) ? 
                    matcher.matchesAllOf(CharSequences.fromCodepoint(i)) : 
                        !matcher.matchesAllOf(CharSequences.fromCodepoint(i)));
        }
        
        for (int i = Character.MIN_CODE_POINT; i <= Character.MAX_CODE_POINT; i++) {
            assertTrue(matchSet.contains(i) ? 
                    !matcher.matchesNoneOf(CharSequences.fromCodepoint(i)) : 
                        matcher.matchesNoneOf(CharSequences.fromCodepoint(i)));
        }
        
        final CodePointMatcher negated = matcher.negate();
        for (int i = Character.MIN_CODE_POINT; i <= Character.MAX_CODE_POINT; i++) {
            assertTrue(matchSet.contains(i) ? !negated.apply(i) : negated.apply(i));
        }
        
        final CodePointMatcher matcherAndNONE = matcher.and(CodePointMatcher.NONE);
        assertFalseForAllCodePoints(matcherAndNONE);
        
        final CodePointMatcher matcherOrANY = matcher.or(CodePointMatcher.ANY);
        assertTrueForAllCodePoints(matcherOrANY);
    }
    
    @Test
    public void testInRange_withCharSequenceCodePoints() {
        try {
            CodePointMatcher.inRange("ab", "c");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {}
        
        try {
            CodePointMatcher.inRange("a", "bc");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {}
        
        try {
            CodePointMatcher.inRange("c", "a");
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {}  
    }
    
    @Test
    public void testInRange_withIntegerCodePoints() {
        try {
            CodePointMatcher.inRange(Integer.MAX_VALUE, 'c');
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {}
        
        try {
            CodePointMatcher.inRange('c', Integer.MAX_VALUE);
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {}
        
        try {
            CodePointMatcher.inRange('c', 'a');
            fail("expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {}
        
        final int minCodePoint = Character.MIN_SUPPLEMENTARY_CODE_POINT;
        final int maxCodePoint = minCodePoint + 100;
        final CodePointMatcher matcher = CodePointMatcher.inRange(minCodePoint, maxCodePoint);
        
        for (int i = 0; i < Character.MAX_CODE_POINT; i++) {
            if ((i >= minCodePoint) && (i <= maxCodePoint)) {
                assertTrue(matcher.apply(i));
            } else {
                assertFalse(matcher.apply(i));
            }
        }
    }
    
    @Test
    public void testNONE() {
        assertFalseForAllCodePoints(CodePointMatcher.NONE);
        assertFalseForAllCodePointStrings(CodePointMatcher.NONE.matchesAllOf());
        assertTrueForAllCodePointStrings(CodePointMatcher.NONE.matchesNoneOf());

        // These aren't guaranteed by the API specification, but work nonetheless.
        assertEquals(CodePointMatcher.NONE.and(CodePointMatcher.ANY), CodePointMatcher.NONE);
        assertEquals(CodePointMatcher.NONE.negate(), CodePointMatcher.ANY);
        assertEquals(CodePointMatcher.NONE.or(CodePointMatcher.ANY), CodePointMatcher.ANY);      
    }
    
    @Test
    public void testNull() {   
        // Tests the default implementation
        doTestNull(CodePointMatcher.anyOf("abc"));
        doTestNull(CodePointMatcher.anyOf("abc").and(CodePointMatcher.NONE));
        doTestNull(CodePointMatcher.anyOf("abc").negate());
        doTestNull(CodePointMatcher.anyOf("abc").or(CodePointMatcher.NONE));
        
        doTestNull(CodePointMatcher.fromCharacterPredicate(CharMatcher.ANY));  
        doTestNull(CodePointMatcher.inRange(0, 10));
        doTestNull(CodePointMatcher.inRange("a", "c"));
        doTestNull(CodePointMatcher.noneOf("abc"));
        
        // ANY/NONE have custom implementation of and, negate, or 
        doTestNull(CodePointMatcher.ANY);
        doTestNull(CodePointMatcher.ANY.and(CodePointMatcher.NONE));
        doTestNull(CodePointMatcher.ANY.negate());
        doTestNull(CodePointMatcher.ANY.or(CodePointMatcher.NONE));    
        
        doTestNull(CodePointMatcher.NONE); 
        doTestNull(CodePointMatcher.NONE.and(CodePointMatcher.ANY));
        doTestNull(CodePointMatcher.NONE.negate());
        doTestNull(CodePointMatcher.NONE.or(CodePointMatcher.ANY));      
    }
   
    @Test
    public void testStaticNullPointers() {
        new NullPointerTester()
            .setDefault(Integer.class, 0)
            .setDefault(CharSequence.class, "a")
            .testAllPublicStaticMethods(CodePointMatcher.class);
    }
}
