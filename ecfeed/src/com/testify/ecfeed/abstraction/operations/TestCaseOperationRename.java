package com.testify.ecfeed.abstraction.operations;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.abstraction.java.Constants;
import com.testify.ecfeed.model.TestCaseNode;

public class TestCaseOperationRename implements IModelOperation {

	private TestCaseNode fTarget;
	private String fNewName;
	private String fCurrentName;
	
	public TestCaseOperationRename(TestCaseNode target, String newName) {
		fTarget = target;
		fNewName = newName;
		fCurrentName = target.getName();
	}

	@Override
	public void execute() throws ModelIfException {
		if(fNewName.matches(Constants.REGEX_TEST_CASE_NODE_NAME) == false){
			throw new ModelIfException(Messages.TEST_CASE_NAME_REGEX_PROBLEM);
		}
		fTarget.setName(fNewName);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new TestCaseOperationRename(fTarget, fCurrentName);
	}

}
