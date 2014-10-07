package com.testify.ecfeed.adapter;


public interface IModelOperation {
	public void execute() throws ModelOperationException;
	public boolean modelUpdated();
	public IModelOperation reverseOperation();
	public String getName();
}
