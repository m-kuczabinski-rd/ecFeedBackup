package com.testify.ecfeed.abstraction.operations;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;

public class ClassOperationRemoveMethod implements IModelOperation {

	private ClassNode fTarget;
	private MethodNode fMethod;
	private int fCurrentIndex;

	public ClassOperationRemoveMethod(ClassNode target, MethodNode method) {
		fTarget = target;
		fMethod = method;
		fCurrentIndex = fMethod.getIndex();
	}
	
	@Override
	public void execute() throws ModelIfException {
		if(fTarget.removeMethod(fMethod) == false){
			throw new ModelIfException(Messages.UNEXPECTED_PROBLEM_WHILE_REMOVING_ELEMENT);
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ClassOperationAddMethod(fTarget, fMethod, fCurrentIndex);
	}

}
