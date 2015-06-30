package com.testify.ecfeed.android.junit.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodInvoker implements IMethodInvoker {

	private ILogger fLogger = null;

	MethodInvoker(ILogger logger) {
		fLogger = logger;
	}

	public void invokeMethod(Invocable invokee) {

		Method method = invokee.getMethod();
		Object object = invokee.getObject();
		Object[] arguments = invokee.getArguments();

		try {
			method.invoke(object, arguments);
		} catch (RuntimeException | IllegalAccessException | InvocationTargetException exception) {
			handleException(exception, object, method, arguments);
		}
	}

	private void handleException(Exception exception, Object object, Method method, Object[] arguments) {
		Throwable internalException = exception.getCause();

		if (internalException instanceof junit.framework.AssertionFailedError)
		{
			String description 
			= createInvokeErrorDescription(internalException.getMessage(), object, method, arguments);

			fLogger.log("ASSERTION FAILED: " + description);
			throw new junit.framework.AssertionFailedError(description);
		}

		logAndThrow(createInvokeErrorDescription(exception.getCause().getMessage(), object, method, arguments));
	}

	private void logAndThrow(String message) {
		fLogger.log(message);
		throw new RuntimeException(message);
	}

	private String createInvokeErrorDescription(
			String internalExceptionMessage, 
			Object object, 
			Method method, 
			Object[] arguments) {
		return internalExceptionMessage
				+ "\n"
				+ "\t class: " + object.getClass().getName() + "\n"
				+ "\t method: " + method.getName() + "\n"
				+ "\t arguments:"
				+ createArgumentsDescription(arguments); 
	}

	private String createArgumentsDescription(Object[] arguments) {
		String result = "";

		for (Object argument : arguments) {
			result = result + "\n\t  <" + argument.toString() + ">";
		}

		return result;
	}
}
