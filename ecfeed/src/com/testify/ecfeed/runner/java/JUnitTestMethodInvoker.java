package com.testify.ecfeed.runner.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.testify.ecfeed.runner.ITestMethodInvoker;
import com.testify.ecfeed.runner.Messages;
import com.testify.ecfeed.runner.RunnerException;

public class JUnitTestMethodInvoker implements ITestMethodInvoker {

	@Override
	public void invoke(
			Method fTestMethod, 
			Object instance, 
			Object[] arguments, 
			String argumentsDescription
			) throws RunnerException {
		try {
			fTestMethod.invoke(instance, arguments);
		} catch (InvocationTargetException e) {
			throw new RunnerException(
					fTestMethod.getName() + " " + argumentsDescription + ": " + e.getTargetException().toString());
		} catch (IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
			throw new RunnerException(
					Messages.CANNOT_INVOKE_TEST_METHOD(fTestMethod.getName(), argumentsDescription, e.getMessage()));
		}
	}
}
