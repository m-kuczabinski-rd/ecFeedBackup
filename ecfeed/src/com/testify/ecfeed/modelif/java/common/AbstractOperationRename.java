package com.testify.ecfeed.modelif.java.common;

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
}
