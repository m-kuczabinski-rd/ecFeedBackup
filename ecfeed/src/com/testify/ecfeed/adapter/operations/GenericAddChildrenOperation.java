package com.testify.ecfeed.adapter.operations;

import java.util.Collection;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.model.GenericNode;

public class GenericAddChildrenOperation extends BulkOperation {

	public GenericAddChildrenOperation(GenericNode target, Collection<? extends GenericNode> children, ITypeAdapterProvider adapterProvider, boolean validate) {
		this(target, children, -1, adapterProvider, validate);
	}

	public GenericAddChildrenOperation(GenericNode target, Collection<? extends GenericNode> children, int index, ITypeAdapterProvider adapterProvider, boolean validate) {
		super(OperationNames.ADD_CHILDREN, false);
		for(GenericNode child : children){
			IModelOperation operation;
			try {
				if(index != -1){
					operation = (IModelOperation)target.accept(new FactoryAddChildOperation(child, index++, adapterProvider, validate));
				}
				else{
					operation = (IModelOperation)target.accept(new FactoryAddChildOperation(child, adapterProvider, validate));
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
