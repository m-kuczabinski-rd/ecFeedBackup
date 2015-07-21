package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.ClassNode;

public class ClassOperationSetRunOnAndroid extends AbstractModelOperation {

	private ClassNode fTarget;
	private boolean fNewValue;
	private boolean fOriginalValue;

	public ClassOperationSetRunOnAndroid(ClassNode target, boolean newValue) {
		super(OperationNames.SET_ANDROID_BASE_RUNNER);
		fTarget = target;
		fNewValue = newValue;
		fOriginalValue = target.getRunOnAndroid();
	}

	@Override
	public void execute() throws ModelOperationException {
		fTarget.setRunOnAndroid(fNewValue);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ClassOperationSetRunOnAndroid(fTarget, fOriginalValue);
	}

}
