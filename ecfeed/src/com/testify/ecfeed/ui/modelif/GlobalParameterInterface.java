package com.testify.ecfeed.ui.modelif;

import java.util.List;

import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.operations.GlobalParameterOperationSetType;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.ui.common.EclipseTypeAdapterProvider;
import com.testify.ecfeed.ui.common.Messages;

public class GlobalParameterInterface extends AbstractParameterInterface {

	private GlobalParameterNode fTarget;
	private ITypeAdapterProvider fAdapterProvider;

	public GlobalParameterInterface(IModelUpdateContext updateContext) {
		super(updateContext);
		fAdapterProvider = new EclipseTypeAdapterProvider();
	}

	@Override
	public void setTarget(AbstractParameterNode target){
		fTarget = (GlobalParameterNode)target;
		super.setTarget(target);
	}

	public List<MethodParameterNode> getLinkers(){
		return fTarget.getLinkers();
	}

	@Override
	public boolean setType(String newType) {
		if(newType.equals(fTarget.getType())){
			return false;
		}
		return execute(new GlobalParameterOperationSetType(fTarget, newType, fAdapterProvider), Messages.DIALOG_RENAME_PAREMETER_PROBLEM_TITLE);
	}
}
