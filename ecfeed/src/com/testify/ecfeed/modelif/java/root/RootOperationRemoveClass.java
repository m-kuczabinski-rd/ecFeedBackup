package com.testify.ecfeed.modelif.java.root;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class RootOperationRemoveClass implements IModelOperation {

	private ClassNode fRemovedClass;
	private RootNode fTarget;
	private int fCurrentIndex;
	
	public RootOperationRemoveClass(RootNode target, ClassNode removedClass) {
		fTarget = target;
		fRemovedClass = removedClass;
		fCurrentIndex = removedClass.getIndex();
	}

	@Override
	public void execute() throws ModelIfException {
		fTarget.removeClass(fRemovedClass);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new RootOperationAddNewClass(fTarget, fRemovedClass, fCurrentIndex);
	}

}
