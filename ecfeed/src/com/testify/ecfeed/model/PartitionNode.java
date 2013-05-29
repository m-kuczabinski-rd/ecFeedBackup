package com.testify.ecfeed.model;

public class PartitionNode extends GenericNode {

	private Object fValue;

	public PartitionNode(String name, Object value) {
		super(name);
		fValue = value;
	}

	public Object getValue() {
		return fValue;
	}

	public void setValue(Object value) {
		this.fValue = value;
	}
	
	public String toString(){
		return new String(getName() + ": " + fValue);
	}
}
