package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.ClassNode;

public class ClassOperationSetAndroidRunner extends AbstractModelOperation {
	
	private ClassNode fTarget;
	private String fNewValue;
	private String fOriginalValue;

	public ClassOperationSetAndroidRunner(ClassNode target, String newValue) {
		super(OperationNames.SET_ANDROID_RUNNER);
		fTarget = target;
		fNewValue = newValue;
		fOriginalValue = target.getAndroidRunner();
	}

	@Override
	public void execute() throws ModelOperationException {
		fTarget.setAndroidRunner(fNewValue);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ClassOperationSetAndroidRunner(fTarget, fOriginalValue);
	}

}
