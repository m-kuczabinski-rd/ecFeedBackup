package com.testify.ecfeed.modelif.java.common;

import java.util.Collection;

import com.testify.ecfeed.model.GenericNode;

public class RemoveNodesOperation extends BulkOperation {

	public RemoveNodesOperation(Collection<? extends GenericNode> nodes){
		for(GenericNode node : nodes){
			addOperation(RemoveOperationFactory.getRemoveOperation(node));
		}
	}
}
