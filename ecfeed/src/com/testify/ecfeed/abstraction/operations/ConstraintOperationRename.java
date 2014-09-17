package com.testify.ecfeed.abstraction.operations;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.abstraction.java.Constants;
import com.testify.ecfeed.model.ConstraintNode;

public class ConstraintOperationRename implements IModelOperation {

	private ConstraintNode fTarget;
	private String fNewName;
	private String fCurrentName;
	
	public ConstraintOperationRename(ConstraintNode target, String newName){
		fTarget = target;
		fNewName = newName;
		fCurrentName = target.getName();
	}
	
	@Override
	public void execute() throws ModelIfException {
		if(fNewName.matches(Constants.REGEX_CONSTRAINT_NODE_NAME) == false){
			throw new ModelIfException(Messages.CONSTRAINT_NAME_REGEX_PROBLEM);
		}
		fTarget.setName(fNewName);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ConstraintOperationRename(fTarget, fCurrentName);
	}

}
