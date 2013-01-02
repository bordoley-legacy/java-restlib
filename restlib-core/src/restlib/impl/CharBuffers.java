package restlib.impl;

import java.nio.CharBuffer;

import com.google.common.base.Preconditions;

/**
 * Static utilities methods pertaining to instances of {@code CharBuffer}. 
 */
public final class CharBuffers {
    /**
     * Resets the position of a buffer to the previous char position after a call to
     * CharBuffer.get() is made.
     * @param buffer a non-null {@code CharBuffer}.
     * @throws NullPointerException if {@code buffer} is null.
     * @throws IllegalStateException if the position of {@code buffer} is 0.
     * 
     */
    public static void pushback(final CharBuffer buffer) {
        Preconditions.checkNotNull(buffer);
        Preconditions.checkState(buffer.position() > 0);
        buffer.position(buffer.position() - 1);    
    }
    
    private CharBuffers(){}
}
