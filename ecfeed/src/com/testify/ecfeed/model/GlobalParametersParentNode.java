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

	public List<GlobalParameterNode> getAvailableGlobalParameters() {
		List<GlobalParameterNode> result = getAvailableGlobalParameters(getParent());
		result.addAll(getGlobalParameters());
		return result;
	}

	private List<GlobalParameterNode> getAvailableGlobalParameters(AbstractNode parent) {
		if(parent == null){
			return new ArrayList<GlobalParameterNode>();
		}
		else if(parent instanceof GlobalParametersParentNode){
			return ((GlobalParametersParentNode)parent).getAvailableGlobalParameters();
		}else if(parent.getParent() != null){
			return getAvailableGlobalParameters(parent.getParent());
		}else{
			return new ArrayList<GlobalParameterNode>();
		}
	}

	public GlobalParameterNode getGlobalParameter(String parameterName) {
		return (GlobalParameterNode)getParameter(parameterName);
	}

}
