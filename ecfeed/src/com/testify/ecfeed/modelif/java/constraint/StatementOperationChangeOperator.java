package com.testify.ecfeed.modelif.java.constraint;

import com.testify.ecfeed.model.constraint.Operator;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

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
