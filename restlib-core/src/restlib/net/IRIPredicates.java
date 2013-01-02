package restlib.net;

import static restlib.impl.CodePointMatcher.inRange;
import restlib.impl.CharSequences;
import restlib.impl.CodePointMatcher;

import com.google.common.base.CharMatcher;
import com.google.common.base.Predicate;

final class IRIPredicates {
    private static Predicate<CharSequence> matchesAll(final CharMatcher charMatcher) {
        return new Predicate<CharSequence>() {
            @Override
            public boolean apply(final CharSequence input) {
                return charMatcher.matchesAllOf(input);
            }            
        };
    }
     
    static final Predicate<CharSequence> IS_FRAGMENT;
    static final Predicate<CharSequence> IS_IFRAGMENT;
    static final Predicate<CharSequence> IS_IQUERY;
    static final Predicate<CharSequence> IS_ISEGMENT;
    static final Predicate<CharSequence> IS_IUNRESERVED;
    static final Predicate<Integer> IS_IUNRESERVED_CODEPOINT;
    static final Predicate<CharSequence> IS_IUSERINFO;
    static final Predicate<CharSequence> IS_QUERY;
    static final Predicate<CharSequence> IS_SCHEME;
    static final Predicate<CharSequence> IS_SEGMENT;
    static final Predicate<CharSequence> IS_UNRESERVED;
    static final Predicate<Character> IS_UNRESERVED_CHARACTER;
    static final Predicate<CharSequence> IS_URI_CHARS;
    static final Predicate<CharSequence> IS_USER_INFO;
    
    static final CharMatcher QUERY_SAFE_CHAR;
    static final CharMatcher SEGMENT_SAFE_CHAR;
    static final CharMatcher USER_INFO_SAFE_CHAR;
    static final CharMatcher FRAGMENT_SAFE_CHAR;
    
    static final CodePointMatcher IQUERY_SAFE_CODEPOINT;
    static final CodePointMatcher ISEGMENT_SAFE_CODEPOINT;
    static final CodePointMatcher IUSER_INFO_SAFE_CODEPOINT;
    static final CodePointMatcher IFRAGMENT_SAFE_CODEPOINT;
    
    static {
        final CharMatcher percentEncoded = CharMatcher.is('%');
        final CharMatcher subDelims = CharMatcher.anyOf("!$&'()*+,;=");
        final CharMatcher genDelims = CharMatcher.anyOf(":/?#[]@]");
        final CharMatcher reserved = genDelims.or(subDelims);
        final CharMatcher unreserved = CharMatcher.inRange('a','z')
                                            .or(CharMatcher.inRange('A','Z'))
                                            .or(CharMatcher.inRange('0','9'))
                                            .or(CharMatcher.anyOf("-._~"));
        
        final CharMatcher pchar = unreserved.or(percentEncoded).or(subDelims).or(CharMatcher.anyOf(":@"));
        FRAGMENT_SAFE_CHAR = pchar.or(CharMatcher.anyOf("/?"));
        QUERY_SAFE_CHAR = FRAGMENT_SAFE_CHAR;
        SEGMENT_SAFE_CHAR = pchar;
        USER_INFO_SAFE_CHAR = unreserved.or(percentEncoded).or(subDelims).or (CharMatcher.is(':'));
        
        IS_FRAGMENT = matchesAll(FRAGMENT_SAFE_CHAR);
        IS_QUERY = matchesAll(QUERY_SAFE_CHAR);
        IS_UNRESERVED = matchesAll(unreserved);    
        IS_UNRESERVED_CHARACTER = unreserved;
        IS_SEGMENT = matchesAll(SEGMENT_SAFE_CHAR);
        IS_URI_CHARS = matchesAll(percentEncoded.or(reserved).or(unreserved));
        IS_USER_INFO = matchesAll(USER_INFO_SAFE_CHAR);
        
        final CodePointMatcher uschar =
                CodePointMatcher
                    .inRange("\u00A0", "\uD7FF")
                    .or(inRange("\u00A0","\uD7FF"))
                    .or(inRange("\u00A0","\uD7FF"))
                    .or(inRange("\uF900","\uFDCF"))
                    .or(inRange("\uFDF0","\uFFEF"))
                    
                    .or(inRange("\uD800\uDC00","\uD83F\uDFFD"))
                    .or(inRange("\uD840\uDC00","\uD87F\uDFFD"))
                    .or(inRange("\uD880\uDC00","\uD8BF\uDFFD"))
                    .or(inRange("\uD8C0\uDC00","\uD8FF\uDFFD"))
                    .or(inRange("\uD900\uDC00","\uD93F\uDFFD"))
                    .or(inRange("\uD940\uDC00","\uD97F\uDFFD"))
                    .or(inRange("\uD980\uDC00","\uD9BF\uDFFD"))
                    .or(inRange("\uD9C0\uDC00","\uD9FF\uDFFD"))
                    .or(inRange("\uDA00\uDC00","\uDA3F\uDFFD"))
                    .or(inRange("\uDA40\uDC00","\uDA7F\uDFFD"))
                    .or(inRange("\uDA80\uDC00","\uDABF\uDFFD"))
                    .or(inRange("\uDAC0\uDC00","\uDAFF\uDFFD"))
                    .or(inRange("\uDB00\uDC00","\uDB3F\uDFFD"))
                    .or(inRange("\uDB40\uDC00","\uDB7F\uDFFD"));

        final CodePointMatcher iprivate = 
                CodePointMatcher.inRange("\uE000", "\uF8FF")
                    .or(inRange("\uDB80\uDC00","\uDBBF\uDFFD"))
                    .or(inRange("\uDBC0\uDC00","\uDBFF\uDFFD"));
        
        final CodePointMatcher iunreserved = CodePointMatcher.fromCharacterPredicate(unreserved).or(uschar);
        final CodePointMatcher ipchar = CodePointMatcher.fromCharacterPredicate(pchar).or(iunreserved);
        IFRAGMENT_SAFE_CODEPOINT = CodePointMatcher.fromCharacterPredicate(FRAGMENT_SAFE_CHAR).or(ipchar);
        IQUERY_SAFE_CODEPOINT = CodePointMatcher.fromCharacterPredicate(QUERY_SAFE_CHAR).or(ipchar).or(iprivate);
        ISEGMENT_SAFE_CODEPOINT = ipchar;       
        IUSER_INFO_SAFE_CODEPOINT = CodePointMatcher.fromCharacterPredicate(USER_INFO_SAFE_CHAR).or(iunreserved);

        IS_IFRAGMENT = IFRAGMENT_SAFE_CODEPOINT.matchesAllOf();
        IS_IQUERY = IQUERY_SAFE_CODEPOINT.matchesAllOf();
        IS_ISEGMENT = ISEGMENT_SAFE_CODEPOINT.matchesAllOf();
        IS_IUNRESERVED = iunreserved.matchesAllOf();
        IS_IUNRESERVED_CODEPOINT = iunreserved;
        IS_IUSERINFO = IUSER_INFO_SAFE_CODEPOINT.matchesAllOf();
        
        IS_SCHEME = new Predicate<CharSequence>() {
            final CharMatcher latinAlphabet = CharMatcher.inRange('a', 'z').or(CharMatcher.inRange('A', 'Z'));
            final CharMatcher scheme = 
                    latinAlphabet
                        .or(CharMatcher.inRange('0', '9'))
                        .or(CharMatcher.anyOf("+-."));
            @Override
            public boolean apply(final CharSequence input) {      	
                if (!CharSequences.isEmpty(input) && latinAlphabet.matches(input.charAt(0))) {
                    return scheme.matchesAllOf(input.subSequence(1, input.length()));
                }
                return false;
            }          
        };
    }
    
    private IRIPredicates(){}
}
