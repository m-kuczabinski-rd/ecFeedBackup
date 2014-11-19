package com.testify.ecfeed.adapter.operations;

import java.util.Collection;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.model.AbstractNode;

public class GenericAddChildrenOperation extends BulkOperation {

	public GenericAddChildrenOperation(AbstractNode target, Collection<? extends AbstractNode> children, ITypeAdapterProvider adapterProvider, boolean validate) {
		this(target, children, -1, adapterProvider, validate);
	}

	public GenericAddChildrenOperation(AbstractNode target, Collection<? extends AbstractNode> children, int index, ITypeAdapterProvider adapterProvider, boolean validate) {
		super(OperationNames.ADD_CHILDREN, false);
		for(AbstractNode child : children){
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
