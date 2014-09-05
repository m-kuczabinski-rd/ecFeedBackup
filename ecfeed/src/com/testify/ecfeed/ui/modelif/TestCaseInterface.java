package com.testify.ecfeed.ui.modelif;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.JavaUtils;
import com.testify.ecfeed.modelif.java.testcase.TestCaseOperationRename;
import com.testify.ecfeed.modelif.java.testcase.TestCaseOperationUpdateTestData;

public class TestCaseInterface extends GenericNodeInterface {

	private TestCaseNode fTarget;
//	private MethodInterface fMethodIf;
	
	public TestCaseInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
	}

	public void setTarget(TestCaseNode target){
		super.setTarget(target);
		fTarget = target;
	}
	
	@Override
	public boolean setName(String newName, AbstractFormPart source, IModelUpdateListener updateListener) {
		return execute(new TestCaseOperationRename(fTarget, newName), source, updateListener, Messages.DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE);
	}

	public boolean isExecutable(TestCaseNode tc){
		MethodInterface mIf = new MethodInterface(null);
		if(tc.getMethod() == null) return false;
		mIf.setTarget(tc.getMethod());
		ImplementationStatus tcStatus = implementationStatus(tc);
		ImplementationStatus methodStatus = mIf.implementationStatus();
		return tcStatus == ImplementationStatus.IMPLEMENTED && methodStatus != ImplementationStatus.NOT_IMPLEMENTED;
	}

	public boolean isExecutable(){
		return isExecutable(fTarget);
	}
	
	static public boolean validateName(String name){
		return JavaUtils.isValidTestCaseName(name);
	}

	public boolean updateTestData(int index, PartitionNode value, AbstractFormPart source, IModelUpdateListener updateListener) {
		return execute(new TestCaseOperationUpdateTestData(fTarget, index, value), source, updateListener, Messages.DIALOG_UPDATE_TEST_DATA_PROBLEM_TITLE);
	}

}
