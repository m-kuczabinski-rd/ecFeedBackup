package com.testify.ecfeed.abstraction;


public interface IModelOperation {
	public void execute() throws ModelIfException;
	public IModelOperation reverseOperation();
}
