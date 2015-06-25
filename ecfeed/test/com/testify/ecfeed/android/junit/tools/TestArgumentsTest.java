/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 ******************************************************************************/

package com.testify.ecfeed.android.junit.tools;

import com.testify.ecfeed.android.junit.tools.TestArguments;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestArgumentsTest{
	
	@Test
	public void shouldReturnTheSameArg(){
		final String arg = "arg";
		TestArguments.set(arg);
		assertEquals(arg, TestArguments.get());
	}
	
}
