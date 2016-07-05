/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.core.serialization;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ecfeed.core.serialization.WhiteCharConverter;

public class WhiteCharConverterTest {

	WhiteCharConverter fWhiteCharConverter = new WhiteCharConverter();
	
	@Test
	public void encodeNullTest(){
		assertEquals(null, fWhiteCharConverter.encode(null));
	}
	
	@Test
	public void decodeNullTest(){
		assertEquals(null, fWhiteCharConverter.decode(null));
	}	
	
	@Test
	public void encodeEmptyTest(){
		assertEquals("", fWhiteCharConverter.encode(""));
	}
	
	@Test
	public void decodeEmptyTest(){
		assertEquals("", fWhiteCharConverter.decode(""));
	}	
	
	@Test
	public void encodeSingleSpaceTest(){
		// encode: space -> \s
		String result = fWhiteCharConverter.encode(" ");
		assertEquals(2, result.length());
		assertEquals("\\s", result);
	}
	
	@Test
	public void decodeSingleSpaceTest(){
		// decode: \s -> space
		assertEquals(" ", fWhiteCharConverter.decode("\\s"));
	}
	
	@Test
	public void encodeMultipleSpacesTest(){
		assertEquals("\\s\\sA\\sB\\s\\s\\sC\\s\\s", fWhiteCharConverter.encode("  A B   C  "));
	}
	
	@Test
	public void decodeMultipleSpacesTest(){
		assertEquals("  A B   C  ", fWhiteCharConverter.decode("\\s\\sA\\sB\\s\\s\\sC\\s\\s"));
	}	
	
	@Test
	public void encodeSequenceWithSTest(){
		// encode: \s -> \\s
		assertEquals("\\\\s", fWhiteCharConverter.encode("\\s"));
	}	
	
	@Test
	public void decodeSequenceWithSTest(){
		// decode: \\s -> \s
		assertEquals("\\s", fWhiteCharConverter.decode("\\\\s"));
	}	
	
	public void encodeSequenceWith2BackslashesAndSTest(){
		// encode: \\s -> \\\\s
		assertEquals("\\\\\\\\s", fWhiteCharConverter.encode("\\\\s"));
	}	
	
	@Test
	public void decodeSequenceWith2BackslashesAndSTest(){
		// decode: \\\\s -> \\s
		assertEquals("\\\\s", fWhiteCharConverter.decode("\\\\\\\\s"));
	}
	
	@Test
	public void encodeSequenceWithBackslashAndSpaceTest(){
		// encode: \_  -> \\\s
		assertEquals("\\\\\\s", fWhiteCharConverter.encode("\\ "));
	}	
	
	@Test
	public void decodeSequenceWithBackslashAndSpaceTest(){
		// decode: \\\s -> \_
		assertEquals("\\ ", fWhiteCharConverter.decode("\\\\\\s"));
	}	
	
	@Test
	public void encodeNewlineTest(){
		// encode: \n (newline) -> \n (string of two characters)
		String result = fWhiteCharConverter.encode("\n");
		assertEquals(2, result.length());
		assertEquals("\\n", result);
	}	
	
	@Test
	public void decodeNewlineTest(){
		// encode: \n (string of two characters) -> \n (newline)
		assertEquals("\n", fWhiteCharConverter.decode("\\n"));
	}	
	
	@Test
	public void encodeTextInTwoLinesTest(){
		assertEquals("abc\\ndef", fWhiteCharConverter.encode("abc\ndef"));
	}	
	
	@Test
	public void decodeTextInTwoLinesTest(){
		assertEquals("abc\ndef", fWhiteCharConverter.decode("abc\\ndef"));
	}
	
	@Test
	public void encodeTextWithSeparatedLinesTest(){
		assertEquals("abc\\ndef", fWhiteCharConverter.encode("abc\ndef"));
	}	
	
	@Test
	public void decodeTextWithSeparatedLinesTest(){
		assertEquals("abc\n\n\ndef", fWhiteCharConverter.decode("abc\\n\\n\\ndef"));
	}
	
	@Test
	public void encodeTextWithSpacesAndNewlinesTest(){
		assertEquals("\\s\\n\\s", fWhiteCharConverter.encode(" \n "));
	}	
	
	@Test
	public void decodeTextWithSpacesAndNewlinesTest(){
		assertEquals(" \n ", fWhiteCharConverter.decode("\\s\\n\\s"));
	}
	
	@Test
	public void encodeTabTest(){
		assertEquals("\\t", fWhiteCharConverter.encode("\t"));
	}	
	
	@Test
	public void decodeTabTest(){
		assertEquals("\t", fWhiteCharConverter.decode("\\t"));
	}
	
	@Test
	public void encodeMultipleTabsTest(){
		assertEquals("\\t\\t\\t", fWhiteCharConverter.encode("\t\t\t"));
	}	
	
	@Test
	public void decodeMultipleTabsTest(){
		assertEquals("\t\t\t", fWhiteCharConverter.decode("\\t\\t\\t"));
	}
	
	
	@Test
	public void encodeMixedSequenceTest(){
		assertEquals("\\s\\n\\t\\s\\t\\n\\s", fWhiteCharConverter.encode(" \n\t \t\n "));
	}	
	
	@Test
	public void decodeMixedSequenceTest(){
		assertEquals(" \n\t \t\n ", fWhiteCharConverter.decode("\\s\\n\\t\\s\\t\\n\\s"));
	}	
}
