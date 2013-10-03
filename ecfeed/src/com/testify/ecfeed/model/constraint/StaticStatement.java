package com.testify.ecfeed.model.constraint;

import java.util.List;

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
	public boolean evaluate(List<PartitionNode> values) {
		return fValue;
	}

	@Override
	public String toString(){
		return fValue?Constants.STATIC_STATEMENT_TRUE_VALUE:Constants.STATIC_STATEMENT_FALSE_VALUE;
	}
	
	public void setValue(boolean value) {
		fValue = value;
	}
}
