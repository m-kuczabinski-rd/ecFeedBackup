package com.testify.ecfeed.modelif.java.category;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.java.common.BulkOperation;
import com.testify.ecfeed.modelif.java.method.MethodOperationAddParameter;
import com.testify.ecfeed.modelif.java.method.MethodOperationRemoveParameter;

public class CategoryOperationMove extends BulkOperation{

	public CategoryOperationMove(CategoryNode target, MethodNode newParent, int newIndex) {
		super(false);
		addOperation(new MethodOperationAddParameter(newParent, target));
		addOperation(new MethodOperationRemoveParameter(target.getMethod(), target));
	}
	
}
