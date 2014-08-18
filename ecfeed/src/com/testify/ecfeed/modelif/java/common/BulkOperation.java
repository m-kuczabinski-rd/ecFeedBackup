package com.testify.ecfeed.modelif.java.common;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class BulkOperation implements IModelOperation{

	List<IModelOperation> fOperations;
	private boolean fExecuteAll;
	
	public BulkOperation(boolean executeAll) {
		fOperations = new ArrayList<IModelOperation>();
		fExecuteAll = executeAll;
	}
	
	public BulkOperation(List<IModelOperation> operations) {
		fOperations = operations;
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
				if(fExecuteAll == false){
					break;
				}
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

	@Override
	public IModelOperation reverseOperation(){
		return new BulkOperation(reverseOperations());
	}
	
	
	protected List<IModelOperation> operations(){
		return fOperations;
	}

	protected List<IModelOperation> reverseOperations(){
		List<IModelOperation> reverseOperations = new ArrayList<IModelOperation>();
		for(IModelOperation operation : operations()){
			reverseOperations.add(0, operation);
		}
		return reverseOperations;
	}
}
