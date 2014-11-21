package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.ParametersParentNode;
import com.testify.ecfeed.model.MethodParameterNode;

public class GenericOperationAddParameter extends AbstractModelOperation {

	private ParametersParentNode fTarget;
	private MethodParameterNode fParameter;
	private int fNewIndex;

	protected class ReverseOperation extends AbstractModelOperation{

		private int fOriginalIndex;
		private MethodParameterNode fReversedParameter;
		private ParametersParentNode fReversedTarget;

		public ReverseOperation(ParametersParentNode target, MethodParameterNode parameter) {
			super("reverse " + OperationNames.ADD_PARAMETER);
			fReversedTarget = target;
			fReversedParameter = parameter;
		}

		@Override
		public void execute() throws ModelOperationException {
			fOriginalIndex = fReversedParameter.getIndex();
			fReversedTarget.removeParameter(fReversedParameter);
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new GenericOperationAddParameter(fReversedTarget, fReversedParameter, fOriginalIndex);
		}
	}

	public GenericOperationAddParameter(ParametersParentNode target, MethodParameterNode parameter, int index) {
		super(OperationNames.ADD_PARAMETER);
		fTarget = target;
		fParameter = parameter;
		fNewIndex = (index == -1)? target.getParameters().size() : index;
	}

	public GenericOperationAddParameter(ParametersParentNode target, MethodParameterNode parameter) {
		this(target, parameter, -1);
	}


	@Override
	public void execute() throws ModelOperationException {
		String parameterName = fParameter.getName();
		if(fNewIndex < 0){
			throw new ModelOperationException(Messages.NEGATIVE_INDEX_PROBLEM);
		}
		if(fNewIndex > fTarget.getParameters().size()){
			throw new ModelOperationException(Messages.TOO_HIGH_INDEX_PROBLEM);
		}
		if(fTarget.getParameter(parameterName) != null){
			throw new ModelOperationException(Messages.CATEGORY_NAME_DUPLICATE_PROBLEM);
		}
		fTarget.addParameter(fParameter, fNewIndex);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation(fTarget, fParameter);
	}

}
