package com.testify.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.Arrays;

import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.operations.TestCaseOperationUpdateTestData;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.Messages;

public class TestCaseInterface extends GenericNodeInterface {

	private TestCaseNode fTarget;

	public TestCaseInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	public void setTarget(TestCaseNode target){
		super.setTarget(target);
		fTarget = target;
	}
	
	public boolean isExecutable(TestCaseNode tc){
		MethodInterface mIf = new MethodInterface(getUpdateContext());
		if(tc.getMethod() == null) return false;
		mIf.setTarget(tc.getMethod());
		EImplementationStatus tcStatus = getImplementationStatus(tc);
		EImplementationStatus methodStatus = mIf.getImplementationStatus();
		return tcStatus == EImplementationStatus.IMPLEMENTED && methodStatus != EImplementationStatus.NOT_IMPLEMENTED;
	}

	public boolean isExecutable(){
		return isExecutable(fTarget);
	}
	
	public void execute() {
		MethodInterface methodIf = new MethodInterface(getUpdateContext());
		methodIf.executeStaticTests(new ArrayList<TestCaseNode>(Arrays.asList(new TestCaseNode[]{fTarget})));
	}

	public boolean updateTestData(int index, ChoiceNode value) {
		IModelOperation operation = new TestCaseOperationUpdateTestData(fTarget, index, value);
		return execute(operation, Messages.DIALOG_UPDATE_TEST_DATA_PROBLEM_TITLE);
	}
}
