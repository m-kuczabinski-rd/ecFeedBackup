package com.testify.ecfeed.modelif.operations;

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
					operation = (IModelOperation)target.accept(new FactoryAddChildOperation(child, index++));
				}
				else{
					operation = (IModelOperation)target.accept(new FactoryAddChildOperation(child));
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
