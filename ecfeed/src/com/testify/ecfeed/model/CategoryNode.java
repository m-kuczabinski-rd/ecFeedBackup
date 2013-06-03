package com.testify.ecfeed.model;

public class CategoryNode extends GenericNode {
	
	private final String fType;	
	
	public CategoryNode(String name, String type) {
		super(name);
		fType = type;
	}

	public String getType() {
		return fType;
	}

	public void addPartition(PartitionNode partition) {
		super.addChild(partition);
	}

	public Object getValueFromString(String valueString){
		return getValueFromString(valueString, fType);
	}

	private static Object getValueFromString(String valueString, String type){
		try{
			switch(type){
			case "boolean":
				return Boolean.valueOf(valueString).booleanValue();
			case "byte":
				return Byte.valueOf(valueString).byteValue();
			case "char":
				return(valueString.charAt(0));
			case "double":
				return Double.valueOf(valueString).doubleValue();
			case "float":
				return Float.valueOf(valueString).floatValue();
			case "int":
				return Integer.valueOf(valueString).intValue();
			case "long":
				return Long.valueOf(valueString).longValue();
			case "short":
				return Short.valueOf(valueString).shortValue();
			case "String":
				return valueString;
			default:
				return null;
			}
		}catch(NumberFormatException|IndexOutOfBoundsException e){
			return null;
		}
	}

	public boolean isStringValueValid(String valueString){
		if(fType == "String"){
			return true;
		}
		return (getValueFromString(valueString) != null);
	}

	public String toString(){
		return new String(getName() + ": " + getType());
	}

	public boolean removeChild(PartitionNode partition) {
		return super.removeChild(partition);
	}
}
