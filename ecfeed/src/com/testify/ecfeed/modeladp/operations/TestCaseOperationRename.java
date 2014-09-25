package com.testify.ecfeed.modeladp.operations;

import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;
import com.testify.ecfeed.modeladp.java.Constants;

public class TestCaseOperationRename extends AbstractOperationRename {

	public TestCaseOperationRename(TestCaseNode target, String newName) {
		super(target, newName);
	}

	@Override
	public void execute() throws ModelOperationException {
		if(fNewName.matches(Constants.REGEX_TEST_CASE_NODE_NAME) == false){
			throw new ModelOperationException(Messages.TEST_CASE_NAME_REGEX_PROBLEM);
		}
		getTarget().setName(fNewName);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new TestCaseOperationRename((TestCaseNode)getTarget(), getOriginalName());
	}
}
