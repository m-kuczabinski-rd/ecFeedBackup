package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.constraint.ExpectedValueStatement;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.operations.PartitionOperationSetValue;
import com.testify.ecfeed.modelif.operations.StatementOperationSetRelation;
import com.testify.ecfeed.ui.common.TypeAdapterProvider;

public class ExpectedValueStatementInterface extends BasicStatementInterface{

	ExpectedValueStatement fTarget;
	
	public void setTarget(ExpectedValueStatement target){
		super.setTarget(target);
		fTarget = target;
	}

	public boolean setRelation(Relation relation, IModelUpdateContext context) {
		if(relation != fTarget.getRelation()){
			IModelOperation operation = new StatementOperationSetRelation(fTarget, relation);
			return execute(operation, context, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public boolean setConditionValue(String newValue, IModelUpdateContext context) {
		IModelOperation operation = new PartitionOperationSetValue(fTarget.getCondition(), newValue, new TypeAdapterProvider());
		return 	execute(operation, context, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
	}
	
	@Override
	public String getConditionValue() {
		return fTarget.getCondition().getValueString();
	}

}
