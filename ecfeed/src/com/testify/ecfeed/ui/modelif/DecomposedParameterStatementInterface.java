package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.operations.StatementOperationSetCondition;
import com.testify.ecfeed.adapter.operations.StatementOperationSetRelation;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.EStatementRelation;
import com.testify.ecfeed.model.DecomposedParameterStatement;
import com.testify.ecfeed.model.DecomposedParameterStatement.ICondition;
import com.testify.ecfeed.ui.common.Messages;

public class DecomposedParameterStatementInterface extends BasicStatementInterface{

	private DecomposedParameterStatement fTarget;

	public DecomposedParameterStatementInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	public void setTarget(DecomposedParameterStatement target){
		super.setTarget(target);
		fTarget = target;
	}

	@Override
	public boolean setRelation(EStatementRelation relation) {
		if(relation != fTarget.getRelation()){
			IModelOperation operation = new StatementOperationSetRelation(fTarget, relation);
			return execute(operation, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public boolean setConditionValue(String text) {
		if(fTarget.getConditionName().equals(text) == false){
			ICondition newCondition;
			ParameterNode parameter = fTarget.getParameter();
			if(parameter.getPartition(text) != null){
				newCondition = fTarget.new PartitionCondition(parameter.getPartition(text));
			}
			else{
				if(text.contains("[label]")){
					text = text.substring(0, text.indexOf("[label]"));
				}
				newCondition = fTarget.new LabelCondition(text);
			}
			IModelOperation operation = new StatementOperationSetCondition(fTarget, newCondition);
			return execute(operation, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public String getConditionValue() {
		return fTarget.getConditionName();
	}
}