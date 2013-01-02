package restlib.data;


import java.nio.CharBuffer;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.impl.CharBuffers;
import restlib.impl.Parser;
import restlib.impl.Parsers;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * Object representation of an HTTP comment.
 */
@Immutable
public final class Comment {        
    private static final String CLOSE_PARENTHESES = ")";   
    private static final char CLOSE_PARENTHESES_CHAR = ')'; 
    private static final char ESCAPE_CHAR = '\\';    
    private static final String OPEN_PARENTHESES = "(";
    private static final char OPEN_PARENTHESES_CHAR = '(';
    
    static final Parser<Comment> PARSER = new Parser<Comment>() {        
        @Override
        public Optional<Comment> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);    
            final int startPos = buffer.position();
            final Optional<Comment> retval = readComment(buffer);
            if (retval.isPresent()) {
                return retval;
            } else {
                buffer.position(startPos);
                return Optional.absent();
            }           
        }

        private Optional<Comment> readComment(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);    

            final int startPos = buffer.position();
            if (startPos >= buffer.capacity()) {
                return Optional.absent();
            }
            
            if (buffer.get() != OPEN_PARENTHESES_CHAR) {
                CharBuffers.pushback(buffer);
                return Optional.absent();
            }
            
            final ImmutableList.Builder<Object> builder = ImmutableList.builder();
            final StringBuilder sb = new StringBuilder();
 
            while(buffer.position() < buffer.capacity()) {
                final char c = buffer.get(); 
                if (c == OPEN_PARENTHESES_CHAR) {
                    if (sb.length() > 0) {
                        builder.add(sb.toString());                
                        sb.setLength(0);  
                    }
               
                    CharBuffers.pushback(buffer);
                    final Optional<Comment> next = readComment(buffer);
                    if (next.isPresent()) {
                        builder.add((Comment) next.get());
                    } else {
                        return Optional.absent();
                    }
                } else if (c == CLOSE_PARENTHESES_CHAR) {
                    if (sb.length() > 0) {
                        builder.add(sb.toString());
                    }
                    return Optional.of(new Comment(builder.build()));
                } else if (c == ESCAPE_CHAR) { 
                    final char next = buffer.get();
                    if (!CharMatchers.QUOTED_CPAIR_CHAR_MATCHER.matches(next)) {
                        // Illegal escaped comment char
                        return Optional.absent();                   
                    } 
                    sb.append(next);                 
                } else if (CharMatchers.CTEXT_MATCHER.matches(c)) { 
                    sb.append(c);
                } else {
                    // Illegal comment char
                    return Optional.absent();
                }
            }
            // Reached EOF
            return Optional.absent();
        }       
    };
    
    private static String encodeCommentText(final CharSequence in) {
        Preconditions.checkNotNull(in);    
        final StringBuilder retval = new StringBuilder(in.length() * 2);

        for (int i = 0; i < in.length(); i++) {
            final char c = in.charAt(i);
            if (CharMatchers.CTEXT_MATCHER.matches(c)) {
                retval.append(c);
            } else if (c == OPEN_PARENTHESES_CHAR  || 
                       c == CLOSE_PARENTHESES_CHAR || 
                       c == ESCAPE_CHAR) {
                retval.append(ESCAPE_CHAR).append(c);
            } else {
                throw new IllegalArgumentException();
            }
        }

        return retval.toString();
    }
    
    /**
     * Parses a comment from its String representation.
     * @throws NullPointerException if {@code comment} is null.
     * @throws IllegalArgumentException if {@code comment} not parseable.
     */
    public static Comment parse(final String comment) {
        return Parsers.parseWithParser(comment, PARSER);
    }
       
    private final ImmutableList<Object> contents;

    Comment(final ImmutableList<Object> contents) {
        this.contents = contents;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Comment) {
            final Comment that = (Comment) obj;
            return this.contents.equals(that.contents);           
        }
        
        return false;      
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.contents);
    }  
    
    @Override
    public String toString() {        
        final StringBuilder builder = new StringBuilder();
        builder.append(OPEN_PARENTHESES);
        
        for (final Object obj : contents) {                    
            if (obj instanceof String) {
                builder.append(encodeCommentText(obj.toString()));
            } else {
                builder.append((Comment) obj);
            }
        }
      
        builder.append(CLOSE_PARENTHESES);     
        return builder.toString();
    }
}
