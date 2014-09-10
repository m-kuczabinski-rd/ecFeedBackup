package com.testify.ecfeed.modelif.operations;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.JavaClassUtils;
import com.testify.ecfeed.modelif.java.JavaUtils;

public class MethodOperationRename extends AbstractOperationRename {
	
	private MethodNode fTargetMethod;
	
	public MethodOperationRename(MethodNode target, String newName){
		super(target, newName);
		fTargetMethod = target;
	}

	@Override
	public void execute() throws ModelIfException {
		List<String> problems = new ArrayList<String>();
		if(JavaClassUtils.validateNewMethodSignature(fTargetMethod.getClassNode(), fNewName, fTargetMethod.getCategoriesTypes(), problems) == false){
			throw new ModelIfException(JavaUtils.consolidate(problems));
		}
		fTarget.setName(fNewName);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationRename(fTargetMethod, fOriginalName);
	}

}
