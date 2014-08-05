package com.testify.ecfeed.modelif.java.common;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public abstract class AbstractBulkOperation implements IModelOperation{

	List<IModelOperation> fOperations;
	
	public AbstractBulkOperation() {
		fOperations = new ArrayList<IModelOperation>();
	}
	
	protected void addOperation(IModelOperation operation) {
		fOperations.add(operation);
	}
	
	@Override
	public void execute() throws ModelIfException {
		List<String> errors = new ArrayList<String>();
		for(IModelOperation operation : fOperations){
			try{
				operation.execute();
			}catch(ModelIfException e){
				errors.add(e.getMessage());
			}
		}
		if(errors.size() > 0){
			String message = Messages.PROBLEM_WITH_BULK_OPERATION;
			for(String error : errors){
				message += "\n" + error;
			}
			throw new ModelIfException(message);
		}

	}

}
