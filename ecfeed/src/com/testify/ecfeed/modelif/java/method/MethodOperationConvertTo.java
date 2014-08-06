package com.testify.ecfeed.modelif.java.method;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.common.Messages;

public class MethodOperationConvertTo implements IModelOperation {
	
	private MethodNode fTarget;
	private MethodNode fSource;

	public MethodOperationConvertTo(MethodNode target, MethodNode source) {
		fTarget = target;
		fSource = source;
	}

	@Override
	public void execute() throws ModelIfException {
		if(fTarget.getClassNode().getMethod(fSource.getName(), fSource.getCategoriesTypes()) != null){
			throw new ModelIfException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM);
		}
		if(fTarget.getCategoriesTypes().equals(fSource.getCategoriesTypes()) == false){
			throw new ModelIfException(Messages.METHODS_INCOMPATIBLE_PROBLEM);
		}

		fTarget.setName(fSource.getName());
		for(int i = 0; i < fTarget.getCategories().size(); i++){
			CategoryNode targetCategory = fTarget.getCategories().get(i);
			CategoryNode sourceCategory = fSource.getCategories().get(i);
			
			targetCategory.setName(sourceCategory.getName());
			targetCategory.setExpected(sourceCategory.isExpected());
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationConvertTo(fSource, fTarget);
	}

}
