package com.testify.ecfeed.modeladp.operations;

import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;
import com.testify.ecfeed.modeladp.java.Constants;

public class MethodOperationAddConstraint extends AbstractModelOperation {

	private MethodNode fTarget;
	private ConstraintNode fConstraint;
	private int fIndex;

	public MethodOperationAddConstraint(MethodNode target, ConstraintNode constraint, int index){
		super(OperationNames.ADD_CONSTRAINT);
		fTarget = target;
		fConstraint = constraint;
		fIndex = index; 
	}

	public MethodOperationAddConstraint(MethodNode target, ConstraintNode constraint){
		this(target, constraint, -1);
	}

	@Override
	public void execute() throws ModelOperationException {
		if(fIndex == -1){
			fIndex = fTarget.getConstraintNodes().size();
		}
		if(fConstraint.getName().matches(Constants.REGEX_CONSTRAINT_NODE_NAME) == false){
			throw new ModelOperationException(Messages.CONSTRAINT_NAME_REGEX_PROBLEM);
		}
		if(fConstraint.updateReferences(fTarget) == false){
			throw new ModelOperationException(Messages.INCOMPATIBLE_CONSTRAINT_PROBLEM);
		}
		fTarget.addConstraint(fConstraint, fIndex);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationRemoveConstraint(fTarget, fConstraint);
	}
	
}
