package com.testify.ecfeed.modeladp.operations;

import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;
import com.testify.ecfeed.modeladp.java.Constants;

public class ConstraintOperationRename extends AbstractOperationRename {
	
	public ConstraintOperationRename(ConstraintNode target, String newName){
		super(target, newName);
	}
	
	@Override
	public void execute() throws ModelOperationException {
		if(getNewName().matches(Constants.REGEX_CONSTRAINT_NODE_NAME) == false){
			throw new ModelOperationException(Messages.CONSTRAINT_NAME_REGEX_PROBLEM);
		}
		getTarget().setName(getNewName());
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ConstraintOperationRename((ConstraintNode)getTarget(), getOriginalName());
	}

}
