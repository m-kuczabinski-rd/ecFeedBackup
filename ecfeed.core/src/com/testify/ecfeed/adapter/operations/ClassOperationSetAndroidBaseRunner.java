package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.ClassNode;

public class ClassOperationSetAndroidBaseRunner extends AbstractModelOperation {

	private ClassNode fTarget;
	private String fNewValue;
	private String fOriginalValue;

	public ClassOperationSetAndroidBaseRunner(ClassNode target, String newValue) {
		super(OperationNames.SET_ANDROID_BASE_RUNNER);
		fTarget = target;
		fNewValue = newValue;
		fOriginalValue = target.getAndroidBaseRunner();
	}

	@Override
	public void execute() throws ModelOperationException {
		fTarget.setAndroidBaseRunner(fNewValue);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ClassOperationSetAndroidBaseRunner(fTarget, fOriginalValue);
	}

}
