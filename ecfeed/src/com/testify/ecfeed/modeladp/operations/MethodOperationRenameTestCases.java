package com.testify.ecfeed.modeladp.operations;

import java.util.Collection;

import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modeladp.ModelOperationException;
import com.testify.ecfeed.modeladp.java.Constants;

public class MethodOperationRenameTestCases extends BulkOperation {

	public MethodOperationRenameTestCases(Collection<TestCaseNode> testCases, String newName) throws ModelOperationException {
		super(OperationNames.RENAME_TEST_CASE, false);
		if(newName.matches(Constants.REGEX_TEST_CASE_NODE_NAME) == false){
			throw new ModelOperationException(Messages.TEST_CASE_NAME_REGEX_PROBLEM);
		}
		for(TestCaseNode testCase : testCases){
			addOperation(FactoryRenameOperation.getRenameOperation(testCase, newName));
		}
	}
}
