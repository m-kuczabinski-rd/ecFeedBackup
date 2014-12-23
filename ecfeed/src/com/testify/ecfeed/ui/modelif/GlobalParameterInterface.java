package com.testify.ecfeed.ui.modelif;

import java.util.List;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.operations.GlobalParameterOperationSetType;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.MethodParameterNode;

public class GlobalParameterInterface extends AbstractParameterInterface {

	public GlobalParameterInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	public List<MethodParameterNode> getLinkers(){
		return getTarget().getLinkers();
	}

	@Override
	protected GlobalParameterNode getTarget(){
		return (GlobalParameterNode)super.getTarget();
	}

	@Override
	protected IModelOperation setTypeOperation(String type) {
		return new GlobalParameterOperationSetType(getTarget(), type, getAdapterProvider());
	}
}
