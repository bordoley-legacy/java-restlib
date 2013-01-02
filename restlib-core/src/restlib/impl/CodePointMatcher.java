package restlib.impl;

import java.util.Arrays;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;

/**
 * A {@code Predicate} for determining the true or false value of any Java {@code Integer} code point value.
 *
 */
@Immutable
public abstract class CodePointMatcher implements Predicate<Integer> {
    /**
     * Matches any code point.
     */
    public static final CodePointMatcher ANY = new CodePointMatcher() {
        @Override
        public CodePointMatcher and(final CodePointMatcher other) {
            Preconditions.checkNotNull(other);
            return other;
        }
        
        @Override
        public boolean apply(final Integer input) {
            Preconditions.checkNotNull(input);
            return true;
        }        
        
        @Override
        public boolean matchesAllOf(final CharSequence in) {
            Preconditions.checkNotNull(in);
            return true;
        }
        
        @Override
        public boolean matchesNoneOf(final CharSequence in) {
            Preconditions.checkNotNull(in);
            return false;
        }
        
        @Override
        public CodePointMatcher or(final CodePointMatcher other) {
            Preconditions.checkNotNull(other);
            return this;
        }
        
        @Override
        public CodePointMatcher negate() {
            return NONE;
        }
    };
    
    /**
     * Matches no code points.
     */
    public static final CodePointMatcher NONE = new CodePointMatcher() {
        @Override
        public CodePointMatcher and(final CodePointMatcher other) {
            Preconditions.checkNotNull(other);
            return this;
        }
        
        @Override
        public boolean apply(final Integer input) {
            Preconditions.checkNotNull(input);
            return false;
        }        
        
        @Override
        public boolean matchesAllOf(final CharSequence in) {
            Preconditions.checkNotNull(in);
            return false;
        }
        
        @Override
        public boolean matchesNoneOf(final CharSequence in) {
            Preconditions.checkNotNull(in);
            return true;
        }
        
        @Override
        public CodePointMatcher or(final CodePointMatcher other) {
            Preconditions.checkNotNull(other);
            return other;
        }
        
        @Override
        public CodePointMatcher negate() {
            return ANY;
        }
    };
    
    /**
     * Returns a {@code CodePointMatcher} that matches any code point in the given {@code CharSequence} 
     * @param sequence a non-null {@code CharSequence}
     * @throws NullPointerException if {@code sequence} is null.
     */
    public static CodePointMatcher anyOf(final CharSequence sequence) {
        Preconditions.checkNotNull(sequence);
        
        if (sequence.length() == 0) {
            return NONE;
        }
        
        final Integer[] codepoints = 
                Iterables.toArray(CharSequences.codePoints(sequence), Integer.class);
        Arrays.sort(codepoints);
        
        return new CodePointMatcher() {
            @Override
            public boolean apply(final Integer input) {
                return Arrays.binarySearch(codepoints, input) >= 0;
            }      
        };
    }

    /**
     * Returns a {@code CodePointMatcher} that matches any characters {@code predicate} matches and always returns false
     * for supplementary code points.
     * @param predicate a non-null {@code Predicate<Character>}
     * @throws NullPointerException if {@code predicate} is null.
     */
    public static CodePointMatcher fromCharacterPredicate(final Predicate<Character> predicate) {
        Preconditions.checkNotNull(predicate);
        
        return new CodePointMatcher() {
            @Override
            public boolean apply(final Integer input) {
                Preconditions.checkNotNull(input);
                
                if (input > Character.MAX_VALUE) {
                    return false;
                }

                return predicate.apply(Character.valueOf((char) input.intValue()));
            }     
        };
    }
    
    /**
     * Returns a {@code CodePointMatcher} that matches any code points in a given range (inclusive).
     * @param start a non-null code point {@code CharSequence}
     * @param finish a non-null code point {@code CharSequence}
     * @throws NullPointerException if either {@code start} or {@code finish} are null.
     * @throws IllegalArgumentException if either {@code start} or {@code finish} are not single Unicode 
     * code points, or if {@code finish} is not greater than {@code start}.
     */
    public static CodePointMatcher inRange(final CharSequence start, final CharSequence finish) {
        return inRange(CharSequences.toCodePoint(start), CharSequences.toCodePoint(finish));
    }
    
    /**
     * Returns a {@code CodePointMatcher} that matches any code points in a given range (inclusive).
     * @param start a valid {@code Integer} code point
     * @param finish a valid {@code Integer} code point
     * @throws IllegalArgumentException If either {@code start} or {@code finish} are not valid Unicode 
     * code points or if {@code finish} is not greater than {@code start}.
     */
    public static CodePointMatcher inRange(final int start, final int finish) {
        Preconditions.checkArgument(
                Character.isValidCodePoint(start) && Character.isValidCodePoint(finish));
        
        final Range<Integer> range = Range.closed(start, finish);
        return new CodePointMatcher() {
            @Override
            public boolean apply(final Integer input) {
                return range.contains(input);
            }      
        };
    }


    /**
     * Returns a {@code CodePointMatcher} that matches any code point not in the given {@code CharSequence} 
     * @param sequence a non-null {@code CharSequence}
     * @throws NullPointerException if {@code sequence} is null.
     */
    public static CodePointMatcher noneOf(final CharSequence sequence) {
        return anyOf(sequence).negate();
    }
    
    private CodePointMatcher(){}

    /**
     * Returns a {@code CodePointMatcher} that matches any code point matched by both {@code this} matcher and {@code other}.
     * @param other a non-null {@code CodePointMatcher}
     * @throws NullPointerException if {@code other} is null.
     */
    public CodePointMatcher and(final CodePointMatcher other) {
        Preconditions.checkNotNull(other);
        
        final CodePointMatcher self = this;
        return new CodePointMatcher() {
            @Override
            public boolean apply(final Integer input) {
                return self.apply(input) && other.apply(input);
            }
        };
    }
    
    @Override
    public abstract boolean apply(final Integer input);
    
    /**
     * Return a {@code Predicate<CharSequence>} that returns true if 
     * a character sequence contains only matching code points.
     */
    public final Predicate<CharSequence>  matchesAllOf() {
        final CodePointMatcher self = this;
        
        return new Predicate<CharSequence>() {
            @Override
            public boolean apply(final CharSequence input) {
                Preconditions.checkNotNull(input);
                return self.matchesAllOf(input);
            }          
        };
    }
    
    /**
     * Returns true if a character sequence contains only matching code points.
     * @param in a non-null {@code CharSequence}
     * @throws NullPointerException if {@code in} is null.
     */
    public boolean matchesAllOf(final CharSequence in) {
        Preconditions.checkNotNull(in);
        for (final int cp : CharSequences.codePoints(in)) {
            if (!this.apply(cp)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Return a {@code Predicate<CharSequence>} that returns true if 
     * a character sequence contains no matching code points.
     */
    public final Predicate<CharSequence>  matchesNoneOf() {
        final CodePointMatcher self = this;
        
        return new Predicate<CharSequence>() {
            @Override
            public boolean apply(final CharSequence input) {
                Preconditions.checkNotNull(input);
                return self.matchesNoneOf(input);
            }          
        };
    }  
    
    /**
     * Returns true if a character sequence contains no matching code points.
     * @param in a non-null {@code CharSequence}
     * @throws NullPointerException if {@code in} is null.
     */
    public boolean matchesNoneOf(final CharSequence in) {
        Preconditions.checkNotNull(in);
        for (final int cp : CharSequences.codePoints(in)) {
            if (this.apply(cp)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     *  Returns a matcher that matches any code point not matched by this matcher.
     */
    public CodePointMatcher negate() {
        final CodePointMatcher self = this;
        return new CodePointMatcher() {
            @Override
            public boolean apply(final Integer input) {
                return !self.apply(input);
            }
        };
    }
    
    /**
     * Returns a {@code CodePointMatcher} that matches any code point matched by either {@code this} matcher or {@code other}.
     * @param other a non-null {@code CodePointMatcher}
     * @throws NullPointerException if {@code other} is null.
     */
    public CodePointMatcher or(final CodePointMatcher other) {
        Preconditions.checkNotNull(other);
        
        final CodePointMatcher self = this;
        return new CodePointMatcher() {
            @Override
            public boolean apply(final Integer input) {
                return self.apply(input) || other.apply(input);
            }
        };
    }
}
