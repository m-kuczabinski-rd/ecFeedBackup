package com.testify.ecfeed.model;

import java.util.ArrayList;
import java.util.List;

public abstract class ParameterKeeperNode extends AbstractNode {

	private List<ParameterNode> fParameters;

	public ParameterKeeperNode(String name) {
		super(name);
		fParameters = new ArrayList<ParameterNode>();
	}

	public void addParameter(ParameterNode parameter){
		addParameter(parameter, fParameters.size());
	}

	public void addParameter(ParameterNode parameter, int index) {
		fParameters.add(index, parameter);
		parameter.setParent(this);
	}

	public List<ParameterNode> getParameters(){
		return fParameters;
	}

	public ParameterNode getParameter(String parameterName) {
		for(ParameterNode parameter : fParameters){
			if(parameter.getName().equals(parameterName)){
				return parameter;
			}
		}
		return null;
	}

	public List<ParameterNode> getParameters(boolean expected) {
		ArrayList<ParameterNode> parameters = new ArrayList<>();
		for(ParameterNode parameter : fParameters){
			if(parameter.isExpected() == expected){
				parameters.add(parameter);
			}
		}
		return parameters;
	}

	public List<String> getParametersTypes() {
		List<String> types = new ArrayList<String>();
		for(ParameterNode parameter : fParameters){
			types.add(parameter.getType());
		}
		return types;
	}

	public List<String> getParametersShortTypes() {
		List<String> types = new ArrayList<String>();
		for(ParameterNode parameter : fParameters){
			types.add(parameter.getShortType());
		}
		return types;
	}

	public List<String> getParametersNames() {
		List<String> names = new ArrayList<String>();
		for(ParameterNode parameter : fParameters){
			names.add(parameter.getName());
		}
		return names;
	}

	public ArrayList<String> getParametersNames(boolean expected) {
		ArrayList<String> names = new ArrayList<String>();
		for(ParameterNode parameter : fParameters){
			if(parameter.isExpected() == expected){
				names.add(parameter.getName());
			}
		}
		return names;
	}

	public boolean removeParameter(ParameterNode parameter){
		parameter.setParent(null);
		return fParameters.remove(parameter);
	}

	public void replaceParameters(List<ParameterNode> parameters) {
		fParameters.clear();
		fParameters.addAll(parameters);
	}

	@Override
	public boolean compare(AbstractNode node){
		if(node instanceof ParameterKeeperNode == false){
			return false;
		}
		ParameterKeeperNode comparedKeeper = (ParameterKeeperNode)node;
		if(getParameters().size() != comparedKeeper.getParameters().size()){
			return false;
		}
		for(int i = 0; i < getParameters().size(); ++i){
			if(getParameters().get(i).compare(comparedKeeper.getParameters().get(i)) == false){
				return false;
			}
		}
		return true;
	}
}
