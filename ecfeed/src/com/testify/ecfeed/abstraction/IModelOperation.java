package com.testify.ecfeed.abstraction;


public interface IModelOperation {
	public void execute() throws ModelIfException;
	public boolean modelUpdated();
	public IModelOperation reverseOperation();
	public String getName();
}
