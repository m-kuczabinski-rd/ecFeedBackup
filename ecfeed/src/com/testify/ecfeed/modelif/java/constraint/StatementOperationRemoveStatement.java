package com.testify.ecfeed.modelif.java.constraint;

import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class StatementOperationRemoveStatement implements IModelOperation {

	private StatementArray fTarget;
	private BasicStatement fStatement;
	private int fIndex;

	public StatementOperationRemoveStatement(StatementArray target, BasicStatement statement){
		fTarget = target;
		fStatement = statement;
		fIndex = target.getChildren().indexOf(statement);
	}
	
	@Override
	public void execute() throws ModelIfException {
		fTarget.removeChild(fStatement);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new StatementOperationAddStatement(fTarget, fStatement, fIndex);
	}
}
