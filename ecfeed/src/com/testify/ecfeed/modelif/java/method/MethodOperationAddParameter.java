package com.testify.ecfeed.modelif.java.method;

import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.common.Messages;

public class MethodOperationAddParameter implements IModelOperation {
	
	List<TestCaseNode> fRemovedTestCases;
	MethodNode fTarget;
	CategoryNode fParameter;
	
	private class ReverseOperation implements IModelOperation{

		@Override
		public void execute() throws ModelIfException {
			fTarget.removeCategory(fParameter);
			fTarget.replaceTestCases(fRemovedTestCases);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new MethodOperationAddParameter(fTarget, fParameter);
		}
		
	}

	public MethodOperationAddParameter(MethodNode target, CategoryNode parameter) {
		fRemovedTestCases = target.getTestCases();
		fTarget = target;
		fParameter = parameter;
	}

	@Override
	public void execute() throws ModelIfException {
		String parameterName = fParameter.getName();
		if(fTarget.getCategory(parameterName) != null){
			throw new ModelIfException(Messages.CATEGORY_NAME_DUPLICATE_PROBLEM);
		}
		List<String> types = fTarget.getCategoriesTypes();
		types.add(fParameter.getType());
		if(fTarget.getClassNode().getMethod(parameterName, types) != null){
			throw new ModelIfException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM);
		}
		fTarget.addCategory(fParameter);
		fTarget.removeTestCases();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}

}
