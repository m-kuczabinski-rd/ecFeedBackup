package com.testify.ecfeed.modeladp.operations;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;
import com.testify.ecfeed.modeladp.java.Constants;
import com.testify.ecfeed.modeladp.java.JavaUtils;

public class ClassOperationRename extends AbstractOperationRename {

	public ClassOperationRename(GenericNode target, String newName) {
		super(target, newName);
	}

	@Override
	public void execute() throws ModelOperationException {
		if(fNewName.matches(Constants.REGEX_CLASS_NODE_NAME) == false){
			throw new ModelOperationException(Messages.CLASS_NAME_REGEX_PROBLEM);
		}
		
		for(String token : fNewName.split("\\.")){
			if(JavaUtils.isJavaKeyword(token)){
				throw new ModelOperationException(Messages.CLASS_NAME_CONTAINS_KEYWORD_PROBLEM);
			}
		}
		
		ClassNode target = (ClassNode)fTarget;
		if(target.getRoot().getClassModel(fNewName) != null){
			throw new ModelOperationException(Messages.CLASS_NAME_DUPLICATE_PROBLEM);
		}
		target.setName(fNewName);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ClassOperationRename(fTarget, fOriginalName);
	}

}
