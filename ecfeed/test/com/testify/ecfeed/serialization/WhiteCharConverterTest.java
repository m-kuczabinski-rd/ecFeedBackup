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
}
