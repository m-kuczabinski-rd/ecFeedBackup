package com.testify.ecfeed.modelif.java.method;

import java.util.Collection;

import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.Constants;
import com.testify.ecfeed.modelif.java.common.BulkOperation;
import com.testify.ecfeed.modelif.java.common.Messages;
import com.testify.ecfeed.modelif.java.testcase.TestCaseOperationRename;

public class MethodOperationRenameTestCases extends BulkOperation {

	public MethodOperationRenameTestCases(Collection<TestCaseNode> testCases, String newName) throws ModelIfException {
		super(true);
		if(newName.matches(Constants.REGEX_TEST_CASE_NODE_NAME) == false){
			throw new ModelIfException(Messages.TEST_CASE_NAME_REGEX_PROBLEM);
		}
		for(TestCaseNode testCase : testCases){
			addOperation(new TestCaseOperationRename(testCase, newName));
		}
	}
}
