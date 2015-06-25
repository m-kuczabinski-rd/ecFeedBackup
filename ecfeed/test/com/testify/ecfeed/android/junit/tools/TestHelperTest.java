/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 ******************************************************************************/

package com.testify.ecfeed.android.junit.tools;

import com.testify.ecfeed.android.junit.tools.TestHelper;
import com.testify.ecfeed.android.junit.tools.ILogger;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

class ClassUnderTestStub {
	void exampleMethod(int arg) {
	}
}

class LogerStub implements ILogger {

	@Override
	public void log(String message) {
	}
}

public class TestHelperTest{

	@Test
	public void shouldGetClassUnderTest(){
		final String className = ClassUnderTestStub.class.getName();

		final String testArguments = className + ", exampleMethod, int[0]";
		TestHelper.prepareTestArguments(testArguments);
		
		Class<?> theClass = TestHelper.getClassUnderTest(new LogerStub());
		assertEquals(ClassUnderTestStub.class, theClass);
	}
}
