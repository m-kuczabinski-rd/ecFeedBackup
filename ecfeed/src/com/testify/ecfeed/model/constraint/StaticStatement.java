package com.testify.ecfeed.model.constraint;

import java.util.Vector;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.model.PartitionNode;

public class StaticStatement extends BasicStatement {

	private boolean fValue;
	
	public StaticStatement(boolean value){
		fValue = value;
	}

	public boolean getValue(){
		return fValue;
	}
	
	@Override
	public boolean evaluate(Vector<PartitionNode> values) {
		return fValue;
	}

	@Override
	public String toString(){
		return fValue?Constants.STATIC_STATEMENT_TRUE_VALUE:Constants.STATIC_STATEMENT_FALSE_VALUE;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}

		if(obj instanceof StaticStatement == false){
			return false;
		}
		if(fValue != ((StaticStatement)obj).getValue()){
			return false;
		}
		return super.equals(obj);
	}

	public void setValue(boolean value) {
		fValue = value;
	}
}
