package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modeladp.EImplementationStatus;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.operations.TestCaseOperationUpdateTestData;
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
	
	public boolean updateTestData(int index, PartitionNode value) {
		IModelOperation operation = new TestCaseOperationUpdateTestData(fTarget, index, value);
		return execute(operation, Messages.DIALOG_UPDATE_TEST_DATA_PROBLEM_TITLE);
	}
}
