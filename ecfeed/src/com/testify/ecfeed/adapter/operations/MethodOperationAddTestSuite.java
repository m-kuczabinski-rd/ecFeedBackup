package com.testify.ecfeed.adapter.operations;

import java.util.List;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;

public class MethodOperationAddTestSuite extends BulkOperation {

	public MethodOperationAddTestSuite(MethodNode target, String testSuiteName, List<List<PartitionNode>> testData) {
		super(OperationNames.ADD_TEST_CASES, false);
		for(List<PartitionNode> values : testData){
			addOperation(new MethodOperationAddTestCase(target, new TestCaseNode(testSuiteName, values)));
		}
	}

}
