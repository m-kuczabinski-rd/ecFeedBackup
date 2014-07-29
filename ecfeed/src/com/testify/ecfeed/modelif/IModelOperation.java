package com.testify.ecfeed.modelif;


public interface IModelOperation {
	public void execute() throws GalException;
	public IModelOperation reverseOperation();
}
