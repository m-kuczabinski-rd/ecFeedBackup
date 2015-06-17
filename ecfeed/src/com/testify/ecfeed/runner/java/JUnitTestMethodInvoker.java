package com.testify.ecfeed.runner.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.runner.Messages;
import com.testify.ecfeed.runner.RunnerException;

public class JUnitTestMethodInvoker implements TestMethodInvoker {

	@Override
	public void invoke(Method fTestMethod, 
			Object instance, 
			Object[] arguments, 
			MethodNode fTarget, 
			List<ChoiceNode> testData) throws RunnerException {
		try {
			fTestMethod.invoke(instance, arguments);
		} catch (InvocationTargetException e) {
			throw new RunnerException(fTarget.getName() + testData.toString() + ": " + e.getTargetException().toString());
		} catch (IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
			throw new RunnerException(Messages.CANNOT_INVOKE_TEST_METHOD(fTarget.toString(), testData.toString(), e.getMessage()));
		}
	}
}
