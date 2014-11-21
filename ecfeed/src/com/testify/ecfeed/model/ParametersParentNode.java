package com.testify.ecfeed.model;

import java.util.ArrayList;
import java.util.List;

public abstract class ParametersParentNode extends AbstractNode {

	private List<MethodParameterNode> fParameters;

	public ParametersParentNode(String name) {
		super(name);
		fParameters = new ArrayList<MethodParameterNode>();
	}

	public void addParameter(MethodParameterNode parameter){
		addParameter(parameter, fParameters.size());
	}

	public void addParameter(MethodParameterNode parameter, int index) {
		fParameters.add(index, parameter);
		parameter.setParent(this);
	}

	public List<MethodParameterNode> getParameters(){
		return fParameters;
	}

	public MethodParameterNode getParameter(String parameterName) {
		for(MethodParameterNode parameter : fParameters){
			if(parameter.getName().equals(parameterName)){
				return parameter;
			}
		}
		return null;
	}

	public List<MethodParameterNode> getParameters(boolean expected) {
		ArrayList<MethodParameterNode> parameters = new ArrayList<>();
		for(MethodParameterNode parameter : fParameters){
			if(parameter.isExpected() == expected){
				parameters.add(parameter);
			}
		}
		return parameters;
	}

	public List<String> getParametersTypes() {
		List<String> types = new ArrayList<String>();
		for(MethodParameterNode parameter : fParameters){
			types.add(parameter.getType());
		}
		return types;
	}

	public List<String> getParametersNames() {
		List<String> names = new ArrayList<String>();
		for(MethodParameterNode parameter : fParameters){
			names.add(parameter.getName());
		}
		return names;
	}

	public ArrayList<String> getParametersNames(boolean expected) {
		ArrayList<String> names = new ArrayList<String>();
		for(MethodParameterNode parameter : fParameters){
			if(parameter.isExpected() == expected){
				names.add(parameter.getName());
			}
		}
		return names;
	}

	public boolean removeParameter(MethodParameterNode parameter){
		parameter.setParent(null);
		return fParameters.remove(parameter);
	}

	public void replaceParameters(List<MethodParameterNode> parameters) {
		fParameters.clear();
		fParameters.addAll(parameters);
	}

	@Override
	public boolean compare(AbstractNode node){
		if(node instanceof ParametersParentNode == false){
			return false;
		}
		ParametersParentNode comparedParent = (ParametersParentNode)node;
		if(getParameters().size() != comparedParent.getParameters().size()){
			return false;
		}
		for(int i = 0; i < getParameters().size(); ++i){
			if(getParameters().get(i).compare(comparedParent.getParameters().get(i)) == false){
				return false;
			}
		}
		return super.compare(node);
	}
}
