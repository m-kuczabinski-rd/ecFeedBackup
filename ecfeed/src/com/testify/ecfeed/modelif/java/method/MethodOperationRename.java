package com.testify.ecfeed.modelif.java.method;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.JavaClassUtils;
import com.testify.ecfeed.modelif.java.JavaUtils;

public class MethodOperationRename implements IModelOperation {
	
	private String fNewName;
	private MethodNode fTarget;

	public MethodOperationRename(MethodNode target, String newName){
		fTarget = target;
		fNewName = newName;
	}

	@Override
	public void execute() throws ModelIfException {
		List<String> problems = new ArrayList<String>();
		if(JavaClassUtils.validateNewMethodSignature(fTarget.getClassNode(), fNewName, fTarget.getCategoriesTypes(), problems) == false){
			throw new ModelIfException(JavaUtils.consolidate(problems));
		}
		fTarget.setName(fNewName);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationRename(fTarget, fNewName);
	}

}
