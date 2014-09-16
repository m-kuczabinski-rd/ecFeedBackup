package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.modelif.operations.TestCaseOperationRename;
import com.testify.ecfeed.modelif.operations.TestCaseOperationUpdateTestData;
import com.testify.ecfeed.ui.common.Messages;

public class TestCaseInterface extends GenericNodeInterface {

	private TestCaseNode fTarget;
	
	public void setTarget(TestCaseNode target){
		super.setTarget(target);
		fTarget = target;
	}
	
	@Override
	public boolean setName(String newName, IModelUpdateContext context) {
		IModelOperation operation = new TestCaseOperationRename(fTarget, newName);
		return execute(operation, context, Messages.DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE);
	}

	public boolean isExecutable(TestCaseNode tc){
		MethodInterface mIf = new MethodInterface();
		if(tc.getMethod() == null) return false;
		mIf.setTarget(tc.getMethod());
		ImplementationStatus tcStatus = getImplementationStatus(tc);
		ImplementationStatus methodStatus = mIf.implementationStatus();
		return tcStatus == ImplementationStatus.IMPLEMENTED && methodStatus != ImplementationStatus.NOT_IMPLEMENTED;
	}

	public boolean isExecutable(){
		return isExecutable(fTarget);
	}
	
	public boolean updateTestData(int index, PartitionNode value, IModelUpdateContext context) {
		IModelOperation operation = new TestCaseOperationUpdateTestData(fTarget, index, value);
		return execute(operation, context, Messages.DIALOG_UPDATE_TEST_DATA_PROBLEM_TITLE);
	}

}
