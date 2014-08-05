package com.testify.ecfeed.modelif.java.classx;

import java.util.Collection;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.java.common.AbstractBulkOperation;

public class ClassOperationRemoveMethods extends AbstractBulkOperation {

	private ClassNode fTarget;
	private Collection<MethodNode> fMethods;

	public ClassOperationRemoveMethods(ClassNode target, Collection<MethodNode> methods) {
		for(MethodNode method : methods){
			addOperation(new ClassOperationRemoveMethod(target, method));
		}
		fTarget = target;
		fMethods = methods;
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ClassOperationAddMethods(fTarget, fMethods);
	}
}
