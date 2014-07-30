package com.testify.ecfeed.modelif.java.root;

import java.util.Collection;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class RootOperationAddClasses implements IModelOperation {

	private Collection<ClassNode> fAddedClasses;
	private RootNode fTarget;

	public RootOperationAddClasses(RootNode target, Collection<ClassNode> addedClasses){
		fTarget = target;
		fAddedClasses = addedClasses;
	}
	
	@Override
	public void execute() throws ModelIfException {
	}

	@Override
	public IModelOperation reverseOperation() {
		return new RootOperationRemoveClasses(fTarget, fAddedClasses);
	}

}
