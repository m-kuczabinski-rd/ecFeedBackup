package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.PartitionedCategoryStatement;
import com.testify.ecfeed.model.PartitionedCategoryStatement.ICondition;

public class StatementOperationSetCondition extends AbstractModelOperation {

	private PartitionedCategoryStatement fTarget;
	private ICondition fCurrentCondition;
	private ICondition fNewCondition;

	public StatementOperationSetCondition(PartitionedCategoryStatement target, ICondition condition) {
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
