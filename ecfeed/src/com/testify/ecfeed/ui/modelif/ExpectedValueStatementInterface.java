package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.ExpectedValueStatement;
import com.testify.ecfeed.model.EStatementRelation;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.operations.PartitionOperationSetValue;
import com.testify.ecfeed.modeladp.operations.StatementOperationSetRelation;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.TypeAdapterProvider;

public class ExpectedValueStatementInterface extends BasicStatementInterface{

	ExpectedValueStatement fTarget;

	public ExpectedValueStatementInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	public void setTarget(ExpectedValueStatement target){
		super.setTarget(target);
		fTarget = target;
	}

	public boolean setRelation(EStatementRelation relation) {
		if(relation != fTarget.getRelation()){
			IModelOperation operation = new StatementOperationSetRelation(fTarget, relation);
			return execute(operation, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public boolean setConditionValue(String newValue) {
		IModelOperation operation = new PartitionOperationSetValue(fTarget.getCondition(), newValue, new TypeAdapterProvider());
		return 	execute(operation, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
	}
	
	@Override
	public String getConditionValue() {
		return fTarget.getCondition().getValueString();
	}

}
