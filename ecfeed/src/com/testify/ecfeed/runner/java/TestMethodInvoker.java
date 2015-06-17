package com.testify.ecfeed.runner.java;

import java.lang.reflect.Method;
import java.util.List;

import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.runner.RunnerException;

public interface TestMethodInvoker {
	void invoke(Method fTestMethod, 
			Object instance, 
			Object[] arguments, 
			MethodNode fTarget, 
			List<ChoiceNode> testData) throws RunnerException;
}
