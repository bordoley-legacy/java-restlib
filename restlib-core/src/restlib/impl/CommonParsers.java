package restlib.impl;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedInts;
import com.google.common.primitives.UnsignedLongs;

/**
 * Static utilities for parsing unsigned numbers.
 */
public final class CommonParsers {
    private static final CharMatcher DIGITS = CharMatcher.inRange('0', '9');
    
    /**
     * Parses a {@code String} to an unsigned {@code int}.
     * @param in a non-null string containing only valid ASCII digits.
     * @throws NullPointerException if {@code in} is null.
     * @throws IllegalArgumentException if {@code in} contains any characters other than ASCII digits.
     */
    public static int parseUnsignedInteger(final String in) {
        Preconditions.checkNotNull(in);
        Preconditions.checkArgument(!in.isEmpty());
        
        // Java 1.7 allows Strings starting with +/- 
        // as the leading char, we don't.
        Preconditions.checkArgument(DIGITS.apply(in.charAt(0)));
        return UnsignedInts.parseUnsignedInt(in);
    }
    
    /**
     * Parses a {@code String} to an unsigned {@code long}.
     * @param in a non-null string containing only valid ASCII digits.
     * @throws NullPointerException if {@code in} is null.
     * @throws IllegalArgumentException if {@code in} contains any characters other than ASCII digits.
     */
    public static long parseUnsignedLong(final String in) {
        Preconditions.checkNotNull(in);
        Preconditions.checkArgument(!in.isEmpty());
        
        // Java 1.7 allows Strings starting with +/-
        // as the leading char, we don't.
        Preconditions.checkArgument(DIGITS.apply(in.charAt(0)));
        return UnsignedLongs.parseUnsignedLong(in);
    }
    
    private CommonParsers(){}
}
