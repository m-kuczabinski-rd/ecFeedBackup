package com.testify.ecfeed.core.adapter.operations;

import com.ecfeed.core.model.ClassNode;
import com.testify.ecfeed.core.adapter.IModelOperation;
import com.testify.ecfeed.core.adapter.ModelOperationException;

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
