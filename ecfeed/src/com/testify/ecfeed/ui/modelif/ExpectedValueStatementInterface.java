package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.constraint.ExpectedValueStatement;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.constraint.StatementOperationSetRelation;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

public class ExpectedValueStatementInterface extends BasicStatementInterface{

	ExpectedValueStatement fTarget;
	
	public ExpectedValueStatementInterface(ModelOperationManager operationManager) {
		super(operationManager);
	}
	
	public void setTarget(ExpectedValueStatement target){
		super.setTarget(target);
		fTarget = target;
	}

	public boolean setRelation(Relation relation, BasicSection source, IModelUpdateListener updateListener) {
		if(relation != fTarget.getRelation()){
			IModelOperation operation = new StatementOperationSetRelation(fTarget, relation);
			return execute(operation, source, updateListener, Messages.DIALOG_SET_RELATION_PROBLEM_TITLE);
		}
		return false;
	}

}
