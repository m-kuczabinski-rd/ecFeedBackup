package com.testify.ecfeed.model;

import org.eclipse.jdt.core.Signature;

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
	
	public void setValueFromString(String valueString){
		switch(getTypeSignature()){
		case Signature.SIG_BOOLEAN:
			setValue(Boolean.valueOf(valueString).booleanValue());
			break;
		case Signature.SIG_BYTE:
			setValue(Byte.valueOf(valueString).byteValue());
			break;
		case Signature.SIG_CHAR:
			if(valueString.length() > 0){
				setValue(valueString.charAt(0));
			}
			else{
				setValue(null);
			}
			break;
		case Signature.SIG_DOUBLE:
			setValue(Double.valueOf(valueString).doubleValue());
			break;
		case Signature.SIG_FLOAT:
			setValue(Float.valueOf(valueString).floatValue());
			break;
		case Signature.SIG_INT:
			setValue(Integer.valueOf(valueString).intValue());
			break;
		case Signature.SIG_LONG:
			setValue(Long.valueOf(valueString).longValue());
			break;
		case Signature.SIG_SHORT:
			setValue(Short.valueOf(valueString).shortValue());
			break;
		case "QString;":
			setValue(valueString);
			break;
		default:
			break;
		}
	}
	
	private String getTypeSignature(){
		return ((CategoryNode)getParent()).getTypeSignature();
	}
}
