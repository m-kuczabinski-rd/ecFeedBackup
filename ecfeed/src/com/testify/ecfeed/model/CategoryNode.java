package com.testify.ecfeed.model;

import java.util.Vector;

import org.eclipse.jdt.core.Signature;

public class CategoryNode extends GenericNode {
	
	private final String fTypeSignature;	
	
	public CategoryNode(String name, String typeSignature) {
		super(name);
		fTypeSignature = typeSignature;
	}

	public String getTypeSignature() {
		return fTypeSignature;
	}

	public void addPartition(PartitionNode partition) {
		super.addChild(partition);
	}
	
	public Object getValueFromString(String valueString){
		try{
			switch(fTypeSignature){
			case Signature.SIG_BOOLEAN:
				return Boolean.valueOf(valueString).booleanValue();
			case Signature.SIG_BYTE:
				return Byte.valueOf(valueString).byteValue();
			case Signature.SIG_CHAR:
				return(valueString.charAt(0));
			case Signature.SIG_DOUBLE:
				return Double.valueOf(valueString).doubleValue();
			case Signature.SIG_FLOAT:
				return Float.valueOf(valueString).floatValue();
			case Signature.SIG_INT:
				return Integer.valueOf(valueString).intValue();
			case Signature.SIG_LONG:
				return Long.valueOf(valueString).longValue();
			case Signature.SIG_SHORT:
				return Short.valueOf(valueString).shortValue();
			case "QString;":
				return valueString;
			default:
				return null;
			}
		}catch(NumberFormatException|IndexOutOfBoundsException e){
			return null;
		}
	}

	public boolean isStringValueValid(String valueString){
		if(fTypeSignature == "QString;"){
			return true;
		}
		return (getValueFromString(valueString) != null);
	}

	public String getTypeName() {
		switch(fTypeSignature){
		case Signature.SIG_BOOLEAN:
			return "boolean";
		case Signature.SIG_BYTE:
			return "byte";
		case Signature.SIG_CHAR:
			return "char";
		case Signature.SIG_DOUBLE:
			return "double";
		case Signature.SIG_FLOAT:
			return "float";
		case Signature.SIG_INT:
			return "int";
		case Signature.SIG_LONG:
			return "long";
		case Signature.SIG_SHORT:
			return "short";
		case "QString;":
			return "String";
		default:
			return "unsupported";
		}
	}
	
	public String toString(){
		return new String(getName() + ": " + getTypeName());
	}

	public boolean removeChild(PartitionNode partition) {
		return super.removeChild(partition);
	}
}
