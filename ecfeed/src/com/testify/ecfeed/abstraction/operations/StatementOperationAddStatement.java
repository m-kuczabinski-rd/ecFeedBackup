package com.testify.ecfeed.abstraction.operations;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.StatementArray;

public class StatementOperationAddStatement extends AbstractModelOperation {

	private BasicStatement fStatement;
	private StatementArray fTarget;
	private int fIndex;

	public StatementOperationAddStatement(StatementArray parent, BasicStatement statement, int index) {
		super(OperationNames.ADD_STATEMENT);
		fTarget = parent;
		fStatement = statement;
		fIndex = index;
	}

	@Override
	public void execute() throws ModelIfException {
		fTarget.addStatement(fStatement, fIndex);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new StatementOperationRemoveStatement(fTarget, fStatement);
	}

}
