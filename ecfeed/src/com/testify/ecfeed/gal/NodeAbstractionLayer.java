package com.testify.ecfeed.gal;

public class NodeAbstractionLayer {
	
	private ModelOperationManager fModelOperation;

	public NodeAbstractionLayer(ModelOperationManager modelAbstraction){
		fModelOperation = modelAbstraction;
	}
	
	protected void execute(IModelOperation operation) throws GalException{
		operation.execute();
		fModelOperation.updateHistory(operation);
	}
}
