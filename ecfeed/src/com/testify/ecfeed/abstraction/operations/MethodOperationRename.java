package com.testify.ecfeed.abstraction.operations;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.abstraction.java.JavaUtils;
import com.testify.ecfeed.model.MethodNode;

public class MethodOperationRename extends AbstractOperationRename {
	
	private MethodNode fTargetMethod;
	
	public MethodOperationRename(MethodNode target, String newName){
		super(target, newName);
		fTargetMethod = target;
	}

	@Override
	public void execute() throws ModelIfException {
		List<String> problems = new ArrayList<String>();
		if(JavaUtils.validateNewMethodSignature(fTargetMethod.getClassNode(), fNewName, fTargetMethod.getCategoriesTypes(), problems) == false){
			throw new ModelIfException(JavaUtils.consolidate(problems));
		}
		fTarget.setName(fNewName);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationRename(fTargetMethod, fOriginalName);
	}

}
