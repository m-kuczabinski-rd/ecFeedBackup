package com.testify.ecfeed.modelif.java.root;

import java.util.Collection;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.java.common.AbstractBulkOperation;

public class RootOperationRemoveClasses extends AbstractBulkOperation {

	private RootNode fTarget;
	private Collection<ClassNode> fClasses;

	public RootOperationRemoveClasses(RootNode target, Collection<ClassNode> classes){
		for(ClassNode removedClass : classes){
			addOperation(new RootOperationAddNewClass(target, removedClass));
		}
		fTarget = target;
		fClasses = classes;
	}

	@Override
	public IModelOperation reverseOperation() {
		return new RootOperationAddClasses(fTarget, fClasses);
	}
}
