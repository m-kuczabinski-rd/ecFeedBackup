package com.testify.ecfeed.modeladp.operations;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;
import com.testify.ecfeed.modeladp.java.JavaUtils;

public class MethodOperationRename extends AbstractOperationRename {
	
	private MethodNode fTargetMethod;
	
	public MethodOperationRename(MethodNode target, String newName){
		super(target, newName);
		fTargetMethod = target;
	}

	@Override
	public void execute() throws ModelOperationException {
		List<String> problems = new ArrayList<String>();
		if(JavaUtils.validateNewMethodSignature(fTargetMethod.getClassNode(), fNewName, fTargetMethod.getCategoriesTypes(), problems) == false){
			throw new ModelOperationException(JavaUtils.consolidate(problems));
		}
		fTarget.setName(fNewName);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationRename(fTargetMethod, fOriginalName);
	}

}
