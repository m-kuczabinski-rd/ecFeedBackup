package com.testify.ecfeed.model;

public class ExpectedValueCategoryNode extends CategoryNode implements
		IGenericNode {
	private PartitionNode fDefaultValue;

	public ExpectedValueCategoryNode(String name, String type, Object defaultValue) {
		super(name, type);
		fDefaultValue = new PartitionNode("default value" , defaultValue);
	}

	public Object getDefaultValue() {
		return fDefaultValue.getValue();
	}

	public void setDefaultValue(Object value) {
		fDefaultValue.setValue(value);
	}

	public String toString(){
		return super.toString() + "(" + getDefaultValue() + ")";
	}
}
