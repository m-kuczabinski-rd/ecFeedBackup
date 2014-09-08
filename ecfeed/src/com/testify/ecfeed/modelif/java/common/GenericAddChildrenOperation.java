package com.testify.ecfeed.modelif.java.common;

import java.util.Collection;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.IModelOperation;

public class GenericAddChildrenOperation extends BulkOperation {

	public GenericAddChildrenOperation(GenericNode target, Collection<? extends GenericNode> children) {
		super(false);
		for(GenericNode child : children){
			IModelOperation operation;
			try {
				operation = (IModelOperation)target.accept(new AddChildOperationProvider(child));
				if(operation != null){
					addOperation(operation);
				}
			} catch (Exception e) {}
		}
	}
	
	public boolean posible(){
		return operations().isEmpty() == false;
	}
}
