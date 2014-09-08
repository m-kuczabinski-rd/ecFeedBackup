package com.testify.ecfeed.modelif.java.method;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.Constants;
import com.testify.ecfeed.modelif.java.common.Messages;

public class MethodOperationAddTestCase implements IModelOperation {
	
	private MethodNode fTarget;
	private TestCaseNode fTestCase;
	private int fIndex;
	
	public MethodOperationAddTestCase(MethodNode target, TestCaseNode testCase, int index) {
		fTarget = target;
		fTestCase = testCase;
		fIndex = index;
	}

	public MethodOperationAddTestCase(MethodNode target, TestCaseNode testCase) {
		fTarget = target;
		fTestCase = testCase;
		fIndex = -1;
	}

	@Override
	public void execute() throws ModelIfException {
		if(fIndex == -1){
			fIndex = fTarget.getTestCases().size();
		}
		if(fTestCase.getName().matches(Constants.REGEX_TEST_CASE_NODE_NAME) == false){
			throw new ModelIfException(Messages.TEST_CASE_NAME_REGEX_PROBLEM);
		}
		if(fTestCase.updateReferences(fTarget) == false){
			throw new ModelIfException(Messages.TEST_CASE_INCOMPATIBLE_WITH_METHOD);
		}
		else{
			fTarget.addTestCase(fTestCase, fIndex);
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationRemoveTestCase(fTarget, fTestCase);
	}

}
