package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.constraint.ExpectedValueStatement;
import com.testify.ecfeed.modelif.ModelOperationManager;

public class ExpectedValueStatementInterface extends BasicStatementInterface{

	ExpectedValueStatement fTarget;
	
	public ExpectedValueStatementInterface(ModelOperationManager operationManager) {
		super(operationManager);
	}
	
	public void setTarget(ExpectedValueStatement target){
		super.setTarget(target);
		fTarget = target;
	}

}
