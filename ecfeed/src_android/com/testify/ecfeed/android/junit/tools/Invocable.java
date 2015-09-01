/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.android.junit.tools;

import java.lang.reflect.Method;

public class Invocable {

	private Object fObject;
	private Method fMethod;
	private Object[] fArguments;

	public Invocable(Object object, Method method, Object[] arguments) {
		fObject = object;
		fMethod = method;
		fArguments = arguments;
	}

	public Object getObject() {
		return fObject;
	}

	public Method getMethod() {
		return fMethod;
	}

	public Object[] getArguments() {
		return fArguments;
	}
}
