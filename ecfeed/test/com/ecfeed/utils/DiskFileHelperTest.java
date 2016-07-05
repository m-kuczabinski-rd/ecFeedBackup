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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.ecfeed.core.utils.DiskFileHelper;

public class DiskFileHelperTest{

	@Test
	public void shouldReturnNullWhenFileNameIsValid(){
		String result = DiskFileHelper.checkEctFileName("abcdEFGH1234567890_.ect");
		assertNull(result);
	}

	@Test
	public void shouldReturnNotNullWhenNoEctExtension(){
		String result = DiskFileHelper.checkEctFileName("abc");
		assertNotNull(result);
	}

	@Test
	public void shouldReturnNotNullWhenNameBeginsWithSpace(){
		String result = DiskFileHelper.checkEctFileName(" abc.ect");
		assertNotNull(result);
	}

	@Test
	public void shouldReturnNotNullWhenNameContainsSpace(){
		String result = DiskFileHelper.checkEctFileName("a bc.ect");
		assertNotNull(result);
	}	

	@Test
	public void shouldReturnNotNullWhenNameContainsInvalidChar(){
		String result = DiskFileHelper.checkEctFileName("abc$.ect");
		assertNotNull(result);
	}

	@Test
	public void shouldCreateFileName() {
		String result = DiskFileHelper.createFileName("abc", "txt");
		assertEquals("abc.txt", result);
	}

	@Test
	public void shouldJoinPathWithFile1() {
		String result = DiskFileHelper.joinPathWithFile("c:", "file.txt");
		String expectedResult = "c:" + DiskFileHelper.pathSeparator() + "file.txt";
		assertEquals(expectedResult, result);
	}

	@Test
	public void shouldJoinPathWithFile2() {
		String result = DiskFileHelper.joinPathWithFile("c:" + DiskFileHelper.pathSeparator(), "file.txt");
		String expectedResult = "c:" + DiskFileHelper.pathSeparator() + "file.txt";
		assertEquals(expectedResult, result);
	}

	@Test
	public void shouldJoinSubdirectory() {
		String result = DiskFileHelper.joinPathWithFile("c:", "subdir");
		String expectedResult = "c:" + DiskFileHelper.pathSeparator() + "subdir";
		assertEquals(expectedResult, result);
	}

	@Test
	public void shouldExtractFileName() {
		String result = DiskFileHelper.extractFileName("c:" + DiskFileHelper.pathSeparator() + "file.txt");
		String expectedResult = "file.txt";
		assertEquals(expectedResult, result);
	}

	@Test
	public void shouldExtractPathWithSeparator() {
		String result = DiskFileHelper.extractPathWithSeparator("c:" + DiskFileHelper.pathSeparator() + "file.txt");
		String expectedResult = "c:" + DiskFileHelper.pathSeparator();
		assertEquals(expectedResult, result);
	}

	@Test
	public void shouldExtractFileNameWithoutExtension() {
		String result = DiskFileHelper.extractFileNameWithoutExtension("file.txt");
		String expectedResult = "file";
		assertEquals(expectedResult, result);
	}

}
