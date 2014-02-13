package com.testify.ecfeed.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;

public class ParameterizedMethod extends FrameworkMethod {
	

	private Collection<List<PartitionNode>> fTestData;

	public ParameterizedMethod(Method method, Collection<TestCaseNode> testCases) {
		super(method);
		fTestData = new LinkedList<List<PartitionNode>>();
		for(TestCaseNode testCase : testCases){
			fTestData.add(testCase.getTestData());
		}
	}

	@Override
	public Object invokeExplosively(Object target, Object... parameters) throws Throwable{
		for(List<PartitionNode> testCase : fTestData){
			Object[] arguments = getParameters(testCase);
			try{
				super.invokeExplosively(target, arguments);
			}catch (Throwable e){
				String message = getName() + "(" + testCase + "): " + e.getMessage();
				throw new Throwable(message);
			}
		}
		return null;
	}

	protected Object[] getParameters(List<PartitionNode> testCase) {
		List<Object> parameters = new ArrayList<Object>();
		for(PartitionNode parameter : testCase){
			parameters.add(parameter.getValue());
		}
		return parameters.toArray();
	}
}
