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
