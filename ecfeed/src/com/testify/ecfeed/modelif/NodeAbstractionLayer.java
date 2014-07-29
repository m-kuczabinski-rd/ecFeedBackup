package com.testify.ecfeed.modelif;


public class NodeAbstractionLayer {
	
	private ModelOperationManager fModelOperationManager;

	public NodeAbstractionLayer(ModelOperationManager modelOperationManager){
		fModelOperationManager = modelOperationManager;
	}
	
	protected void execute(IModelOperation operation) throws ModelIfException{
		operation.execute();
		fModelOperationManager.updateHistory(operation);
	}
	
}
