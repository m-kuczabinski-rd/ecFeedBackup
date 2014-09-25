package com.testify.ecfeed.modeladp.operations;

import com.testify.ecfeed.model.EStatementOperator;
import com.testify.ecfeed.model.StatementArray;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;

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
