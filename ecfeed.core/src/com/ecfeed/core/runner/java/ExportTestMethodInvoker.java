package com.ecfeed.core.runner.java;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.testify.ecfeed.core.model.ChoiceNode;
import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.model.MethodParameterNode;
import com.testify.ecfeed.core.model.TestCaseNode;
import com.testify.ecfeed.core.runner.ITestMethodInvoker;

public class ExportTestMethodInvoker implements ITestMethodInvoker {

	MethodNode fMethodNode;
	List<MethodParameterNode> fMethodParameters;
	ArrayList<TestCaseNode> fTestCaseNodes;

	@Override
	public boolean isRemote() {
		return false;
	}

	public ExportTestMethodInvoker(MethodNode methodNode) {
		fMethodNode = methodNode;
		fTestCaseNodes = new ArrayList<TestCaseNode>();
		fMethodParameters = fMethodNode.getMethodParameters();
	}

	@Override
	public void invoke(
			Method testMethod, 
			String className, 
			Object instance,
			Object[] arguments, 
			String argumentsDescription) throws RuntimeException {

		fTestCaseNodes.add(createTestCase(arguments));
	}

	TestCaseNode createTestCase(Object[] arguments) {
		List<ChoiceNode> choiceNodes = new ArrayList<ChoiceNode>();

		for (int cnt = 0; cnt < fMethodNode.getParametersCount(); ++cnt) {
			MethodParameterNode methodParameterNode = fMethodParameters.get(cnt);
			choiceNodes.add(new ChoiceNode(methodParameterNode.getName(), arguments[cnt].toString()));
		}

		TestCaseNode testCaseNode = new TestCaseNode("name", choiceNodes); 
		testCaseNode.setParent(fMethodNode);

		return testCaseNode;
	}

	public Collection<TestCaseNode> getTestCasesToExport() {
		return fTestCaseNodes; 
	}

}
