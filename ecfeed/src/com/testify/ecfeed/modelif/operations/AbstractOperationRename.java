package com.testify.ecfeed.modelif.operations;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.IModelOperation;

public abstract class AbstractOperationRename implements IModelOperation {

	protected GenericNode fTarget;
	protected String fNewName;
	protected String fOriginalName;

	public AbstractOperationRename(GenericNode target, String newName){
		fTarget = target;
		fNewName = newName;
		fOriginalName = target.getName();
	}
	
	protected GenericNode getTarget(){
		return fTarget;
	}
	
	protected String getOriginalName(){
		return fOriginalName;
	}
	
	protected String getNewName(){
		return fNewName;
	}
}
