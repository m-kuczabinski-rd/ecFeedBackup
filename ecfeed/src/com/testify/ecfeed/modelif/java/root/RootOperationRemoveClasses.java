package com.testify.ecfeed.modelif.java.root;

import java.util.Collection;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class RootOperationRemoveClasses implements IModelOperation {

	private Collection<ClassNode> fRemovedClasses;
	private RootNode fTarget;
	
	public RootOperationRemoveClasses(RootNode target, Collection<ClassNode> removedClasses){
		fRemovedClasses = removedClasses;
		fTarget = target;
	}
	
	@Override
	public void execute() throws ModelIfException {
		for(ClassNode removed : fRemovedClasses){
			fTarget.removeClass(removed);
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new RootOperationAddClasses(fTarget, fRemovedClasses);
	}
}
