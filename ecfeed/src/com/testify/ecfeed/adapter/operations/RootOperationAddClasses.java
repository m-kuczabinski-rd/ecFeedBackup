package com.testify.ecfeed.adapter.operations;

import java.util.Collection;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;

public class RootOperationAddClasses extends BulkOperation {
	public RootOperationAddClasses(RootNode target, Collection<ClassNode> classes, int index) {
		super(OperationNames.ADD_CLASSES, false);
		for(ClassNode classNode : classes){
			addOperation(new RootOperationAddNewClass(target, classNode, index++));
		}
	}
}
