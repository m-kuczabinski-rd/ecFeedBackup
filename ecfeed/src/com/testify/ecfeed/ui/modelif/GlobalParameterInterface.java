package com.testify.ecfeed.ui.modelif;

import java.util.List;

import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.operations.GlobalParameterOperationSetType;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.ui.common.EclipseTypeAdapterProvider;
import com.testify.ecfeed.ui.common.Messages;

public class GlobalParameterInterface extends AbstractParameterInterface {

	private ITypeAdapterProvider fAdapterProvider;

	public GlobalParameterInterface(IModelUpdateContext updateContext) {
		super(updateContext);
		fAdapterProvider = new EclipseTypeAdapterProvider();
	}

	public List<MethodParameterNode> getLinkers(){
		return getTarget().getLinkers();
	}

	@Override
	public boolean setType(String newType) {
		if(newType.equals(getTarget().getType())){
			return false;
		}
		return execute(new GlobalParameterOperationSetType(getTarget(), newType, fAdapterProvider), Messages.DIALOG_RENAME_PAREMETER_PROBLEM_TITLE);
	}

	@Override
	protected GlobalParameterNode getTarget(){
		return (GlobalParameterNode)super.getTarget();
	}
}
