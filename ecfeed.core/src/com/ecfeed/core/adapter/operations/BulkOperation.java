/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.adapter.java.Messages;

public class BulkOperation extends AbstractModelOperation{

	List<IModelOperation> fOperations;
	List<IModelOperation> fExecutedOperations;
	// either all operation or none. if false, all operations are executed
	// otherwise after first error the reverse operation is called
	private final boolean fAtomic;
	private final List<ICheckOperation> fCheckOperations;

	protected interface ICheckOperation{
		public void check() throws ModelOperationException;
	}

	public BulkOperation(String name, boolean atomic) {
		this(name, new ArrayList<IModelOperation>(), atomic);
	}

	public BulkOperation(String name, List<IModelOperation> operations, boolean atomic) {
		super(name);
		fOperations = operations;
		fExecutedOperations = new ArrayList<IModelOperation>();
		fCheckOperations = new ArrayList<ICheckOperation>();
		fAtomic = atomic;
	}

	protected void addOperation(IModelOperation operation) {
		fOperations.add(operation);
	}

	protected void addCheckOperation(ICheckOperation operation){
		fCheckOperations.add(operation);
	}

	@Override
	public void execute() throws ModelOperationException {
		Set<String> errors = new HashSet<String>();
		fExecutedOperations.clear();
		for(IModelOperation operation : fOperations){
			try{
				operation.execute();
				fExecutedOperations.add(operation);
			}catch(ModelOperationException e){
				errors.add(e.getMessage());
				if(fAtomic){
					reverseOperation().execute();
					break;
				}
			}
		}
		for(ICheckOperation operation : fCheckOperations){
			try{
				operation.check();
			}catch(ModelOperationException e){
				errors.add(e.getMessage());
				reverseOperation().execute();
				break;
			}
		}
		if(errors.size() > 0){
			String message = Messages.PROBLEM_WITH_BULK_OPERATION(getName());
			for(String error : errors){
				message += "\n" + error;
			}
			ModelOperationException.report(message);
		}
	}

	@Override
	public IModelOperation reverseOperation(){
		return new BulkOperation("reverse " + getName(), reverseOperations(), fAtomic);
	}


	protected List<IModelOperation> operations(){
		return fOperations;
	}

	protected List<IModelOperation> executedOperations(){
		return fExecutedOperations;
	}

	protected List<IModelOperation> reverseOperations(){
		List<IModelOperation> reverseOperations = new ArrayList<IModelOperation>();
		for(IModelOperation operation : executedOperations()){
			reverseOperations.add(0, operation.reverseOperation());
		}
		return reverseOperations;
	}

	@Override
	public boolean modelUpdated() {
		for(IModelOperation operation : fExecutedOperations){
			if(operation.modelUpdated()){
				return true;
			}
		}
		return false;
	}
}
