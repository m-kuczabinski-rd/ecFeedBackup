package com.ecfeed.core.runner.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.ecfeed.core.utils.ExceptionHelper;
import com.testify.ecfeed.core.runner.ITestMethodInvoker;
import com.testify.ecfeed.core.runner.Messages;

public class JUnitTestMethodInvoker implements ITestMethodInvoker {

	@Override
	public boolean isRemote() {
		return false;
	}

	@Override
	public void invoke(
			Method testMethod, 
			String className, 
			Object instance,
			Object[] arguments, 
			String argumentsDescription) throws RuntimeException {
		try {
			testMethod.invoke(instance, arguments);
		} catch (InvocationTargetException e) {
			String message = testMethod.getName() + " " + argumentsDescription + ": " + e.getTargetException().toString();
			ExceptionHelper.reportRuntimeException(message);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			String message = Messages.CANNOT_INVOKE_TEST_METHOD(testMethod.getName(), argumentsDescription, e.getMessage());
			ExceptionHelper.reportRuntimeException(message);			
		}
	}
}
