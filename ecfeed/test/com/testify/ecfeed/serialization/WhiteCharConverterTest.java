package com.testify.ecfeed.serialization;

import static org.junit.Assert.*;

import org.junit.Test;

public class WhiteCharConverterTest {

	@Test
	public void encodeEmptyTest(){
		assertEquals("", WhiteCharConverter.encode(""));
	}
	
	@Test
	public void decodeEmptyTest(){
		assertEquals("", WhiteCharConverter.decode(""));
	}	
	
	@Test
	public void encodeSingleSpaceTest(){
		// encode: space -> \s
		String result = WhiteCharConverter.encode(" ");
		assertEquals(2, result.length());
		assertEquals("\\s", result);
	}
	
	@Test
	public void decodeSingleSpaceTest(){
		// decode: \s -> space
		assertEquals(" ", WhiteCharConverter.decode("\\s"));
	}
	
	@Test
	public void encodeMultipleSpacesTest(){
		assertEquals("\\s\\sA\\sB\\s\\s\\sC\\s\\s", WhiteCharConverter.encode("  A B   C  "));
	}
	
	@Test
	public void decodeMultipleSpacesTest(){
		assertEquals("  A B   C  ", WhiteCharConverter.decode("\\s\\sA\\sB\\s\\s\\sC\\s\\s"));
	}	
	
	@Test
	public void encodeSequenceWithSTest(){
		// encode: \s -> \\s
		assertEquals("\\\\s", WhiteCharConverter.encode("\\s"));
	}	
	
	@Test
	public void decodeSequenceWithSTest(){
		// decode: \\s -> \s
		assertEquals("\\s", WhiteCharConverter.decode("\\\\s"));
	}	
	
	public void encodeSequenceWith2BackslashesAndSTest(){
		// encode: \\s -> \\\\s
		assertEquals("\\\\\\\\s", WhiteCharConverter.encode("\\\\s"));
	}	
	
	@Test
	public void decodeSequenceWith2BackslashesAndSTest(){
		// decode: \\\\s -> \\s
		assertEquals("\\\\s", WhiteCharConverter.decode("\\\\\\\\s"));
	}
	
	@Test
	public void encodeSequenceWithBackslashAndSpaceTest(){
		// encode: \_  -> \\\s
		assertEquals("\\\\\\s", WhiteCharConverter.encode("\\ "));
	}	
	
	@Test
	public void decodeSequenceWithBackslashAndSpaceTest(){
		// decode: \\\s -> \_
		assertEquals("\\ ", WhiteCharConverter.decode("\\\\\\s"));
	}	
	
	@Test
	public void encodeNewlineTest(){
		// encode: \n (newline) -> \n (string of two characters)
		String result = WhiteCharConverter.encode("\n");
		assertEquals(2, result.length());
		assertEquals("\\n", result);
	}	
	
	@Test
	public void decodeNewlineTest(){
		// encode: \n (string of two characters) -> \n (newline)
		assertEquals("\n", WhiteCharConverter.decode("\\n"));
	}	
	
	@Test
	public void encodeTextInTwoLinesTest(){
		assertEquals("abc\\ndef", WhiteCharConverter.encode("abc\ndef"));
	}	
	
	@Test
	public void decodeTextInTwoLinesTest(){
		assertEquals("abc\ndef", WhiteCharConverter.decode("abc\\ndef"));
	}
	
	@Test
	public void encodeTextWithSeparatedLinesTest(){
		assertEquals("abc\\ndef", WhiteCharConverter.encode("abc\ndef"));
	}	
	
	@Test
	public void decodeTextWithSeparatedLinesTest(){
		assertEquals("abc\n\n\ndef", WhiteCharConverter.decode("abc\\n\\n\\ndef"));
	}
	
	@Test
	public void encodeTextWithSpacesAndNewlinesTest(){
		assertEquals("\\s\\n\\s", WhiteCharConverter.encode(" \n "));
	}	
	
	@Test
	public void decodeTextWithSpacesAndNewlinesTest(){
		assertEquals(" \n ", WhiteCharConverter.decode("\\s\\n\\s"));
	}
	
	@Test
	public void encodeTabTest(){
		assertEquals("\\t", WhiteCharConverter.encode("\t"));
	}	
	
	@Test
	public void decodeTabTest(){
		assertEquals("\t", WhiteCharConverter.decode("\\t"));
	}
	
	@Test
	public void encodeMultipleTabsTest(){
		assertEquals("\\t\\t\\t", WhiteCharConverter.encode("\t\t\t"));
	}	
	
	@Test
	public void decodeMultipleTabsTest(){
		assertEquals("\t\t\t", WhiteCharConverter.decode("\\t\\t\\t"));
	}
	
	
	@Test
	public void encodeMixedSequenceTest(){
		assertEquals("\\s\\n\\t\\s\\t\\n\\s", WhiteCharConverter.encode(" \n\t \t\n "));
	}	
	
	@Test
	public void decodeMixedSequenceTest(){
		assertEquals(" \n\t \t\n ", WhiteCharConverter.decode("\\s\\n\\t\\s\\t\\n\\s"));
	}	
}
