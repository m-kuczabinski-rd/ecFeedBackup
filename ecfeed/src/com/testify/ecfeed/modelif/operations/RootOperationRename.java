package com.testify.ecfeed.modelif.operations;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.Constants;

public class RootOperationRename extends AbstractOperationRename{

	public RootOperationRename(GenericNode target, String newName){
		super(target, newName);
	}
	
	@Override
	public void execute() throws ModelIfException {
		if(fNewName.matches(Constants.REGEX_ROOT_NODE_NAME)){
			fTarget.setName(fNewName);
		}
		else{
			throw new ModelIfException(Messages.MODEL_NAME_REGEX_PROBLEM);
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new RootOperationRename(fTarget, fOriginalName);
	}

}
