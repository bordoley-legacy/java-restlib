package restlib.net;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import restlib.impl.CharSequences;
import restlib.impl.CodePointMatcher;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.primitives.UnsignedBytes;

/**
 * Factory for generating {@code Function}s that can be used to 
 * encode and decode URI encoded strings.
 */
public final class UriEncoding {    
    
    /**
     * A {@code Function} that decodes a URI encoded String using the UTF-8 charset.
     */
    public static final Function<CharSequence, String> UTF8_DECODE = 
            decoderUsingCharset(Charsets.UTF_8);
    static final Function<CharSequence, String> UTF8_DECODE_FRAGMENT =
            utf8DecoderCharsOnly(IRIPredicates.FRAGMENT_SAFE_CHAR);
    static final Function<CharSequence, String> UTF8_DECODE_IFRAGMENT =
            utf8Decoder(IRIPredicates.IFRAGMENT_SAFE_CODEPOINT);
    static final Function<CharSequence, String> UTF8_DECODE_IQUERY =
            utf8Decoder(IRIPredicates.IQUERY_SAFE_CODEPOINT);
    static final Function<CharSequence, String> UTF8_DECODE_IUSER_INFO =
            utf8Decoder(IRIPredicates.IUSER_INFO_SAFE_CODEPOINT);
    static final Function<CharSequence, String> UTF8_DECODE_PATH_ISEGMENT =
            utf8Decoder(IRIPredicates.ISEGMENT_SAFE_CODEPOINT);   
    static final Function<CharSequence, String> UTF8_DECODE_PATH_SEGMENT =
            utf8DecoderCharsOnly(IRIPredicates.SEGMENT_SAFE_CHAR);
    static final Function<CharSequence, String> UTF8_DECODE_QUERY =
            utf8DecoderCharsOnly(IRIPredicates.QUERY_SAFE_CHAR);
    static final Function<CharSequence, String> UTF8_DECODE_USER_INFO =
            utf8DecoderCharsOnly(IRIPredicates.USER_INFO_SAFE_CHAR); 
    static final Function<CharSequence, String> UTF8_ENCODE_FRAGMENT =
            utf8EncoderWithSafeChars(IRIPredicates.FRAGMENT_SAFE_CHAR);
    static final Function<CharSequence, String> UTF8_ENCODE_IFRAGMENT =
            utf8Encoder(IRIPredicates.IFRAGMENT_SAFE_CODEPOINT);
    static final Function<CharSequence, String> UTF8_ENCODE_IQUERY =
            utf8Encoder(IRIPredicates.IQUERY_SAFE_CODEPOINT);
    static final Function<CharSequence, String> UTF8_ENCODE_IUSER_INFO =
            utf8Encoder(IRIPredicates.IUSER_INFO_SAFE_CODEPOINT);
    static final Function<CharSequence, String> UTF8_ENCODE_PATH_ISEGMENT =
            utf8Encoder(IRIPredicates.ISEGMENT_SAFE_CODEPOINT);
    static final Function<CharSequence, String> UTF8_ENCODE_PATH_SEGMENT =
            utf8EncoderWithSafeChars(IRIPredicates.SEGMENT_SAFE_CHAR);
    static final Function<CharSequence, String> UTF8_ENCODE_QUERY =
            utf8EncoderWithSafeChars(IRIPredicates.QUERY_SAFE_CHAR);
    static final Function<CharSequence, String> UTF8_ENCODE_USER_INFO =
            utf8EncoderWithSafeChars(IRIPredicates.USER_INFO_SAFE_CHAR);
    
    @VisibleForTesting
    static Function<CharSequence, String> charsetPercentDecoder(final Charset charset) {
        Preconditions.checkNotNull(charset);
        
        final ThreadLocal<CharsetDecoder> decoder = new ThreadLocal<CharsetDecoder>() {
            @Override
            protected CharsetDecoder initialValue() {
                return charset.newDecoder()
                        .onUnmappableCharacter(CodingErrorAction.REPORT)
                        .onMalformedInput(CodingErrorAction.REPORT);
            }
        };
        
        return new Function<CharSequence, String>() {
            @Override
            public String apply(final CharSequence in) {
                Preconditions.checkNotNull(in);
                
                final ByteBuffer buffer = ByteBuffer.allocate(in.length() / 3);              
                for (int i = 0; i < in.length(); i+=3) { 
                    Preconditions.checkArgument(in.charAt(i) == '%', in);
                    Preconditions.checkArgument(in.length() > (i + 2), "Invalid URI encoded string: " + in);
      
                    final int byteVal;
                    try {
                        byteVal = Integer.parseInt(in.subSequence(i+1, i+3).toString(), 16); 
                    } catch (final NumberFormatException e) {
                        throw new IllegalArgumentException(e);
                    }          
                   
                    buffer.put((byte) byteVal);
                }
                
                try {
                    buffer.rewind();
                    return decoder.get().decode(buffer.asReadOnlyBuffer()).toString();
                } catch (final CharacterCodingException e) {
                    throw new IllegalArgumentException(e);
                }               
            }            
        };
    }
    
    @VisibleForTesting
    static Function<Integer, String> charsetPercentEncoder(final Charset charset) {
        Preconditions.checkNotNull(charset);
        return new Function<Integer, String>() {
            @Override
            public String apply(final Integer codePoint) {
                Preconditions.checkNotNull(codePoint);
                
                final StringBuilder encoded = new StringBuilder();
                final byte[] bytes = charset.encode(CharBuffer.wrap(Character.toChars(codePoint))).array();
                
                for (final byte b : bytes) {
                    final int value = UnsignedBytes.toInt(b);
                    if (value != 0) {
                        final String hex = Integer.toHexString(value).toUpperCase();
                        
                        encoded.append("%");  
                        if (hex.length()  == 1) {
                            encoded.append('0');
                        }
                        encoded.append(hex);
                    }    
                }
                return encoded.toString();
            }           
        };
    }
    
    /**
     * Returns a {@code Function} that decodes only the code points that 
     * match {@code codePointsToDecode} using the Charset {@code charset}.
     * @param codePointsToDecode a {@code Predicate} matching all the codepoints that should be decoded by the decoder.
     * @param charset any non-null {@code Charset}
     * @throws NullPointerException if {@code codePointsToDecode} or {@code charset} are null.
     */
    public static Function<CharSequence, String> decoder(final Predicate<Integer> codePointsToDecode, final Charset charset) {
        Preconditions.checkNotNull(codePointsToDecode);
        Preconditions.checkNotNull(charset);
        return decoderBase(
                Functions.compose(
                        UriEncoding.encoder(codePointsToDecode, charset), 
                        UriEncoding.charsetPercentDecoder(charset)));
    }
    
    private static Function<CharSequence, String> decoderBase(final Function<CharSequence, String> percentDecoder) {   
        Preconditions.checkNotNull(percentDecoder);
        
        return new Function<CharSequence, String>() {
            @Override
            public String apply(final CharSequence in) {
                Preconditions.checkNotNull(in);
                
                final StringBuilder builder = new StringBuilder(in.length());                
                for (int i = 0; i < in.length();) {
                    final char c = in.charAt(i);

                    if (c == '%') {
                        final int start = i;
                        for (; (i < in.length()) && (in.charAt(i) == '%'); i+=3) { 
                            Preconditions.checkArgument(in.length() > (i + 2), "Invalid URI encoded string: " + in);
                        }
                        builder.append(percentDecoder.apply(in.subSequence(start, i)));
                    } else {
                        builder.append(c);
                        i++;
                    }
                }
                return builder.toString();
            }        
        };
    }
    
    /**
     * Returns a {@code Function} that decodes a URI encoded string using the Charset {@code charset}.
     * @param charset any non-null {@code Charset}
     * @throws NullPointerException if {@code charset} is null.
     */
    public static Function<CharSequence, String> decoderUsingCharset(final Charset charset) {
        return decoder(Predicates.<Integer> alwaysTrue(), charset);
    }
    
    /**
     * Returns a {@code Function} that URI encodes all code points in the inputs string except 
     * those matching {@code safeCodePoints} using the Charset {@code charset}.
     * @param safeCodePoints a {@code Predicate} matching all the code points that should not be encoded.
     * @param charset any non-null {@code Charset}
     * @throws NullPointerException if {@code safeCodePoints} or {@code charset} are null.
     */
    public static Function<CharSequence, String> encoder(
            final Predicate<Integer> safeCodePoints, 
            final Charset charset) {
        return encoderBase(safeCodePoints, charsetPercentEncoder(charset));
    }
    
    private static Function<CharSequence, String> encoderBase(
            final Predicate<Integer> safeCodePoints, 
            final Function<Integer, String> percentEncoder) {
        Preconditions.checkNotNull(safeCodePoints);
        Preconditions.checkNotNull(percentEncoder);
        
        return new Function<CharSequence, String>() {
            @Override
            public String apply(final CharSequence in) {
                Preconditions.checkNotNull(in);     
                final StringBuilder encoded = new StringBuilder(in.length() * 3);

                for (final Integer cp : CharSequences.codePoints(in)) {
                    encoded.append(safeCodePoints.apply(cp) ? CharSequences.fromCodepoint(cp) : percentEncoder.apply(cp));
                }                 
                return encoded.toString();
            }
        };    
    }
    
    /**
     * Returns a {@code Function} that decodes only the code points that match {@code codePointsToDecode} using the UTF-8 charset.
     * @param codePointsToDecode a {@code Predicate} matching all the codepoints that should be decoded by the decoder.
     * @throws NullPointerException if {@code codePointsToDecode}.
     */
    public static Function<CharSequence, String> utf8Decoder(final Predicate<Integer> codePointsToDecode) {
        return decoder(codePointsToDecode, Charsets.UTF_8);
    }
    
    /**
     * Returns a {@code Function} that decodes only the characters that match {@code charsToDecode} using the UTF-8 charset.
     * @param charsToDecode a {@code Predicate} matching all the character that should be decoded by the decoder.
     * @throws NullPointerException if {@code codePointsToDecode}.
     */
    public static Function<CharSequence, String> utf8DecoderCharsOnly(final Predicate<Character> charsToDecode) {
        return decoder(CodePointMatcher.fromCharacterPredicate(charsToDecode), Charsets.UTF_8);
    }

    /**
     * Returns a {@code Function} that URI encodes all code points in the inputs string except 
     * those matching {@code safeCodePoints} using the UTF-8 charset.
     * @param safeCodePoints a {@code Predicate} matching all the code points that should not be encoded.
     * @throws NullPointerException if {@code safeCodePoints} is null.
     */
    public static Function<CharSequence, String> utf8Encoder(final Predicate<Integer> safeCodePoints) {
        return encoder(safeCodePoints, Charsets.UTF_8);
    }
    
    /**
     * Returns a {@code Function} that URI encodes all characters in the inputs string except 
     * those matching {@code safeChars} using the UTF-8 charset.
     * @param safeChars a {@code Predicate} matching all the code points that should not be encoded.
     * @throws NullPointerException if {@code safeChars} is null.
     */
    public static Function<CharSequence, String> utf8EncoderWithSafeChars(final Predicate<Character> safeChars) {
        return encoder(CodePointMatcher.fromCharacterPredicate(safeChars), Charsets.UTF_8);
    }
    
    private UriEncoding(){}
}
