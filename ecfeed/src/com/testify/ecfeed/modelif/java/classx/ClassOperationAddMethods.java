package com.testify.ecfeed.modelif.java.classx;

import java.util.Collection;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.java.common.BulkOperation;
import com.testify.ecfeed.modelif.java.common.RemoveNodesOperation;

public class ClassOperationAddMethods extends BulkOperation{

	private Collection<MethodNode> fMethods;

	public ClassOperationAddMethods(ClassNode target, Collection<MethodNode> methods) {
		for(MethodNode method : methods){
			addOperation(new ClassOperationAddMethod(target, method));
		}
		fMethods = methods;
	}

	@Override
	public IModelOperation reverseOperation() {
		return new RemoveNodesOperation(fMethods);
	}
	
}
