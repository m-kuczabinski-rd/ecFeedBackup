package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement.ICondition;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.operations.StatementOperationSetCondition;
import com.testify.ecfeed.modeladp.operations.StatementOperationSetRelation;
import com.testify.ecfeed.ui.common.Messages;

public class PartitionedCategoryStatementInterface extends BasicStatementInterface{

	private PartitionedCategoryStatement fTarget;

	public PartitionedCategoryStatementInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	public void setTarget(PartitionedCategoryStatement target){
		super.setTarget(target);
		fTarget = target;
	}
	
	@Override
	public boolean setRelation(Relation relation) {
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
			CategoryNode category = fTarget.getCategory();
			if(category.getPartition(text) != null){
				newCondition = fTarget.new PartitionCondition(category.getPartition(text));
			}
			else{
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