package com.testify.ecfeed.modeladp;

public enum ImplementationStatus {
	IMPLEMENTED, PARTIALLY_IMPLEMENTED, NOT_IMPLEMENTED, IRRELEVANT;
	
	public String toString(){
		switch(this){
		case IMPLEMENTED: return "implemented";
		case PARTIALLY_IMPLEMENTED: return "partially implemented";
		case NOT_IMPLEMENTED: return "not implemented";
		case IRRELEVANT: return "irrelevant implementation status";
		}
		return "";
	}
}
