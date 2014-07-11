package com.testify.ecfeed.gal;


public interface IModelOperation {
	public void execute() throws GalException;
	public IModelOperation reverseOperation();
}
