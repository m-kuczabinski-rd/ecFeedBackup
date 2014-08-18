package com.testify.ecfeed.modelif.java.testcase;

import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class TestCaseOperationShift implements IModelOperation {

	private int fNewIndex;
	private int fCurrentIndex;
	
	public TestCaseOperationShift(TestCaseNode target, int newIndex){
	}

	@Override
	public void execute() throws ModelIfException {
		// TODO Auto-generated method stub

	}

	@Override
	public IModelOperation reverseOperation() {
		// TODO Auto-generated method stub
		return null;
	}

}
