package com.testify.ecfeed.modeladp;


public interface IModelOperation {
	public void execute() throws ModelOperationException;
	public boolean modelUpdated();
	public IModelOperation reverseOperation();
	public String getName();
}
