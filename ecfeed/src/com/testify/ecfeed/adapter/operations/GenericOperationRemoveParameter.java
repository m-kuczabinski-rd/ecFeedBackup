package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.ParametersParentNode;
import com.testify.ecfeed.model.ParameterNode;

public class GenericOperationRemoveParameter extends AbstractModelOperation{

	private ParametersParentNode fTarget;
	private ParameterNode fParameter;
	private int fOriginalIndex;

	public GenericOperationRemoveParameter(ParametersParentNode target, ParameterNode parameter) {
		super(OperationNames.REMOVE_PARAMETER);
		fTarget = target;
		fParameter = parameter;
	}

	@Override
	public void execute() throws ModelOperationException {
		fOriginalIndex = fTarget.getParameters().indexOf(fParameter);
		fTarget.removeParameter(fParameter);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new GenericOperationAddParameter(fTarget, fParameter, fOriginalIndex);
	}

	protected ParametersParentNode getTarget(){
		return fTarget;
	}

	protected ParameterNode getParameter(){
		return fParameter;
	}

	protected int getOriginalIndex(){
		return fOriginalIndex;
	}
}
