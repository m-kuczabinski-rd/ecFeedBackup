package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.operations.StatementOperationAddStatement;
import com.testify.ecfeed.abstraction.operations.StatementOperationChangeOperator;
import com.testify.ecfeed.abstraction.operations.StatementOperationRemoveStatement;
import com.testify.ecfeed.abstraction.operations.StatementOperationReplaceChild;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.ui.common.Messages;

public class StatementArrayInterface extends BasicStatementInterface{

	private StatementArray fTarget;
	
	public void setTarget(StatementArray target){
		super.setTarget(target);
		fTarget = target;
	}
	
	@Override
	public boolean addStatement(BasicStatement statement, IModelUpdateContext context){
		IModelOperation operation = new StatementOperationAddStatement(fTarget, statement, fTarget.getChildren().size()); 
		return execute(operation, context, Messages.DIALOG_ADD_STATEMENT_PROBLEM_TITLE);
	}
	
	public boolean removeChild(BasicStatement child, IModelUpdateContext context){
		IModelOperation operation = new StatementOperationRemoveStatement(fTarget, child); 
		return execute(operation, context, Messages.DIALOG_REMOVE_STATEMENT_PROBLEM_TITLE);
	}

	@Override
	public boolean setOperator(Operator operator, IModelUpdateContext context) {
		if(operator != fTarget.getOperator()){
			IModelOperation operation = new StatementOperationChangeOperator(fTarget, operator); 
			return execute(operation, context, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public Operator getOperator() {
		return fTarget.getOperator();
	}

	@Override
	public boolean replaceChild(BasicStatement child, BasicStatement newStatement, IModelUpdateContext context) {
		if(child != newStatement){
			IModelOperation operation = new StatementOperationReplaceChild(fTarget, child, newStatement);
			return execute(operation, context, Messages.DIALOG_ADD_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

}
