package com.testify.ecfeed.runner;

import java.lang.reflect.Method;
import java.util.List;

import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.MethodNode;

public interface ITestMethodInvoker {
	void invoke(Method fTestMethod, 
			Object instance, 
			Object[] arguments, 
			MethodNode fTarget, 
			List<ChoiceNode> testData) throws RunnerException;
}
