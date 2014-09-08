package com.testify.ecfeed.modelif.java.root;

import java.util.Collection;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.modelif.java.common.BulkOperation;

public class RootOperationAddClasses extends BulkOperation {
	public RootOperationAddClasses(RootNode target, Collection<ClassNode> classes, int index) {
		super(false);
		for(ClassNode classNode : classes){
			addOperation(new RootOperationAddNewClass(target, classNode, index++));
		}
	}
}
