package com.testify.ecfeed.modelif.java.testcase;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.java.common.BulkOperation;
import com.testify.ecfeed.modelif.java.method.MethodOperationAddTestCase;
import com.testify.ecfeed.modelif.java.method.MethodOperationRemoveTestCase;

public class TestCaseOperationMove extends BulkOperation {

	public TestCaseOperationMove(TestCaseNode target, MethodNode newParent, int newIndex) {
		super(false);
		addOperation(new MethodOperationRemoveTestCase(target.getMethod(), target));
		addOperation(new MethodOperationAddTestCase(newParent, target, newIndex));
	}

}
