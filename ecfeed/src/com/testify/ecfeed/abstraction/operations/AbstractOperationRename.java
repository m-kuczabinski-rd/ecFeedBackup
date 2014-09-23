package com.testify.ecfeed.abstraction.operations;

import com.testify.ecfeed.model.GenericNode;

public abstract class AbstractOperationRename extends AbstractModelOperation {

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
