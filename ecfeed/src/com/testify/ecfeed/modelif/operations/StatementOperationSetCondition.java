package com.testify.ecfeed.modelif.operations;

import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement.ICondition;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class StatementOperationSetCondition implements IModelOperation {

	private PartitionedCategoryStatement fTarget;
	private ICondition fCurrentCondition;
	private ICondition fNewCondition;

	public StatementOperationSetCondition(PartitionedCategoryStatement target, ICondition condition) {
		fTarget = target;
		fNewCondition = condition;
		fCurrentCondition = target.getCondition();
	}

	@Override
	public void execute() throws ModelIfException {
		fTarget.setCondition(fNewCondition);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new StatementOperationSetCondition(fTarget, fCurrentCondition);
	}

}
