package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.ParametersParentNode;
import com.testify.ecfeed.model.MethodParameterNode;

public class GenericOperationRemoveParameter extends AbstractModelOperation{

	private ParametersParentNode fTarget;
	private MethodParameterNode fParameter;
	private int fOriginalIndex;

	public GenericOperationRemoveParameter(ParametersParentNode target, MethodParameterNode parameter) {
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

	protected MethodParameterNode getParameter(){
		return fParameter;
	}

	protected int getOriginalIndex(){
		return fOriginalIndex;
	}
}
