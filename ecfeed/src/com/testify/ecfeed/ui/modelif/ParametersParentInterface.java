package com.testify.ecfeed.ui.modelif;

import java.util.Collection;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.adapter.operations.GenericOperationAddParameter;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.ParametersParentNode;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.Messages;

public abstract class ParametersParentInterface extends AbstractNodeInterface {

	private ParametersParentNode fTarget;

	public ParametersParentInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	public void setTarget(ParametersParentNode target){
		super.setTarget(target);
		fTarget = target;
	}

	public ParametersParentNode getTarget(){
		return fTarget;
	}

	public abstract AbstractParameterNode addNewParameter();

	public boolean addParameter(AbstractParameterNode parameter, int index) {
		return execute(new GenericOperationAddParameter(fTarget, parameter, index), Messages.DIALOG_CONVERT_METHOD_PROBLEM_TITLE);
	}

	protected boolean removeParameters(Collection<? extends AbstractParameterNode> parameters){
		return super.removeChildren(parameters, Messages.DIALOG_REMOVE_PARAMETERS_PROBLEM_TITLE);
	}

	protected String generateNewParameterType() {
		return JavaUtils.supportedPrimitiveTypes()[0];
	}

	protected String generateNewParameterName() {
		int i = 0;
		String name = Constants.DEFAULT_NEW_PARAMETER_NAME + i++;
		while(fTarget.getParameter(name) != null){
			name = Constants.DEFAULT_NEW_PARAMETER_NAME + i++;
		}
		return name;
	}
}
