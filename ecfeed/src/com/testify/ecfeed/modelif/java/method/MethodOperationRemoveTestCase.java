package com.testify.ecfeed.modelif.java.method;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class MethodOperationRemoveTestCase implements IModelOperation {

	private MethodNode fTarget;
	private TestCaseNode fTestCase;
	private int fIndex;

	public MethodOperationRemoveTestCase(MethodNode target, TestCaseNode testCase) {
		fTarget = target;
		fTestCase = testCase;
		fIndex = testCase.getIndex();
	}

	@Override
	public void execute() throws ModelIfException {
		fTarget.removeTestCase(fTestCase);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationAddTestCase(fTarget, fTestCase, fIndex);
	}

}
