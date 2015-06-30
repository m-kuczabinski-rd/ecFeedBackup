/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 ******************************************************************************/

package com.testify.ecfeed.android.junit.tools;

import java.lang.reflect.Method;

import com.testify.ecfeed.android.junit.tools.Invocable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

class StubClass {
	public void stubMethod(int arg1, double arg2, String arg3) {
	}
}

public class InvocableTest{

	@Test
	public void shouldReturnArgsFromConstructor(){

		Object object = new StubClass();

		Method method = null;
		try {
			method = object.getClass().getMethod("stubMethod", int.class, double.class, String.class);
		} catch (NoSuchMethodException | SecurityException e) {
			fail();
		}

		Object[] arguments = new Object[3];
		int a1 = 5;
		arguments[0] = a1;
		double a2 = 2.0;
		arguments[1] = a2;
		String a3 = "abc";
		arguments[2] = a3;

		Invocable invocable = new Invocable(object, method, arguments);

		assertEquals(object, invocable.getObject());
		assertEquals(method, invocable.getMethod());

		Object[] resultArguments = invocable.getArguments(); 
		assertEquals(arguments[0], resultArguments[0]);
		assertEquals(arguments[1], resultArguments[1]);
		assertEquals(arguments[2], resultArguments[2]);
	}
}
