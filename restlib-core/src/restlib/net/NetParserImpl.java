package restlib.net;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

final class NetParserImpl {
    private static final Pattern emailPattern;
    private static final Pattern iAuthorityPattern;  
    private static final Pattern iriPattern;
    
    static {        
        final String authority = "((^.+)?@)?(.*?)(:(\\d+$))?";
        
        final String iri = 
                "^(([^:/?#]+):)?" + // Scheme
                "(//([^/?#]*))?" + // Authority
                "([^?#]*)" + // Path
                "(\\?([^#]*))?" + // Query
                "(#(.*))?"; // Fragment  
        
        final String email = "(.+)@([^@]+$)";
        
        emailPattern = Pattern.compile(email);
        iAuthorityPattern = Pattern.compile(authority);
        iriPattern = Pattern.compile(iri);
    }
    
    static List<String> parseEmail(final CharSequence email) {
        Preconditions.checkNotNull(email);
        
        final Matcher matcher = emailPattern.matcher(email);
        Preconditions.checkArgument(matcher.matches(), "Invalid Email Address: " + email);
        
        return ImmutableList.of(matcher.group(1), matcher.group(2));      
    }

    static List<String> parseIRI(final CharSequence iri) {
        final ImmutableList.Builder<String> builder = ImmutableList.builder();
        
        final Matcher matcher = iriPattern.matcher(iri);
        Preconditions.checkArgument(matcher.matches(), "Invalid IRI: " + iri);
        
        final Matcher authorityMatcher = iAuthorityPattern.matcher(Strings.nullToEmpty(matcher.group(4)));
        Preconditions.checkArgument(authorityMatcher.matches(), "Invalid IRI: " + iri);

        // Scheme
        builder.add(Strings.nullToEmpty(matcher.group(2)));

        // UserInfo
        builder.add(Strings.nullToEmpty(authorityMatcher.group(2)));
        
        // IP or Regname
        builder.add(Strings.nullToEmpty(authorityMatcher.group(3)));
        
        // Port
        builder.add(Strings.nullToEmpty(authorityMatcher.group(5)));
        
        // Path
        builder.add(Strings.nullToEmpty(matcher.group(5)));
        
        // Query
        builder.add(Strings.nullToEmpty(matcher.group(7)));
        
        // Fragment
        builder.add(Strings.nullToEmpty(matcher.group(9)));
        
        return builder.build();
    }

    static List<String> parseAuthority(final CharSequence authority) {
        Preconditions.checkNotNull(authority);
        final ImmutableList.Builder<String> builder = ImmutableList.builder();
        final Matcher matcher = iAuthorityPattern.matcher(authority);
        Preconditions.checkArgument(matcher.matches());

        // Userinfo
        builder.add(Strings.nullToEmpty(matcher.group(2)));
        
        // Host
        builder.add(Strings.nullToEmpty(matcher.group(3)));
        
        // Port
        builder.add(Strings.nullToEmpty(matcher.group(5)));

        return builder.build();
    }
}
