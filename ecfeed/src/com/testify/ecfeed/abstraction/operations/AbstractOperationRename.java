package com.testify.ecfeed.abstraction.operations;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.model.GenericNode;

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
