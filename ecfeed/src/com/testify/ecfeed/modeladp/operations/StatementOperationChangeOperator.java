package com.testify.ecfeed.modeladp.operations;

import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;

public class StatementOperationChangeOperator extends AbstractModelOperation {

	private StatementArray fTarget;
	private Operator fNewOperator;
	private Operator fCurrentOperator;

	public StatementOperationChangeOperator(StatementArray target, Operator operator) {
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
