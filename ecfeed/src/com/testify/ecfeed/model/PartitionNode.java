package com.testify.ecfeed.model;

import com.testify.ecfeed.constants.Constants;

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
	
	public String getValueString(){
		if(fValue == null) return Constants.NULL_VALUE_STRING_REPRESENTATION;
		if(fValue instanceof Character){
			if((Character)fValue != 0) return " \\" + (int)((char)fValue ) + " ['" + fValue + "']";
			return "\\0";
		}
		return String.valueOf(fValue);
	}
	
	public String toString(){
		return getName() + ": " + getValueString();
	}
	
	@Override 
	public boolean equals(Object obj){
		if(obj instanceof PartitionNode != true){
			return false;
		}
		PartitionNode partition = (PartitionNode)obj;
		Object partitionValue = partition.getValue();
		if(fValue != null){
			if(!fValue.equals(partitionValue)){
				return false;
			}
		}
		else{
			if(partitionValue != null){
				return false;
			}
		}
		return super.equals(partition);
	}
}