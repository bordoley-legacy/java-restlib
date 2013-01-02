package restlib.impl;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * Static utilities methods pertaining to instances of {@code Optional}. 
 */
public final class Optionals {
    /**
     * Returns whether the Optional value is absent.
     * @param optional a non-null Optional.
     * @throws NullPointerException if {@code optional} is null.
     */
    public static boolean isAbsent(final Optional<?> optional) {
        Preconditions.checkNotNull(optional);
        return !optional.isPresent();
    }
    
    /**
     * Returns the result of call toString() on the optional value if present.
     * @param optional a non-null Optional value that is present.
     * @throws NullPointerException if {@code optional} is null.
     * @throws IllegalArgumentException if optional is not present.
     */
    public static String toString(final Optional<?> optional) {
        Preconditions.checkNotNull(optional);
        Preconditions.checkArgument(optional.isPresent());
        return optional.get().toString();
    }
    
    /**
     * Returns the result of calling toString on the optional value if present,
     * otherwise the empty string is returned.
     * @param optional a non-null Optional value.
     * @throws NullPointerException if {@code optional} is null.
     */
    public static String toStringOrEmpty(final Optional<?> optional) {
        Preconditions.checkNotNull(optional);
        return optional.isPresent() ? optional.get().toString() : "";
    }
    
    private Optionals(){}
}
