package com.testify.ecfeed.modelif.java.testcase;

import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.Constants;
import com.testify.ecfeed.modelif.java.common.Messages;

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
