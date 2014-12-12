package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.ParametersParentNode;

public class GenericOperationRemoveParameter extends AbstractModelOperation{

	private ParametersParentNode fTarget;
	private AbstractParameterNode fParameter;
	private int fOriginalIndex;

	public GenericOperationRemoveParameter(ParametersParentNode target, AbstractParameterNode parameter) {
		super(OperationNames.REMOVE_METHOD_PARAMETER);
		fTarget = target;
		fParameter = parameter;
	}

	@Override
	public void execute() throws ModelOperationException {
		fOriginalIndex = fTarget.getParameters().indexOf(fParameter);
		fTarget.removeParameter(fParameter);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new GenericOperationAddParameter(fTarget, fParameter, fOriginalIndex);
	}

	protected ParametersParentNode getTarget(){
		return fTarget;
	}

	protected AbstractParameterNode getParameter(){
		return fParameter;
	}

	protected int getOriginalIndex(){
		return fOriginalIndex;
	}
}
