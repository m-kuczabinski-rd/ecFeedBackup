package com.testify.ecfeed.modelif.java.classx;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.modelif.java.common.BulkOperation;
import com.testify.ecfeed.modelif.java.root.RootOperationAddNewClass;
import com.testify.ecfeed.modelif.java.root.RootOperationRemoveClass;

public class ClassOperationMove extends BulkOperation/*implements IModelOperation */{

	public ClassOperationMove(ClassNode target, RootNode newParent, int newIndex) {
		super(false);
		addOperation(new RootOperationAddNewClass(newParent, target, newIndex));
		addOperation(new RootOperationRemoveClass(target.getRoot(), target));
	}
}
