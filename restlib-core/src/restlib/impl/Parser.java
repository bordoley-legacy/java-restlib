package restlib.impl;

import java.nio.CharBuffer;

import com.google.common.base.Optional;

/**
 * Interface for parsing objects from the contents of a {@code CharBuffer}.
 */
public interface Parser<T> {
    /**
     * Returns the parsed object from the current position of {@code buffer}. If a parser
     * is unable to perform a parsing action, the position of {@code buffer} is reset to 
     * it original position and Optional.absent() is returned.
     * @param buffer a non-null {@code CharBuffer}.
     * @throws NullPointerException if {@code buffer} is null.
     */
    public Optional<T> parse(final CharBuffer buffer);
}
