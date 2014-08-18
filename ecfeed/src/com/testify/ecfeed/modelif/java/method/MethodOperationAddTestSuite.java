package com.testify.ecfeed.modelif.java.method;

import java.util.List;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.java.common.BulkOperation;

public class MethodOperationAddTestSuite extends BulkOperation {

	public MethodOperationAddTestSuite(MethodNode target, String testSuiteName,
			List<List<PartitionNode>> testData) {
		super(true);
		for(List<PartitionNode> values : testData){
			addOperation(new MethodOperationAddTestCase(target, new TestCaseNode(testSuiteName, values)));
		}
	}

}
