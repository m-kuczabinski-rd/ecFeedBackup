package com.testify.ecfeed.modeladp.operations;

import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;

public class StatementOperationRemoveStatement extends AbstractModelOperation {

	private StatementArray fTarget;
	private BasicStatement fStatement;
	private int fIndex;

	public StatementOperationRemoveStatement(StatementArray target, BasicStatement statement){
		super(OperationNames.REMOVE_STATEMENT);
		fTarget = target;
		fStatement = statement;
		fIndex = target.getChildren().indexOf(statement);
	}
	
	@Override
	public void execute() throws ModelOperationException {
		fTarget.removeChild(fStatement);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new StatementOperationAddStatement(fTarget, fStatement, fIndex);
	}
}
