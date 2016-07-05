/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.utils;

import com.ecfeed.core.utils.StringHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringHelperTest{

	@Test
	public void shouldReportNonempty(){
		boolean result = StringHelper.isNullOrEmpty("123");
		assertEquals(false, result);
	}

	@Test
	public void shouldReportEmptyWhenNull(){
		boolean result = StringHelper.isNullOrEmpty(null);
		assertEquals(true, result);
	}	

	@Test
	public void shouldReportEmptyWhenEmpty(){
		boolean result = StringHelper.isNullOrEmpty(new String());
		assertEquals(true, result);
	}

	@Test
	public void shouldRemovePrefix(){
		String result = StringHelper.removePrefix("123", "123abc");
		assertEquals("abc", result);
	}

	@Test
	public void shouldReturnArgWhenNoPrefix(){
		String result = StringHelper.removePrefix("123", "abc");
		assertEquals("abc", result);
	}	

	@Test
	public void shouldIgnoreEmptyPrefix(){
		String result = StringHelper.removePrefix("", "abcd");
		assertEquals("abcd", result);
	}	

	@Test
	public void shouldRemoveFromPostfix(){
		String result = StringHelper.removeFromPostfix("123", "abc123");
		assertEquals("abc", result);
	}

	@Test
	public void shouldIgnoreRemoveFromPostfix(){
		String result = StringHelper.removeFromPostfix("123", "abc123XYZ");
		assertEquals("abc", result);
	}

	@Test
	public void shouldRemoveAtEnd(){
		String result = StringHelper.removeFromPostfix("123", "abc123");
		assertEquals("abc", result);
	}

	@Test
	public void shouldRemoveAtEnd2(){
		String result = StringHelper.removeStrgAtEnd("123", "abc123X");
		assertEquals("abc123X", result);
	}	

	@Test
	public void shouldRemoveNewlineAtEnd() {
		String result = StringHelper.removeNewlineAtEnd("abc" + StringHelper.newLine());
		assertEquals("abc", result);
	}

	@Test
	public void shouldIgnoreNewlineAtEnd() {
		String initialStrg = "abc" + StringHelper.newLine() + "def";
		String result = StringHelper.removeNewlineAtEnd(initialStrg);
		assertEquals(initialStrg, result);
	}	

	@Test
	public void shouldReturnResultWhenPostfixNotFound(){
		String result = StringHelper.removeFromPostfix("123", "abcd");
		assertEquals("abcd", result);
	}	

	@Test
	public void shouldIgnoreEmptyPostfix(){
		String result = StringHelper.removeFromPostfix("", "abcd");
		assertEquals("abcd", result);
	}	

	@Test
	public void shouldReturnEmptWhenEmptyString(){
		String result = StringHelper.removeFromPostfix("abcd", "");
		assertEquals("", result);
	}	

	@Test
	public void shouldGetLastToken(){
		String result = StringHelper.getLastToken("1#2#3", "#");
		assertEquals("3", result);
	}

	@Test
	public void shouldReturnNullWhenNoLastToken(){
		String result = StringHelper.getLastToken("123", "#");
		assertEquals(null, result);
	}

	@Test
	public void shouldGetAllBeforeLastToken(){
		String result = StringHelper.getAllBeforeLastToken("1#2#3", "#");
		assertEquals("1#2", result);
	}

	@Test
	public void shouldReturnNullWhenTokenSeparatorNotFound(){
		String result = StringHelper.getAllBeforeLastToken("123", "#");
		assertEquals(null, result);
	}	

	@Test
	public void shouldReturnNullWhenAllCharsAreAllowed(){
		String result = StringHelper.containsOnlyAllowedChars("1234567890_.", "[0-9_\\.]");
		assertNull(result);
	}

	@Test
	public void shouldReturnHash(){
		String result = StringHelper.containsOnlyAllowedChars("#456", "[0-9]");
		assertNotNull(result);
		assertEquals("#", result);
	}

	@Test
	public void shouldReturnFalseWhenNotAllCharsAreAllowed2(){
		String result = StringHelper.containsOnlyAllowedChars("M T R.F%", "[A-Z\\. ]");
		assertNotNull(result);
		assertEquals("%", result);
	}

	@Test
	public void shouldReturnTrueWhenNull(){
		assertTrue(StringHelper.isNullOrBlank(null));
	}

	@Test
	public void shouldReturnTrueWhenBlank(){
		assertTrue(StringHelper.isNullOrBlank("   "));
	}	

	@Test
	public void shouldReturnFalseWhenNotBlank(){
		assertFalse(StringHelper.isNullOrBlank("abc DEF "));
	}

	@Test
	public void shouldReturnTrueWhenNonBlankContents(){
		assertTrue(StringHelper.hasNonBlankContents(" X "));
	}	

	@Test
	public void shouldReturnFalseWhenBlankContentsSpacesOnly(){
		assertFalse(StringHelper.hasNonBlankContents("    "));
	}	

	@Test
	public void shouldReturnFalseWhenBlankContentsNull(){
		assertFalse(StringHelper.hasNonBlankContents(null));
	}

	@Test
	public void shouldReturnNoOccurencesWhenEmpty(){
		assertEquals(0, StringHelper.countOccurencesOfChar("", 'x'));
	}

	@Test
	public void shouldReturnNoOccurencesOfSpace(){
		assertEquals(0, StringHelper.countOccurencesOfChar("12345", ' '));
	}	

	@Test
	public void shouldReturnFourOccurencesOfSpace(){
		assertEquals(4, StringHelper.countOccurencesOfChar(" 12 3 45 ", ' '));
	}

}
