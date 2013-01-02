package restlib.data;


import java.nio.CharBuffer;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.impl.Optionals;
import restlib.impl.Parser;
import restlib.impl.Parsers;
import restlib.impl.Tokenizer;
import restlib.net.HostPort;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * Additional information about the status or transformation of a request or response. 
 * This is typically used to warn about a possible incorrectness introduced
 * by caching operations or transformations applied to the payload of the message.
 */
@Immutable
public final class Warning {          
    static final Parser<Warning> PARSER = new Parser<Warning>() { 
        @Override
        public Optional<Warning> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            
            final int startPos = buffer.position();
            final List<Optional<Object>> tokens =
                    Tokenizer.create(buffer)
                        .read(Parsers.INTEGER_PARSER) // 0
                        .read(Primitives.WHITE_SPACE_PARSER) // 1
                        .read(Primitives.HOST_PORT_OR_PSEUDONYM) // 2
                        .read(Primitives.WHITE_SPACE_PARSER) // 3
                        .read(Primitives.QUOTED_STRING_PARSER) // 4
                        .read(Primitives.WHITE_SPACE_PARSER) // 5
                        .read(Primitives.QUOTED_STRING_PARSER) // 6
                        .tokens();
            
            final Optional<Object> warnCodeOptional = tokens.get(0);
            final Optional<Object> warnAgentOptional = tokens.get(2);
            final Optional<Object> warnTextOptional = tokens.get(4);
            final Optional<Object> warnDateOptional = tokens.get(6);
            
            if (Optionals.isAbsent(warnCodeOptional) || 
                    Optionals.isAbsent(warnAgentOptional)  ||
                    Optionals.isAbsent(warnTextOptional)) {
                buffer.position(startPos);
                return Optional.absent();
            }
            
            final int warnCode = (Integer) warnCodeOptional.get();
            final Object warnAgent = warnAgentOptional.get();
            final String warnText = 
                    warnTextOptional.get().toString();
            
            if (Optionals.isAbsent(warnDateOptional)) {
                return Optional.of(Warning.create(warnCode, warnAgent, warnText, Optional.<HttpDate> absent()));
            } else {
                final HttpDate warnDate = 
                        HttpDate.parse(warnDateOptional.get().toString());
                return Optional.of(Warning.create(warnCode, warnAgent, warnText, Optional.of(warnDate)));
            }
        }       
    };
    
    /**
     * Creates a new Warning.
     * @param warnCode a valid warn code between 100 and 999 inclusive.
     * @param warnAgent the warning agent URI host and port.
     * @param warnText the warning text.
	 * @throws NullPointerException if {@code warnAgent} or {@code warnText} are null.
	 * @throws IllegalArgumentException if {@code warnCode} is not valid, or if warnText
	 * contains characters not allowed within an HTTP quoted string.
     */
    public static Warning create(
            final int warnCode, final HostPort warnAgent, 
            final String warnText) {        
        return Warning.create(warnCode, warnAgent, warnText, Optional.<HttpDate> absent());
    }
    
    /**
     * Creates a new Warning.
     * @param warnCode a valid warn code between 100 and 999 inclusive.
     * @param warnAgent the warning agent URI host and port.
     * @param warnText the warning text.
     * @param warnDate the date the warning should be generated on.
	 * @throws NullPointerException if {@code warnAgent} or {@code warnText} are null.
	 * @throws IllegalArgumentException if {@code warnCode} is not valid, or if warnText
	 * contains characters not allowed within an HTTP quoted string.
     */
    public static Warning create(
            final int warnCode, final HostPort warnAgent, 
            final String warnText, final HttpDate warnDate) { 
    	Preconditions.checkNotNull(warnAgent);
        return Warning.create(warnCode, (Object) warnAgent, warnText, Optional.of(warnDate));
    }
    
    private static Warning create(
            final int warnCode, Object warnAgent, 
            final String warnText, final Optional<HttpDate> warnDate) {
        Preconditions.checkNotNull(warnAgent);
        Preconditions.checkNotNull(warnText);
        Preconditions.checkNotNull(warnDate); 
        
        Preconditions.checkArgument((warnCode >=100) && (warnCode <= 999));
        Preconditions.checkArgument(Primitives.IS_QUOTABLE.apply(warnText));
        
        if (warnAgent instanceof String) {
            try{
                warnAgent = HostPort.parse((String) warnAgent);
            } catch (final IllegalArgumentException e) {
                Preconditions.checkArgument(Primitives.IS_TOKEN.apply((String) warnAgent)); 
            }
        }
        
    	Preconditions.checkArgument(!warnAgent.equals(""));     
        return new Warning(warnCode, warnAgent, warnText, warnDate);
    }
    
    /**
     * Creates a new Warning.
     * @param warnCode a valid warn code between 100 and 999 inclusive.
     * @param warnAgent the warning agent URI host and port or pseudonym.
     * @param warnText the warning text.
	 * @throws NullPointerException if {@code warnAgent} or {@code warnText} are null.
	 * @throws IllegalArgumentException if {@code warnCode} is not valid, or if warnText
	 * contains characters not allowed within an HTTP quoted string.
     */
    public static Warning create(
            final int warnCode, final String warnAgent, 
            final String warnText) {  
        return Warning.create(warnCode, warnAgent, warnText, Optional.<HttpDate> absent());
    }

    /**
     * Creates a new Warning.
     * @param warnCode a valid warn code between 100 and 999 inclusive.
     * @param warnAgent the warning agent URI host and port or pseudonym.
     * @param warnText the warning text.
     * @param warnDate the date the warning should be generated on.
	 * @throws NullPointerException if {@code warnAgent} or {@code warnText} are null.
	 * @throws IllegalArgumentException if {@code warnCode} is not valid, or if warnText
	 * contains characters not allowed within an HTTP quoted string.
     */
    public static Warning create(
            final int warnCode, final String warnAgent, 
            final String warnText, final HttpDate warnDate) {  
        return Warning.create(warnCode, (Object) warnAgent, warnText, Optional.of(warnDate));
    }
    
    private final Object warnAgent;
    private final int warnCode;
    private final Optional<HttpDate> warnDate;
    private final String warnText;

    private Warning(final int warnCode, 
            final Object warnAgent, final String warnText,
            final Optional<HttpDate> warnDate) {
        this.warnCode = warnCode;
        this.warnAgent = warnAgent;
        this.warnText = warnText;
        this.warnDate = warnDate;
    }
    
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Warning) {
            final Warning that = (Warning) obj;
            return this.warnAgent.equals(that.warnAgent) &&
                    (this.warnCode == that.warnCode) &&
                    this.warnDate.equals(that.warnDate) &&
                    this.warnText.equals(that.warnText);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.warnAgent, this.warnCode, this.warnDate, this.warnText);
    }
    
    @Override
    public String toString() {        
        final StringBuilder builder = new StringBuilder();
        builder.append(this.warnCode).append(" ").append(warnAgent);     
            
        builder.append(" ").append(Primitives.encodeQuotedString(this.warnText));
        
        if (!this.warnDate.isPresent()) {
            builder.append(" ").append(Primitives.encodeQuotedString(this.warnDate.toString()));
        }
        return builder.toString();
    }
    
    /**
     * Returns the name or pseudonym of the server adding the Warning header field.
     * The returned object is either an instance of HostPort or String in the
     * case of that the warn-agent is a pseudonym.
     */
    public Object warnAgent() {
        return warnAgent;
    }
    
    /**
     * Returns the three digit warn-code.
     */
    public int warnCode() {
        return warnCode;
    }

    /**
     * Returns the date the warning was generated if available.
     */
    public Optional<HttpDate> warnDate() {
        return warnDate;
    }

    /**
     * Returns the warning text.
     */
    public String warnText() {
        return warnText;
    }
}
