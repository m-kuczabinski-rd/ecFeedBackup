package com.testify.ecfeed.modelif.operations;

import java.util.Collection;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;

public class ClassOperationAddMethods extends BulkOperation{

	public ClassOperationAddMethods(ClassNode target, Collection<MethodNode> methods, int index) {
		super(false);
		for(MethodNode method : methods){
			addOperation(new ClassOperationAddMethod(target, method, index++));
		}
	}
}
