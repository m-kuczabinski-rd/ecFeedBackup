package com.testify.ecfeed.modeladp.operations;

import com.testify.ecfeed.modeladp.IModelOperation;

public abstract class AbstractModelOperation implements IModelOperation {

	private boolean fModelUpdated;
	private String fName;

	public AbstractModelOperation(String name){
		fName = name;
	}
	
	@Override
	public boolean modelUpdated() {
		return fModelUpdated;
	}

	protected void markModelUpdated(){
		fModelUpdated = true;
	}
	
	@Override
	public String getName(){
		return fName;
	}
}
