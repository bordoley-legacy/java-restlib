package restlib.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import restlib.impl.ImmutableMapEntry;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.testing.NullPointerTester;

public final class FormTest {
	@Test
	public void testBuilder() {
		final String expected = "a=b&c=d&e=f&g=h&i=j&i=k";
		final Form test = 
				Form.builder()
					.put(ImmutableMapEntry.create("a", "b"))
					.put("c", "d")
					.putAll(ImmutableMultimap.of("e", "f"))
					.putAll("g", ImmutableList.<String> of("h"))
					.putAll("i", "j", "k")
					.build();
		
		assertEquals(expected, test.toString());
		
		try {
			Form.builder().put(ImmutableMapEntry.create("", ""));
			fail("Expected IllegalArgumentException");
		} catch (final IllegalArgumentException expectedException){}	
		
		try {
			Form.builder().put("", "");
			fail("Expected IllegalArgumentException");
		} catch (final IllegalArgumentException expectedException){}	
		
		try {
			Form.builder().putAll(ImmutableMultimap.of("", ""));
			fail("Expected IllegalArgumentException");
		} catch (final IllegalArgumentException expectedException){}	
		
		try {
			Form.builder().putAll("", ImmutableList.<String> of());
			fail("Expected IllegalArgumentException");
		} catch (final IllegalArgumentException expectedException){}	
		
		try {
			Form.builder().putAll("", "a", "b", "c");
			fail("Expected IllegalArgumentException");
		} catch (final IllegalArgumentException expectedException){}	
	}
	
    @Test
    public void testNulls() {
        new NullPointerTester()
                .testAllPublicInstanceMethods(Form.builder());
        new NullPointerTester()
           		.testAllPublicInstanceMethods(Form.of());
        new NullPointerTester()
                .setDefault(String.class, "test")
        		.testAllPublicStaticMethods(Form.class);
    }
    
    @Test
    public void testParse() {
    	final String test = 
    			Joiner.on('&').join(
    					ImmutableList.of(
	                        "abc=ABC",
	                        "%F0%90%80%80=%F0%90%8F%BF",
	                        "%F4%8F%B0%80=%F4%8F%BF%BF",
	                        "1%F4%8F%B0%80=%40%F4%8F%B0%80",
	                        "%F4%8F%B0%801=%F4%8F%B0%80%40",
	                        "%C4%81%F4%8F%B0%80=%F4%8F%B0%80%C4%81",
	                        "Hello+%2B%25-_.%21%7E*%27%28%29%40%C2%AE%C4%81%E1%82%A0="));
    	final Form expected = Form.builder()
                .put("abc", "ABC")
                .put("\uD800\uDC00", "\uD800\uDFFF")
                .put("\uDBFF\uDC00", "\uDBFF\uDFFF")
                .put("1\uDBFF\uDC00", "@\uDBFF\uDC00")
                .put("\uDBFF\uDC001", "\uDBFF\uDC00@")
                .put("\u0101\uDBFF\uDC00", "\uDBFF\uDC00\u0101")
                .put("Hello +%-_.!~*'()@\u00ae\u0101\u10a0", "")
                .build();
    	
    	assertEquals(expected, Form.parse(test));
    	assertEquals(test, Form.parse(test).toString());
    	
    	try {
    		Form.parse("a=b&=c");
    		fail("Expected IllegalArgumentException");
    	} catch (final IllegalArgumentException expectedException){}
    	
    }    
}
