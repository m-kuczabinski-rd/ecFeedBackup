package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.modelif.ModelOperationManager;

public class StaticStatementInterface extends BasicStatementInterface{

	public StaticStatementInterface(ModelOperationManager operationManager) {
		super(operationManager);
	}

	public void setTarget(StaticStatement statement) {
		super.setTarget(statement);
	}

}
