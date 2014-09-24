package com.testify.ecfeed.abstraction.operations;

import java.util.Collection;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.model.GenericNode;

public class GenericAddChildrenOperation extends BulkOperation {

	public GenericAddChildrenOperation(GenericNode target, Collection<? extends GenericNode> children, boolean validate) {
		this(target, children, -1, validate);
	}

	public GenericAddChildrenOperation(GenericNode target, Collection<? extends GenericNode> children, int index, boolean validate) {
		super(OperationNames.ADD_CHILDREN, false);
		for(GenericNode child : children){
			IModelOperation operation;
			try {
				if(index != -1){
					operation = (IModelOperation)target.accept(new FactoryAddChildOperation(child, index++, validate));
				}
				else{
					operation = (IModelOperation)target.accept(new FactoryAddChildOperation(child, validate));
				}
				if(operation != null){
					addOperation(operation);
				}
			} catch (Exception e) {}
		}
	}
	
	public boolean enabled(){
		return operations().isEmpty() == false;
	}
}
