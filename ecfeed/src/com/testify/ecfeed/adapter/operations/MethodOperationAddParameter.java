package com.testify.ecfeed.adapter.operations;

import java.util.List;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.TestCaseNode;

public class MethodOperationAddParameter extends AbstractModelOperation {

	List<TestCaseNode> fRemovedTestCases;
	MethodNode fTarget;
	ParameterNode fParameter;
	private int fNewIndex;
	private int fCurrentIndex;

	private class ReverseOperation extends AbstractModelOperation{

		public ReverseOperation() {
			super(OperationNames.ADD_PARAMETER);
		}

		@Override
		public void execute() throws ModelOperationException {
			fTarget.removeParameter(fParameter);
			fTarget.replaceTestCases(fRemovedTestCases);
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new MethodOperationAddParameter(fTarget, fParameter, fCurrentIndex);
		}

	}

	public MethodOperationAddParameter(MethodNode target, ParameterNode parameter, int index) {
		super(OperationNames.ADD_PARAMETER);
		fRemovedTestCases = target.getTestCases();
		fTarget = target;
		fParameter = parameter;
		fNewIndex = index;
		fCurrentIndex = parameter.getIndex();
	}

	public MethodOperationAddParameter(MethodNode target, ParameterNode parameter) {
		this(target, parameter, -1);
	}

	@Override
	public void execute() throws ModelOperationException {
		if(fNewIndex == -1){
			fNewIndex = fTarget.getParameters().size();
		}
		String parameterName = fParameter.getName();
		if(fTarget.getParameter(parameterName) != null){
			throw new ModelOperationException(Messages.PARAMETER_NAME_DUPLICATE_PROBLEM);
		}
		List<String> types = fTarget.getParametersTypes();
		types.add(fNewIndex, fParameter.getType());
		if(fTarget.getClassNode().getMethod(fTarget.getName(), types) != null){
			throw new ModelOperationException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM);
		}
		fTarget.addParameter(fParameter, fNewIndex);
		fTarget.removeTestCases();
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}

}
