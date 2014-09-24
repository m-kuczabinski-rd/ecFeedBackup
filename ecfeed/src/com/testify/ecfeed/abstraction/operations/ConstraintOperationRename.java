package com.testify.ecfeed.abstraction.operations;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.abstraction.java.Constants;
import com.testify.ecfeed.model.ConstraintNode;

public class ConstraintOperationRename extends AbstractOperationRename {
	
	public ConstraintOperationRename(ConstraintNode target, String newName){
		super(target, newName);
	}
	
	@Override
	public void execute() throws ModelIfException {
		if(getNewName().matches(Constants.REGEX_CONSTRAINT_NODE_NAME) == false){
			throw new ModelIfException(Messages.CONSTRAINT_NAME_REGEX_PROBLEM);
		}
		getTarget().setName(getNewName());
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ConstraintOperationRename((ConstraintNode)getTarget(), getOriginalName());
	}

}
