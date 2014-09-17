package com.testify.ecfeed.abstraction.operations;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.StatementArray;

public class StatementOperationChangeOperator implements IModelOperation {

	private StatementArray fTarget;
	private Operator fNewOperator;
	private Operator fCurrentOperator;

	public StatementOperationChangeOperator(StatementArray target, Operator operator) {
		fTarget = target;
		fNewOperator = operator;
		fCurrentOperator = target.getOperator();
	}

	@Override
	public void execute() throws ModelIfException {
		fTarget.setOperator(fNewOperator);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new StatementOperationChangeOperator(fTarget, fCurrentOperator);
	}

}
