package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;

public class RootOperationRemoveClass extends AbstractModelOperation {

	private ClassNode fRemovedClass;
	private RootNode fTarget;
	private int fCurrentIndex;
	
	public RootOperationRemoveClass(RootNode target, ClassNode removedClass) {
		super(OperationNames.REMOVE_CLASS);
		fTarget = target;
		fRemovedClass = removedClass;
		fCurrentIndex = removedClass.getIndex();
	}

	@Override
	public void execute() throws ModelOperationException {
		fCurrentIndex = fRemovedClass.getIndex();
		fTarget.removeClass(fRemovedClass);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new RootOperationAddNewClass(fTarget, fRemovedClass, fCurrentIndex);
	}

}
