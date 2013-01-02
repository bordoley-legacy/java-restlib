package restlib.data;

import com.google.common.base.CharMatcher;

final class CharMatchers {
    static final CharMatcher B64_MATCHER;
    static final CharMatcher CTEXT_MATCHER;   
    static final CharMatcher ETAGC_MATCHER;
    static final CharMatcher FIELD_VALUE_MATCHER;
    static final CharMatcher HOST_PORT;
    static final CharMatcher QD_TEXT_MATCHER;
    static final CharMatcher QUOTED_CPAIR_CHAR_MATCHER;
    static final CharMatcher QUOTED_PAIR_CHAR_MATCHER;
    static final CharMatcher REASON_PHRASE;
    static final CharMatcher TCHAR_MATCHER;
    static final CharMatcher WHITE_SPACE_MATCHER;
    
    static {
        WHITE_SPACE_MATCHER = CharMatcher.anyOf(" \t");
        final CharMatcher VCHAR_MATCHER = CharMatcher.inRange('\u0021', '\u007E');
        final CharMatcher OBS_TEXT_MATCHER = CharMatcher.inRange('\u0080','\u00FF');
        
        TCHAR_MATCHER = CharMatcher.inRange('a', 'z')
                .or(CharMatcher.inRange('A', 'Z'))
                .or(CharMatcher.inRange('0', '9'))
                .or(CharMatcher.anyOf("!#$%&'*+-.^_`|~"));
        
        HOST_PORT = TCHAR_MATCHER.or(CharMatcher.is(':'));
        
        QUOTED_PAIR_CHAR_MATCHER = 
                WHITE_SPACE_MATCHER.or(VCHAR_MATCHER).or(OBS_TEXT_MATCHER);
        
        QD_TEXT_MATCHER = 
                WHITE_SPACE_MATCHER
                    .or(CharMatcher.is('\u0021'))
                .or(CharMatcher.inRange('\u0023', '\u005B'))
                .or(CharMatcher.inRange('\u005D', '\u007E'))
                .or(OBS_TEXT_MATCHER);
        
        CTEXT_MATCHER = 
                WHITE_SPACE_MATCHER
                    .or(CharMatcher.inRange('\u0021', '\''))
                    .or(CharMatcher.inRange('\u002A', '\u005B'))
                    .or(CharMatcher.inRange('\u005D', '\u007E'))
                    .or(OBS_TEXT_MATCHER);
        
        QUOTED_CPAIR_CHAR_MATCHER = 
                WHITE_SPACE_MATCHER
                    .or(VCHAR_MATCHER)
                    .or(OBS_TEXT_MATCHER);
        
        B64_MATCHER = CharMatcher.inRange('a', 'z')
                .or(CharMatcher.inRange('A', 'Z'))
                .or(CharMatcher.inRange('0', '9'))
                .or(CharMatcher.anyOf("-._~+/"));

        ETAGC_MATCHER = CharMatcher.is('\u0021')
                .or(CharMatcher.inRange('\u0023', '\u007E'))
                .or(OBS_TEXT_MATCHER);
        
        REASON_PHRASE = CharMatchers.WHITE_SPACE_MATCHER.or(VCHAR_MATCHER).or(OBS_TEXT_MATCHER);
        
        FIELD_VALUE_MATCHER = WHITE_SPACE_MATCHER.or(VCHAR_MATCHER).or(OBS_TEXT_MATCHER);
    }
}
