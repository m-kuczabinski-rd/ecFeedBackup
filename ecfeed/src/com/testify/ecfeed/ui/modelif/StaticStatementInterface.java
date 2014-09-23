package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.constraint.StaticStatement;

public class StaticStatementInterface extends BasicStatementInterface{

	public StaticStatementInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	public void setTarget(StaticStatement statement) {
		super.setTarget(statement);
	}
}
