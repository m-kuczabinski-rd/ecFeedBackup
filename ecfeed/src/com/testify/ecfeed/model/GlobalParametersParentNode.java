package com.testify.ecfeed.model;

import java.util.ArrayList;
import java.util.List;

public abstract class GlobalParametersParentNode extends ParametersParentNode {

	public GlobalParametersParentNode(String name) {
		super(name);
	}

	public List<GlobalParameterNode> getGlobalParameters() {
		List<GlobalParameterNode> result = new ArrayList<>();
		for(AbstractParameterNode parameter : getParameters()){
			result.add((GlobalParameterNode)parameter);
		}
		return result;
	}

	public GlobalParameterNode getGlobalParameter(String parameterName) {
		return (GlobalParameterNode)getParameter(parameterName);
	}

}
