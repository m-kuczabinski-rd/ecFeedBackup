package com.testify.ecfeed.ui.modelif;

import java.util.List;

import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.MethodParameterNode;

public class GlobalParameterInterface extends AbstractParameterInterface {

	private GlobalParameterNode fTarget;

	public GlobalParameterInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	public void setTarget(GlobalParameterNode target){
		fTarget = target;
		super.setTarget(target);
	}

	public List<MethodParameterNode> getLinkers(){
		return fTarget.getLinkers();
	}
}
