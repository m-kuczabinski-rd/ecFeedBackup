package com.testify.ecfeed.modelif.java.common;

import java.util.Collection;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.modelif.IModelOperation;

public class GenericAddChildrenOperation extends BulkOperation {

	public GenericAddChildrenOperation(GenericNode target, Collection<? extends GenericNode> children) {
		this(target, children, -1);
	}

	public GenericAddChildrenOperation(GenericNode target, Collection<? extends GenericNode> children, int index) {
		super(false);
		for(GenericNode child : children){
			IModelOperation operation;
			try {
				if(index != -1){
					operation = (IModelOperation)target.accept(new AddChildOperationProvider(child, index++));
				}
				else{
					operation = (IModelOperation)target.accept(new AddChildOperationProvider(child));
				}
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
