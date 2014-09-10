package com.testify.ecfeed.modelif.operations;

import java.util.Collection;

import com.testify.ecfeed.model.GenericNode;

public class GenericRemoveNodesOperation extends BulkOperation {

	public GenericRemoveNodesOperation(Collection<? extends GenericNode> nodes){
		super(false);
		for(GenericNode node : nodes){
			addOperation(RemoveOperationFactory.getRemoveOperation(node));
		}
	}
}
