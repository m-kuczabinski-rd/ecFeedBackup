package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.EStatementOperator;
import com.testify.ecfeed.model.StatementArray;

public class StatementOperationChangeOperator extends AbstractModelOperation {

	private StatementArray fTarget;
	private EStatementOperator fNewOperator;
	private EStatementOperator fCurrentOperator;

	public StatementOperationChangeOperator(StatementArray target, EStatementOperator operator) {
		super(OperationNames.CHANGE_STATEMENT_OPERATOR);
		fTarget = target;
		fNewOperator = operator;
		fCurrentOperator = target.getOperator();
	}

	@Override
	public void execute() throws ModelOperationException {
		fTarget.setOperator(fNewOperator);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new StatementOperationChangeOperator(fTarget, fCurrentOperator);
	}

}
