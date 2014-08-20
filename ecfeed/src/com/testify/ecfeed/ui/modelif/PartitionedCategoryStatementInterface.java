package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement.ICondition;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.constraint.StatementOperationSetCondition;
import com.testify.ecfeed.modelif.java.constraint.StatementOperationSetRelation;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

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
	public boolean setRelation(Relation relation, BasicSection source, IModelUpdateListener updateListener) {
		if(relation != fTarget.getRelation()){
			IModelOperation operation = new StatementOperationSetRelation(fTarget, relation);
			return execute(operation, source, updateListener, Messages.DIALOG_SET_RELATION_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public boolean updateCondition(String text, BasicSection source, IModelUpdateListener updateListener) {
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
			return execute(operation, source, updateListener, Messages.DIALOG_SET_CONDITION_PROBLEM_TITLE);
		}
		return false;
	}
}
