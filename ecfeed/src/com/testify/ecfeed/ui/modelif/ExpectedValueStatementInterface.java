package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.operations.ChoiceOperationSetValue;
import com.testify.ecfeed.adapter.operations.StatementOperationSetRelation;
import com.testify.ecfeed.model.EStatementRelation;
import com.testify.ecfeed.model.ExpectedValueStatement;
import com.testify.ecfeed.ui.common.EclipseTypeAdapterProvider;
import com.testify.ecfeed.ui.common.Messages;

public class ExpectedValueStatementInterface extends AbstractStatementInterface{

	public ExpectedValueStatementInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	@Override
	public boolean setRelation(EStatementRelation relation) {
		if(relation != getTarget().getRelation()){
			IModelOperation operation = new StatementOperationSetRelation(getTarget(), relation);
			return execute(operation, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public boolean setConditionValue(String newValue) {
		IModelOperation operation = new ChoiceOperationSetValue(getTarget().getCondition(), newValue, new EclipseTypeAdapterProvider());
		return 	execute(operation, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
	}

	@Override
	public String getConditionValue() {
		return getTarget().getCondition().getValueString();
	}

	@Override
	protected ExpectedValueStatement getTarget(){
		return (ExpectedValueStatement)super.getTarget();
	}

}
