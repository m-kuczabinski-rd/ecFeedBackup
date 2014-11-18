package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.DecomposedParameterStatement;
import com.testify.ecfeed.model.DecomposedParameterStatement.ICondition;

public class StatementOperationSetCondition extends AbstractModelOperation {

	private DecomposedParameterStatement fTarget;
	private ICondition fCurrentCondition;
	private ICondition fNewCondition;

	public StatementOperationSetCondition(DecomposedParameterStatement target, ICondition condition) {
		super(OperationNames.SET_STATEMENT_CONDITION);
		fTarget = target;
		fNewCondition = condition;
		fCurrentCondition = target.getCondition();
	}

	@Override
	public void execute() throws ModelOperationException {
		fTarget.setCondition(fNewCondition);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new StatementOperationSetCondition(fTarget, fCurrentCondition);
	}

}
