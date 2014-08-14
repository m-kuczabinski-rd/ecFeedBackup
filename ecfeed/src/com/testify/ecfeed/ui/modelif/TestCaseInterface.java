package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.JavaUtils;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

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
	public boolean setName(String newName, BasicSection source, IModelUpdateListener updateListener) {
		return false;
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

}
