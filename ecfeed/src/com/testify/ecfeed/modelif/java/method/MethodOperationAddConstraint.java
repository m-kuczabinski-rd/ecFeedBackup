package com.testify.ecfeed.modelif.java.method;

import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.Constants;
import com.testify.ecfeed.modelif.java.common.Messages;

public class MethodOperationAddConstraint implements IModelOperation {

	private MethodNode fTarget;
	private ConstraintNode fConstraint;
	private int fIndex;

	public MethodOperationAddConstraint(MethodNode target, ConstraintNode constraint, int index){
		fTarget = target;
		fConstraint = constraint;
		fIndex = index; 
	}
	
	@Override
	public void execute() throws ModelIfException {
		if(fConstraint.getName().matches(Constants.REGEX_CONSTRAINT_NODE_NAME) == false){
			throw new ModelIfException(Messages.CONSTRAINT_NAME_REGEX_PROBLEM);
		}
		fTarget.addConstraint(fConstraint, fIndex);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationRemoveConstraint(fTarget, fConstraint);
	}
	
}
