package com.testify.ecfeed.serialization;

import static org.junit.Assert.*;

import org.junit.Test;

public class WhiteCharConverterTest {

	WhiteCharConverter whiteCharConverter = new WhiteCharConverter();
	
	@Test
	public void encodeEmptyTest(){
		assertEquals("", whiteCharConverter.encode(""));
	}
	
	@Test
	public void decodeEmptyTest(){
		assertEquals("", whiteCharConverter.decode(""));
	}	
	
	@Test
	public void encodeSingleSpaceTest(){
		// encode: space -> \s
		String result = whiteCharConverter.encode(" ");
		assertEquals(2, result.length());
		assertEquals("\\s", result);
	}
	
	@Test
	public void decodeSingleSpaceTest(){
		// decode: \s -> space
		assertEquals(" ", whiteCharConverter.decode("\\s"));
	}
	
	@Test
	public void encodeMultipleSpacesTest(){
		assertEquals("\\s\\sA\\sB\\s\\s\\sC\\s\\s", whiteCharConverter.encode("  A B   C  "));
	}
	
	@Test
	public void decodeMultipleSpacesTest(){
		assertEquals("  A B   C  ", whiteCharConverter.decode("\\s\\sA\\sB\\s\\s\\sC\\s\\s"));
	}	
	
	@Test
	public void encodeSequenceWithSTest(){
		// encode: \s -> \\s
		assertEquals("\\\\s", whiteCharConverter.encode("\\s"));
	}	
	
	@Test
	public void decodeSequenceWithSTest(){
		// decode: \\s -> \s
		assertEquals("\\s", whiteCharConverter.decode("\\\\s"));
	}	
	
	public void encodeSequenceWith2BackslashesAndSTest(){
		// encode: \\s -> \\\\s
		assertEquals("\\\\\\\\s", whiteCharConverter.encode("\\\\s"));
	}	
	
	@Test
	public void decodeSequenceWith2BackslashesAndSTest(){
		// decode: \\\\s -> \\s
		assertEquals("\\\\s", whiteCharConverter.decode("\\\\\\\\s"));
	}
	
	@Test
	public void encodeSequenceWithBackslashAndSpaceTest(){
		// encode: \_  -> \\\s
		assertEquals("\\\\\\s", whiteCharConverter.encode("\\ "));
	}	
	
	@Test
	public void decodeSequenceWithBackslashAndSpaceTest(){
		// decode: \\\s -> \_
		assertEquals("\\ ", whiteCharConverter.decode("\\\\\\s"));
	}	
	
	@Test
	public void encodeNewlineTest(){
		// encode: \n (newline) -> \n (string of two characters)
		String result = whiteCharConverter.encode("\n");
		assertEquals(2, result.length());
		assertEquals("\\n", result);
	}	
	
	@Test
	public void decodeNewlineTest(){
		// encode: \n (string of two characters) -> \n (newline)
		assertEquals("\n", whiteCharConverter.decode("\\n"));
	}	
	
	@Test
	public void encodeTextInTwoLinesTest(){
		assertEquals("abc\\ndef", whiteCharConverter.encode("abc\ndef"));
	}	
	
	@Test
	public void decodeTextInTwoLinesTest(){
		assertEquals("abc\ndef", whiteCharConverter.decode("abc\\ndef"));
	}
	
	@Test
	public void encodeTextWithSeparatedLinesTest(){
		assertEquals("abc\\ndef", whiteCharConverter.encode("abc\ndef"));
	}	
	
	@Test
	public void decodeTextWithSeparatedLinesTest(){
		assertEquals("abc\n\n\ndef", whiteCharConverter.decode("abc\\n\\n\\ndef"));
	}
	
	@Test
	public void encodeTextWithSpacesAndNewlinesTest(){
		assertEquals("\\s\\n\\s", whiteCharConverter.encode(" \n "));
	}	
	
	@Test
	public void decodeTextWithSpacesAndNewlinesTest(){
		assertEquals(" \n ", whiteCharConverter.decode("\\s\\n\\s"));
	}
	
	@Test
	public void encodeTabTest(){
		assertEquals("\\t", whiteCharConverter.encode("\t"));
	}	
	
	@Test
	public void decodeTabTest(){
		assertEquals("\t", whiteCharConverter.decode("\\t"));
	}
	
	@Test
	public void encodeMultipleTabsTest(){
		assertEquals("\\t\\t\\t", whiteCharConverter.encode("\t\t\t"));
	}	
	
	@Test
	public void decodeMultipleTabsTest(){
		assertEquals("\t\t\t", whiteCharConverter.decode("\\t\\t\\t"));
	}
	
	
	@Test
	public void encodeMixedSequenceTest(){
		assertEquals("\\s\\n\\t\\s\\t\\n\\s", whiteCharConverter.encode(" \n\t \t\n "));
	}	
	
	@Test
	public void decodeMixedSequenceTest(){
		assertEquals(" \n\t \t\n ", whiteCharConverter.decode("\\s\\n\\t\\s\\t\\n\\s"));
	}	
}
