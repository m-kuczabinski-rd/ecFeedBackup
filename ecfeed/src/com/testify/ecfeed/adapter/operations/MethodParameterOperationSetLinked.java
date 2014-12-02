package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.MethodParameterNode;

public class MethodParameterOperationSetLinked extends AbstractModelOperation {

	private MethodParameterNode fTarget;
	private boolean fLinked;

	public MethodParameterOperationSetLinked(MethodParameterNode target, boolean linked) {
		super(OperationNames.SET_LINKED);
		fTarget = target;
		fLinked = linked;
	}

	@Override
	public void execute() throws ModelOperationException {
		fTarget.setLinked(fLinked);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodParameterOperationSetLinked(fTarget, !fLinked);
	}
}
