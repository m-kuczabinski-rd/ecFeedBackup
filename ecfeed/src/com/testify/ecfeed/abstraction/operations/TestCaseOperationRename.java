package com.testify.ecfeed.abstraction.operations;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.abstraction.java.Constants;
import com.testify.ecfeed.model.TestCaseNode;

public class TestCaseOperationRename extends AbstractOperationRename {

	public TestCaseOperationRename(TestCaseNode target, String newName) {
		super(target, newName);
	}

	@Override
	public void execute() throws ModelIfException {
		if(fNewName.matches(Constants.REGEX_TEST_CASE_NODE_NAME) == false){
			throw new ModelIfException(Messages.TEST_CASE_NAME_REGEX_PROBLEM);
		}
		getTarget().setName(fNewName);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new TestCaseOperationRename((TestCaseNode)getTarget(), getOriginalName());
	}
}
