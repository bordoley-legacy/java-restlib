package restlib.impl;

import org.junit.Test;

import com.google.common.base.CharMatcher;
import com.google.common.testing.NullPointerTester;

public final class ParsersTest {
    @Test
    public void testNulls() {
        final NullPointerTester tester = new NullPointerTester();
        tester.testAllPublicStaticMethods(Parsers.class);
        tester.testAllPublicInstanceMethods(Parsers.INTEGER_PARSER);
        tester.testAllPublicInstanceMethods(Parsers.LONG_PARSER);
        tester.testAllPublicInstanceMethods(Parsers.charParser('a'));
        tester.testAllPublicInstanceMethods(
                Parsers.firstAvailableParser(Parsers.INTEGER_PARSER, Parsers.LONG_PARSER));
        tester.testAllPublicInstanceMethods(
                Parsers.listParser(
                        Parsers.INTEGER_PARSER, Parsers.LONG_PARSER, Integer.class));
        tester.testAllPublicInstanceMethods(Parsers.stringParser(""));
        tester.testAllPublicInstanceMethods(
                Parsers.untypedListParser(
                        Parsers.INTEGER_PARSER, Parsers.LONG_PARSER, Integer.class));
        tester.testAllPublicInstanceMethods(Parsers.whileMatchesParser(CharMatcher.ANY));
    }
}
