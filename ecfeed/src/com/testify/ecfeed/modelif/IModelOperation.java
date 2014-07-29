package com.testify.ecfeed.modelif;


public interface IModelOperation {
	public void execute() throws ModelIfException;
	public IModelOperation reverseOperation();
}
