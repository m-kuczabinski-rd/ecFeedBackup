package com.testify.ecfeed.modeladp.operations;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;
import com.testify.ecfeed.modeladp.java.Constants;

public class RootOperationRename extends AbstractOperationRename{

	public RootOperationRename(GenericNode target, String newName){
		super(target, newName);
	}
	
	@Override
	public void execute() throws ModelOperationException {
		if(fNewName.matches(Constants.REGEX_ROOT_NODE_NAME)){
			fTarget.setName(fNewName);
		}
		else{
			throw new ModelOperationException(Messages.MODEL_NAME_REGEX_PROBLEM);
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new RootOperationRename(fTarget, fOriginalName);
	}

}
