package com.testify.ecfeed.abstraction.operations;

import java.util.List;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;

public class MethodOperationAddParameter extends AbstractModelOperation {
	
	List<TestCaseNode> fRemovedTestCases;
	MethodNode fTarget;
	CategoryNode fParameter;
	private int fNewIndex;
	private int fCurrentIndex;
	
	private class ReverseOperation extends AbstractModelOperation{

		public ReverseOperation() {
			super(OperationNames.ADD_PARAMETER);
		}

		@Override
		public void execute() throws ModelIfException {
			fTarget.removeCategory(fParameter);
			fTarget.replaceTestCases(fRemovedTestCases);
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new MethodOperationAddParameter(fTarget, fParameter, fCurrentIndex);
		}
		
	}

	public MethodOperationAddParameter(MethodNode target, CategoryNode parameter, int index) {
		super(OperationNames.ADD_PARAMETER);
		fRemovedTestCases = target.getTestCases();
		fTarget = target;
		fParameter = parameter;
		fNewIndex = index;
		fCurrentIndex = parameter.getIndex();
	}

	public MethodOperationAddParameter(MethodNode target, CategoryNode parameter) {
		this(target, parameter, -1);
	}

	@Override
	public void execute() throws ModelIfException {
		if(fNewIndex == -1){
			fNewIndex = fTarget.getCategories().size();
		}
		String parameterName = fParameter.getName();
		if(fTarget.getCategory(parameterName) != null){
			throw new ModelIfException(Messages.CATEGORY_NAME_DUPLICATE_PROBLEM);
		}
		List<String> types = fTarget.getCategoriesTypes();
		types.add(fParameter.getType());
		if(fTarget.getClassNode().getMethod(parameterName, types) != null){
			throw new ModelIfException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM);
		}
		fTarget.addCategory(fParameter, fNewIndex);
		fTarget.removeTestCases();
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}

}
