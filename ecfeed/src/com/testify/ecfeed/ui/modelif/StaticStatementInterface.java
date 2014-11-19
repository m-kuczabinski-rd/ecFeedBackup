package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.StaticStatement;

public class StaticStatementInterface extends AbstractStatementInterface{

	public StaticStatementInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	public void setTarget(StaticStatement statement) {
		super.setTarget(statement);
	}
}
