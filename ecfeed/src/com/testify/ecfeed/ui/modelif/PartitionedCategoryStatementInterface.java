package com.testify.ecfeed.ui.modelif;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement.ICondition;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.operations.StatementOperationSetCondition;
import com.testify.ecfeed.modelif.operations.StatementOperationSetRelation;

public class PartitionedCategoryStatementInterface extends BasicStatementInterface{

	private PartitionedCategoryStatement fTarget;
	
	public PartitionedCategoryStatementInterface(ModelOperationManager operationManager) {
		super(operationManager);
	}

	public void setTarget(PartitionedCategoryStatement target){
		super.setTarget(target);
		fTarget = target;
	}
	
	@Override
	public boolean setRelation(Relation relation, AbstractFormPart source, IModelUpdateListener updateListener) {
		if(relation != fTarget.getRelation()){
			IModelOperation operation = new StatementOperationSetRelation(fTarget, relation);
			return execute(operation, source, updateListener, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public boolean setConditionValue(String text, AbstractFormPart source, IModelUpdateListener updateListener) {
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
			return execute(operation, source, updateListener, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}
	
	@Override
	public String getConditionValue() {
		return fTarget.getConditionName();
	}

}
