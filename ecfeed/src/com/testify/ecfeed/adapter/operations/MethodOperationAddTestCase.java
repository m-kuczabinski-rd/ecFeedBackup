package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.java.Constants;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;

public class MethodOperationAddTestCase extends AbstractModelOperation {
	
	private MethodNode fTarget;
	private TestCaseNode fTestCase;
	private int fIndex;
	
	public MethodOperationAddTestCase(MethodNode target, TestCaseNode testCase, int index) {
		super(OperationNames.ADD_TEST_CASE);
		fTarget = target;
		fTestCase = testCase;
		fIndex = index;
	}

	public MethodOperationAddTestCase(MethodNode target, TestCaseNode testCase) {
		this(target, testCase, -1);
	}

	@Override
	public void execute() throws ModelOperationException {
		if(fIndex == -1){
			fIndex = fTarget.getTestCases().size();
		}
		if(fTestCase.getName().matches(Constants.REGEX_TEST_CASE_NODE_NAME) == false){
			throw new ModelOperationException(Messages.TEST_CASE_NAME_REGEX_PROBLEM);
		}
		if(fTestCase.updateReferences(fTarget) == false){
			throw new ModelOperationException(Messages.TEST_CASE_INCOMPATIBLE_WITH_METHOD);
		}
		else{
			fTarget.addTestCase(fTestCase, fIndex);
		}
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationRemoveTestCase(fTarget, fTestCase);
	}

}
