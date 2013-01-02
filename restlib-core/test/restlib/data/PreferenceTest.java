package restlib.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.nio.CharBuffer;

import org.junit.Test;

import restlib.impl.Optionals;
import restlib.impl.Parser;
import restlib.impl.Parsers;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

public final class PreferenceTest {
    private static final class MockMatcheable implements Matcheable<MockMatcheable> {
        public static Parser<MockMatcheable> PARSER = new Parser<MockMatcheable>() {
            final Parser<String> TOKEN_PARSER = 
                    Parsers.whileMatchesParser(CharMatchers.TCHAR_MATCHER);
            @Override
            public Optional<MockMatcheable> parse(final CharBuffer buffer) {
                final Optional<String> token = TOKEN_PARSER.parse(buffer);
                if (Optionals.isAbsent(token)) {
                    return Optional.absent();
                }
                
                return Optional.of(create(token.get()));
            }          
        };
        
        public static MockMatcheable create(final String value) {
            Preconditions.checkNotNull(value);
            return new MockMatcheable(value);
        }

        private final String value;

        private MockMatcheable(final String value) {
            this.value = value;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            } else if (obj instanceof MockMatcheable) {
                final MockMatcheable that = (MockMatcheable) obj;
                return this.value.equals(that.value);
            } else {
                return false;
            }
        }
        
        @Override
        public int hashCode() {
            return this.value.hashCode();
        }
        
        @Override
        public int match(final MockMatcheable that) {
            Preconditions.checkNotNull(that);
            if (this.equals(that)) {
                return 1000;
            } 
            return 0;
        }
        
        @Override
        public String toString() {
            return this.value;
        }
    }
    
    private static final Parser<Preference<MockMatcheable>> PARSER = 
            Preference.parser(MockMatcheable.PARSER, MockMatcheable.class);
    
    private static void doTestParser$parse(final String test, final MockMatcheable value, final int qf) {
        final Preference<MockMatcheable> parsedValue = PARSER.parse(CharBuffer.wrap(test)).get();
        assertEquals(value, parsedValue.value());
        assertEquals(qf, parsedValue.qualityFactor());
    }
    
    private static void doTestParser$parse_withInvalid(final String test) {
        final Optional<Preference<MockMatcheable>> result = 
                PARSER.parse(CharBuffer.wrap(test));
        assertFalse(result.isPresent());
    }
    
    @Test
    public void testBestMatch() {
        final ImmutableSet<Preference<MockMatcheable>> preferred1 = 
                ImmutableSet.of(
                        Preference.create(MockMatcheable.create("test1"), 100),
                        Preference.create(MockMatcheable.create("test2"), 200),
                        Preference.create(MockMatcheable.create("test3"), 1000));
        final ImmutableSet<MockMatcheable> available =
                ImmutableSet.of(
                        MockMatcheable.create("test1"),
                        MockMatcheable.create("test4"),
                        MockMatcheable.create("test5"));
        assertEquals(
                MockMatcheable.create("test1"), 
                Preference.bestMatch(preferred1, available).get());
        
        final ImmutableSet<Preference<MockMatcheable>> preferred2 = 
                ImmutableSet.of(
                        Preference.create(MockMatcheable.create("test1"), 0),
                        Preference.create(MockMatcheable.create("test2"), 200),
                        Preference.create(MockMatcheable.create("test3"), 1000));
        assertFalse(
                Preference.bestMatch(preferred2, available).isPresent());
    }
    
    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(
                    Preference.create(MockMatcheable.create("test")),
                    Preference.create(MockMatcheable.create("test")))
            .addEqualityGroup(
                    Preference.create(MockMatcheable.create("test"), 100),
                    Preference.create(MockMatcheable.create("test"), 100))
            .addEqualityGroup(
                    Preference.create(MockMatcheable.create("test2"), 100),
                    Preference.create(MockMatcheable.create("test2"), 100)) 
            .addEqualityGroup(
                    Preference.create(MockMatcheable.create("test3"), 110),
                    Preference.create(MockMatcheable.create("test3"), 110))
            .addEqualityGroup(
                    Preference.create(MockMatcheable.create("test3"), 111),
                    Preference.create(MockMatcheable.create("test3"), 111))
            .addEqualityGroup(
                    Preference.create(MockMatcheable.create("test3"), 1000),
                    Preference.create(MockMatcheable.create("test3"), 1000))                     
            .testEquals();
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester() 
            .testAllPublicStaticMethods(Preference.class);
        new NullPointerTester()
            .testAllPublicInstanceMethods(Preference.create(MediaRanges.APPLICATION_ATOM));
    }
    
    @Test
    public void testParser$parse() {
        doTestParser$parse("test", MockMatcheable.create("test"), 1000);
        doTestParser$parse("test;q=0.5", MockMatcheable.create("test"), 500);
        doTestParser$parse("test;q=0.55", MockMatcheable.create("test"), 550);
        doTestParser$parse("test;q=0.555", MockMatcheable.create("test"), 555);
        
        doTestParser$parse_withInvalid("test;q=9.00");
        doTestParser$parse_withInvalid("test;q=0.a");
        doTestParser$parse_withInvalid("test;q=0.");
        doTestParser$parse_withInvalid("test;q=a");
        doTestParser$parse_withInvalid("test;q=");
        doTestParser$parse_withInvalid("test;q");
        doTestParser$parse_withInvalid("");

    }  
}
