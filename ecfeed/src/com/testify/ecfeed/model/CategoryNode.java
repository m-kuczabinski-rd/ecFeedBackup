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

	public static Object getValueFromString(String valueString, String type){
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

	public static boolean isStringValueValid(String valueString, String type){
		if(type == "String"){
			return true;
		}
		return (getValueFromString(valueString, type) != null);
	}

	public String toString(){
		return new String(getName() + ": " + getType());
	}

	public boolean removeChild(PartitionNode partition) {
		return super.removeChild(partition);
	}

	//	public Object getValueFromString(String valueString){
	//		try{
	//			switch(fType){
	//			case Signature.SIG_BOOLEAN:
	//				return Boolean.valueOf(valueString).booleanValue();
	//			case Signature.SIG_BYTE:
	//				return Byte.valueOf(valueString).byteValue();
	//			case Signature.SIG_CHAR:
	//				return(valueString.charAt(0));
	//			case Signature.SIG_DOUBLE:
	//				return Double.valueOf(valueString).doubleValue();
	//			case Signature.SIG_FLOAT:
	//				return Float.valueOf(valueString).floatValue();
	//			case Signature.SIG_INT:
	//				return Integer.valueOf(valueString).intValue();
	//			case Signature.SIG_LONG:
	//				return Long.valueOf(valueString).longValue();
	//			case Signature.SIG_SHORT:
	//				return Short.valueOf(valueString).shortValue();
	//			case "QString;":
	//				return valueString;
	//			default:
	//				return null;
	//			}
	//		}catch(NumberFormatException|IndexOutOfBoundsException e){
	//			return null;
	//		}
	//	}
	//
	//	public boolean isStringValueValid(String valueString){
	//		if(fType == "QString;"){
	//			return true;
	//		}
	//		return (getValueFromString(valueString) != null);
	//	}
	
//		private String getTypeName(String typeSignature) {
//			switch(typeSignature){
//			case Signature.SIG_BOOLEAN:
//				return "boolean";
//			case Signature.SIG_BYTE:
//				return "byte";
//			case Signature.SIG_CHAR:
//				return "char";
//			case Signature.SIG_DOUBLE:
//				return "double";
//			case Signature.SIG_FLOAT:
//				return "float";
//			case Signature.SIG_INT:
//				return "int";
//			case Signature.SIG_LONG:
//				return "long";
//			case Signature.SIG_SHORT:
//				return "short";
//			case "QString;":
//				return "String";
//			default:
//				return "unsupported";
//			}
//		}
}
