package com.testify.ecfeed.modelif.operations;

import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class MethodOperationRemoveConstraint implements IModelOperation {

	private MethodNode fTarget;
	private ConstraintNode fConstraint;
	private int fIndex;

	public MethodOperationRemoveConstraint(MethodNode target, ConstraintNode constraint){
		fTarget = target;
		fConstraint = constraint;
		fIndex = fConstraint.getIndex();
	}
	
	@Override
	public void execute() throws ModelIfException {
		fTarget.removeConstraint(fConstraint);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationAddConstraint(fTarget, fConstraint, fIndex);
	}

}
