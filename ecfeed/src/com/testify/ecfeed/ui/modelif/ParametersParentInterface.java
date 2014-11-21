package com.testify.ecfeed.ui.modelif;

import java.util.Collection;
import java.util.List;

import com.testify.ecfeed.adapter.operations.GenericOperationAddParameter;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.ParametersParentNode;
import com.testify.ecfeed.ui.common.EclipseModelBuilder;
import com.testify.ecfeed.ui.common.Messages;

public class ParametersParentInterface extends AbstractNodeInterface {

	private ParametersParentNode fTarget;

	public ParametersParentInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	public void setTarget(ParametersParentNode target){
		fTarget = target;
	}

	public ParametersParentNode getTarget(){
		return fTarget;
	}

	public ParameterNode addNewParameter() {
		EclipseModelBuilder modelBuilder = new EclipseModelBuilder();
		String name = generateNewParameterName();
		String type = generateNewParameterType();
		String defaultValue = modelBuilder.getDefaultExpectedValue(type);
		ParameterNode parameter = new ParameterNode(name, type, defaultValue, false);
		List<ChoiceNode> defaultChoices = modelBuilder.defaultChoices(type);
		parameter.addChoices(defaultChoices);
		if(addParameter(parameter, fTarget.getParameters().size())){
			return parameter;
		}
		return null;
	}

	public boolean addParameter(ParameterNode parameter, int index) {
		return execute(new GenericOperationAddParameter(fTarget, parameter, index), Messages.DIALOG_CONVERT_METHOD_PROBLEM_TITLE);
	}

	public boolean removeParameters(Collection<ParameterNode> parameters, IModelUpdateContext context){
		return super.removeChildren(parameters, Messages.DIALOG_REMOVE_PARAMETERS_PROBLEM_TITLE);
	}

	protected String generateNewParameterType() {
		return null;
	}

	protected String generateNewParameterName() {
		return null;
	}

}
