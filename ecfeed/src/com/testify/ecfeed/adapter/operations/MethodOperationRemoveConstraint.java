package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;

public class MethodOperationRemoveConstraint extends AbstractModelOperation {

	private MethodNode fTarget;
	private ConstraintNode fConstraint;
	private int fIndex;

	public MethodOperationRemoveConstraint(MethodNode target, ConstraintNode constraint){
		super(OperationNames.REMOVE_CONSTRAINT);
		fTarget = target;
		fConstraint = constraint;
		fIndex = fConstraint.getIndex();
	}
	
	@Override
	public void execute() throws ModelOperationException {
		fIndex = fConstraint.getIndex();
		fTarget.removeConstraint(fConstraint);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationAddConstraint(fTarget, fConstraint, fIndex);
	}

}
