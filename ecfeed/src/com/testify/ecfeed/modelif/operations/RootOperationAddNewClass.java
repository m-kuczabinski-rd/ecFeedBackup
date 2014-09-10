package com.testify.ecfeed.modelif.operations;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.Constants;

public class RootOperationAddNewClass implements IModelOperation {
	
	private RootNode fTarget;
	private ClassNode fAddedClass;
	private int fIndex;
	
	public RootOperationAddNewClass(RootNode target, ClassNode addedClass, int index) {
		fTarget = target;
		fAddedClass = addedClass;
		fIndex = index;
	}
	
	public RootOperationAddNewClass(RootNode target, ClassNode addedClass) {
		this(target, addedClass, -1);
	}
	
	@Override
	public void execute() throws ModelIfException {
		String name = fAddedClass.getQualifiedName(); 
		if(fIndex == -1){
			fIndex = fTarget.getClasses().size();
		}
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
