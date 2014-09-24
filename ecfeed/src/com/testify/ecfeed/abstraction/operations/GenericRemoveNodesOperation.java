package com.testify.ecfeed.abstraction.operations;

import java.util.Collection;

import com.testify.ecfeed.model.GenericNode;

public class GenericRemoveNodesOperation extends BulkOperation {

	public GenericRemoveNodesOperation(Collection<? extends GenericNode> nodes, boolean validate){
		super(OperationNames.REMOVE_NODES, false);
		for(GenericNode node : nodes){
			addOperation(FactoryRemoveOperation.getRemoveOperation(node, validate));
		}
	}
}
