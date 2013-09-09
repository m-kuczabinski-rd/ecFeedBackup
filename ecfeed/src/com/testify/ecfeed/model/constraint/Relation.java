package com.testify.ecfeed.model.constraint;

import com.testify.ecfeed.constants.Constants;

public enum Relation{
	LESS(Constants.RELATION_LESS), 
	LESS_EQUAL(Constants.RELATION_LESS_EQUAL), 
	EQUAL(Constants.RELATION_EQUAL), 
	GREATER_EQUAL(Constants.RELATION_GREATER_EQUAL), 
	GREATER(Constants.RELATION_GREATER), 
	NOT(Constants.RELATION_NOT);
	
	String fValue;
	
	private Relation(String value){
		fValue = value;
	}
	
	public String toString(){
		return fValue; 
	}
	
	public static Relation getRelation(String text){
		switch(text){
		case Constants.RELATION_LESS:
			return LESS;
		case Constants.RELATION_LESS_EQUAL:
			return LESS_EQUAL;
		case Constants.RELATION_EQUAL:
			return EQUAL;
		case Constants.RELATION_GREATER_EQUAL:
			return GREATER_EQUAL;
		case Constants.RELATION_GREATER:
			return GREATER;
		case Constants.RELATION_NOT:
			return NOT;
		}
		return NOT;
	}
}