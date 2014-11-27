package com.testify.ecfeed.ui.modelif;

import java.util.Collection;

import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.GlobalParametersParentNode;
import com.testify.ecfeed.ui.common.EclipseModelBuilder;

public class GlobalParametersParentInterface extends ParametersParentInterface {

	private GlobalParametersParentNode fTarget;

	public GlobalParametersParentInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	public void setTarget(GlobalParametersParentNode target){
		fTarget = target;
		super.setTarget(target);
	}

	@Override
	public AbstractParameterNode addNewParameter() {
		EclipseModelBuilder modelBuilder = new EclipseModelBuilder();
		GlobalParameterNode parameter = new GlobalParameterNode(generateNewParameterName(), generateNewParameterType());
		parameter.addChoices(modelBuilder.defaultChoices(parameter.getType()));
		if(addParameter(parameter, fTarget.getParameters().size())){
			return parameter;
		}
		return null;
	}

	public boolean removeGlobalParameters(Collection<GlobalParameterNode> parameters){
		return super.removeParameters(parameters);
	}
}
