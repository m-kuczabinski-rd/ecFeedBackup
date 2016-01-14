/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 ******************************************************************************/

package com.testify.ecfeed.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.testify.ecfeed.core.utils.DiskFileHelper;

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
}
