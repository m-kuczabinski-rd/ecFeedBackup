package com.testify.ecfeed.modelif.java.root;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.Constants;
import com.testify.ecfeed.modelif.java.common.Messages;

public class RootOperationAddNewClass implements IModelOperation {
	
	private RootNode fTarget;
	private ClassNode fAddedClass;
	private int fIndex;
	
	public RootOperationAddNewClass(RootNode target, ClassNode addedClass, int index) {
		fTarget = target;
		fAddedClass = addedClass;
		fIndex = index;
	}
	
	@Override
	public void execute() throws ModelIfException {
		String name = fAddedClass.getQualifiedName(); 
		if(name.matches(Constants.REGEX_CLASS_NODE_NAME) == false){
			throw new ModelIfException(Messages.CLASS_NAME_REGEX_PROBLEM);
		}
		if(fTarget.getClassModel(name) != null){
			throw new ModelIfException(Messages.CLASS_NAME_DUPLICATE_PROBLEM);
		}
		fTarget.addClass(fAddedClass, fIndex);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new RootOperationRemoveClass(fTarget, fAddedClass);
	}

}
