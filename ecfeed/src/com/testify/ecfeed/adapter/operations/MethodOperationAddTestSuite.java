package com.testify.ecfeed.adapter.operations;

import java.util.List;

import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.TestCaseNode;

public class MethodOperationAddTestSuite extends BulkOperation {

	public MethodOperationAddTestSuite(MethodNode target, String testSuiteName, List<List<ChoiceNode>> testData, ITypeAdapterProvider adapterProvider) {
		super(OperationNames.ADD_TEST_CASES, false);
		for(List<ChoiceNode> values : testData){
			addOperation(new MethodOperationAddTestCase(target, new TestCaseNode(testSuiteName, values), adapterProvider));
		}
	}

}
