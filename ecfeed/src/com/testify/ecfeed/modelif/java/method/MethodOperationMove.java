package com.testify.ecfeed.modelif.java.method;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.java.classx.ClassOperationAddMethod;
import com.testify.ecfeed.modelif.java.classx.ClassOperationRemoveMethod;
import com.testify.ecfeed.modelif.java.common.BulkOperation;

public class MethodOperationMove extends BulkOperation{
	
	public MethodOperationMove(MethodNode target, ClassNode newParent, int newIndex) {
		super(false);
		addOperation(new ClassOperationAddMethod(newParent, target, newIndex));
		addOperation(new ClassOperationRemoveMethod(target.getClassNode(), target));
	}
}
