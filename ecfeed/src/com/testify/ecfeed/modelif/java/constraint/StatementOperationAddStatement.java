package com.testify.ecfeed.modelif.java.constraint;

import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class StatementOperationAddStatement implements IModelOperation {

	private BasicStatement fStatement;
	private StatementArray fTarget;
	private int fIndex;

	public StatementOperationAddStatement(StatementArray parent, BasicStatement statement, int index) {
		fTarget = parent;
		fStatement = statement;
		fIndex = index;
	}

	@Override
	public void execute() throws ModelIfException {
		fTarget.addStatement(fStatement, fIndex);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new StatementOperationRemoveStatement(fTarget, fStatement);
	}

}
