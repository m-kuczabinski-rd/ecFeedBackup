package com.testify.ecfeed.android.junit.tools;

import java.lang.reflect.Method;

public class Invocable {

	private Object fObject;
	private Method fMethod;
	private Object[] fArguments;

	Invocable(Object object, Method method, Object[] arguments) {
		fObject = object;
		fMethod = method;
		fArguments = arguments;
	}

	Object getObject() {
		return fObject;
	}

	Method getMethod() {
		return fMethod;
	}

	Object[] getArguments() {
		return fArguments;
	}
}
