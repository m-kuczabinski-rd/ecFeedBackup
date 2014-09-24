package com.testify.ecfeed.abstraction.operations;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;

public class MethodOperationRemoveTestCase extends AbstractModelOperation {

	private MethodNode fTarget;
	private TestCaseNode fTestCase;
	private int fIndex;

	public MethodOperationRemoveTestCase(MethodNode target, TestCaseNode testCase) {
		super(OperationNames.REMOVE_TEST_CASE);
		fTarget = target;
		fTestCase = testCase;
		fIndex = testCase.getIndex();
	}

	@Override
	public void execute() throws ModelIfException {
		fIndex = fTestCase.getIndex();
		fTarget.removeTestCase(fTestCase);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationAddTestCase(fTarget, fTestCase, fIndex);
	}

}
