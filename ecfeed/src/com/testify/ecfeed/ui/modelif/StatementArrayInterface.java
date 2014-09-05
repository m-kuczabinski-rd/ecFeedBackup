package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.constraint.StatementOperationAddStatement;
import com.testify.ecfeed.modelif.java.constraint.StatementOperationChangeOperator;
import com.testify.ecfeed.modelif.java.constraint.StatementOperationRemoveStatement;
import com.testify.ecfeed.modelif.java.constraint.StatementOperationReplaceChild;
import com.testify.ecfeed.ui.editor.BasicSection;

public class StatementArrayInterface extends BasicStatementInterface{

	private StatementArray fTarget;
	
	public StatementArrayInterface(ModelOperationManager operationManager) {
		super(operationManager);
	}

	public void setTarget(StatementArray target){
		super.setTarget(target);
		fTarget = target;
	}
	
	@Override
	public boolean addStatement(BasicStatement statement, BasicSection source, IModelUpdateListener updateListener){
		IModelOperation operation = new StatementOperationAddStatement(fTarget, statement, fTarget.getChildren().size()); 
		return execute(operation, source, updateListener, Messages.DIALOG_ADD_STATEMENT_PROBLEM_TITLE);
	}
	
	public boolean removeChild(BasicStatement child, BasicSection source, IModelUpdateListener updateListener){
		IModelOperation operation = new StatementOperationRemoveStatement(fTarget, child); 
		return execute(operation, source, updateListener, Messages.DIALOG_REMOVE_STATEMENT_PROBLEM_TITLE);
	}

	@Override
	public boolean setOperator(Operator operator, BasicSection source, IModelUpdateListener updateListener) {
		if(operator != fTarget.getOperator()){
			IModelOperation operation = new StatementOperationChangeOperator(fTarget, operator); 
			return execute(operation, source, updateListener, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public Operator getOperator() {
		return fTarget.getOperator();
	}

	@Override
	public boolean replaceChild(BasicStatement child, BasicStatement newStatement, BasicSection source, IModelUpdateListener updateListener) {
		if(child != newStatement){
			return execute(new StatementOperationReplaceChild(fTarget, child, newStatement), source, updateListener, Messages.DIALOG_ADD_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

}
