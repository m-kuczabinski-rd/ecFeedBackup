package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.PartitionedParameterStatement;
import com.testify.ecfeed.model.PartitionedParameterStatement.ICondition;

public class StatementOperationSetCondition extends AbstractModelOperation {

	private PartitionedParameterStatement fTarget;
	private ICondition fCurrentCondition;
	private ICondition fNewCondition;

	public StatementOperationSetCondition(PartitionedParameterStatement target, ICondition condition) {
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
