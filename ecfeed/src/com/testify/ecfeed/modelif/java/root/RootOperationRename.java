package com.testify.ecfeed.modelif.java.root;

import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.Constants;
import com.testify.ecfeed.modelif.java.common.AbstractOperationRename;
import com.testify.ecfeed.modelif.java.common.Messages;

public class RootOperationRename extends AbstractOperationRename{

	public RootOperationRename(RootNode target, String newName){
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
		// TODO Auto-generated method stub
		return null;
	}

}
