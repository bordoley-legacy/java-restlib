package restlib.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Static methods pertaining to instances of {@code CharSequence}
 *
 */
public final class CharSequences {
    /**
     * Returns an unmodifiable {@code Iterable} view of the {@code Character} values in {@code in}. 
     * 
     * <p>Note: The returned {@code Iterable} maintains a defensive copy of the CharSequence, and
     * any subsequent changes to the CharSequence will not be reflected when iterating.
     * 
     * @param in a non-null {@code CharSequence}
     * @throws NullPointerException if {@code in} is null;
     */
    public static Iterable<Character> characters(final CharSequence in) {
        Preconditions.checkNotNull(in);
  
        return new Iterable<Character>() {
            final String defensiveCopy = in.toString(); 
            
            @Override
            public Iterator<Character> iterator() {
                return new UnmodifiableIterator<Character>() {
                    int index = 0;
                    
                    @Override
                    public boolean hasNext() {
                        return index < defensiveCopy.length();
                    }

                    @Override
                    public Character next() {
                        if (!hasNext()) { 
                            throw new NoSuchElementException();
                        }
                        return defensiveCopy.charAt(index++);
                    }           
                };
            }          
        };
    }
    
    /**
     * Returns an unmodifiable {@code Iterable} view of the {@code Integer} code points in {@code in}. 
     * 
     * <p>Note: The returned {@code Iterable} maintains a defensive copy of the CharSequence, and
     * any subsequent changes to the CharSequence will not be reflected when iterating.
     * 
     * @param in a non-null {@code CharSequence}
     * @throws NullPointerException if {@code in} is null;
     */
    public static Iterable<Integer> codePoints(final CharSequence in) {
        Preconditions.checkNotNull(in);

        return new Iterable<Integer>() {
            final String defensiveCopy = in.toString();
            
            @Override
            public Iterator<Integer> iterator() {
                return new UnmodifiableIterator<Integer>() {
                    int index = 0;
                    
                    @Override
                    public boolean hasNext() {
                        return index < defensiveCopy.length();
                    }
                    
                    @Override
                    public Integer next() {
                        if (!hasNext()) { 
                            throw new NoSuchElementException();
                        }
                        final int result = Character.codePointAt(defensiveCopy, index);
                        index += Character.charCount(result);
                        return result;
                    }    
                };
            }           
        };
    }
  
    /**
     * Returns an unmodifiable {@code Iterable} view of the code points in {@code in} as instances of {@code String}. 
     * 
     * <p>Note: The returned {@code Iterable} maintains a defensive copy of the CharSequence, and
     * any subsequent changes to the CharSequence will not be reflected when iterating.
     * 
     * @param in a non-null {@code CharSequence}
     * @throws NullPointerException if {@code in} is null;
     */
    public static Iterable<String> codePointsAsStrings(final CharSequence in) {
        Preconditions.checkNotNull(in);
        return Iterables.transform(codePoints(in), new Function<Integer, String>() {
            @Override
            public String apply(final Integer codePoint) {
                return fromCodepoint(codePoint);
            }        
        });
    }
    
    /**
     * Returns the {@code String} representation of the code point.
     * @param codePoint a valid Unicode code point.
     * @throws IllegalArgumentException if {@code codePoint} is not a valid Unicode code point.
     */
    public static String fromCodepoint(final int codePoint) {
        return String.valueOf(Character.toChars(codePoint));
    }
    
    /**
     * Returns whether in is an empty {@code CharSequence}
     * @param in a non-null CharSequence.
     * @throw NullPointerException if {@code in} is null.
     */
    public static boolean isEmpty(final CharSequence in) {
    	Preconditions.checkNotNull(in);
    	return in.length() == 0;
    }
    
    /**
     * Returns the {@code int} code point representation of a single Unicode character encoded in a {@code CharSequence}.
     * @param in a non-null {@code CharSequence} containing a single Unicode character.
     * @throws NullPointerException if {@code in} is null.
     * @throws IllegalArgumentException if {@code in} does not include exactly one Unicode character.
     */
    public static int toCodePoint(final CharSequence in) {
        Preconditions.checkNotNull(in);
        Preconditions.checkArgument(in.length() >  0);
        final int cp = Character.codePointAt(in, 0);
        Preconditions.checkArgument(Character.charCount(cp) == in.length());   
        return cp;
    }
    
    private CharSequences(){}
}
