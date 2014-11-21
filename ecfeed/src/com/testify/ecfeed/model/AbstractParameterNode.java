package com.testify.ecfeed.model;

public abstract class AbstractParameterNode extends ChoicesParentNode {

	private String fType;

	public AbstractParameterNode(String name, String type) {
		super(name);
		fType = type;
	}

	public ParametersParentNode getParametersParent(){
		return (ParametersParentNode)getParent();
	}

	@Override
	public int getIndex(){
		if(getParametersParent() == null){
			return -1;
		}
		return getParametersParent().getParameters().indexOf(this);
	}

	@Override
	public int getMaxIndex(){
		if(getParametersParent() != null){
			return getParametersParent().getParameters().size();
		}
		return -1;
	}

	public String getType() {
		return fType;
	}

	public void setType(String type) {
		fType = type;
	}

}
