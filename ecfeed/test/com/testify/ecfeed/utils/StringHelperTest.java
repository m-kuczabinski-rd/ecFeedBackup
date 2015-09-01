/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 ******************************************************************************/

package com.testify.ecfeed.utils;

import com.testify.ecfeed.utils.StringHelper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringHelperTest{

	@Test
	public void shouldReportNonempty(){
		boolean result = StringHelper.isNullOrEmpty("123");
		assertEquals(result, false);
	}

	@Test
	public void shouldReportEmptyWhenNull(){
		boolean result = StringHelper.isNullOrEmpty(null);
		assertEquals(result, true);
	}	

	@Test
	public void shouldReportEmptyWhenEmpty(){
		boolean result = StringHelper.isNullOrEmpty(new String());
		assertEquals(result, true);
	}

	@Test
	public void shouldRemovePrefix(){
		String result = StringHelper.removePrefix("123", "123abc");
		assertEquals(result, "abc");
	}

	@Test
	public void shouldReturnArgWhenNoPrefix(){
		String result = StringHelper.removePrefix("123", "abc");
		assertEquals(result, "abc");
	}	

	@Test
	public void shouldIgnoreEmptyPrefix(){
		String result = StringHelper.removePrefix("", "abcd");
		assertEquals(result, "abcd");
	}	

	@Test
	public void shouldRemovePostfix(){
		String result = StringHelper.removePostfix("123", "abc123");
		assertEquals(result, "abc");
	}

	@Test
	public void shouldReturnResultWhenPostfixNotFound(){
		String result = StringHelper.removePostfix("123", "abcd");
		assertEquals(result, "abcd");
	}	

	@Test
	public void shouldIgnoreEmptyPostfix(){
		String result = StringHelper.removePostfix("", "abcd");
		assertEquals(result, "abcd");
	}	

	@Test
	public void shouldReturnEmptWhenEmptyString(){
		String result = StringHelper.removePostfix("abcd", "");
		assertEquals(result, "");
	}	

	@Test
	public void shouldGetLastToken(){
		String result = StringHelper.getLastToken("1#2#3", "#");
		assertEquals(result, "3");
	}

	@Test
	public void shouldReturnNullWhenNoLastToken(){
		String result = StringHelper.getLastToken("123", "#");
		assertEquals(result, null);
	}

	@Test
	public void shouldGetAllBeforeLastToken(){
		String result = StringHelper.getAllBeforeLastToken("1#2#3", "#");
		assertEquals(result, "1#2");
	}

	@Test
	public void shouldReturnNullWhenTokenSeparatorNotFound(){
		String result = StringHelper.getAllBeforeLastToken("123", "#");
		assertEquals(result, null);
	}	
}
