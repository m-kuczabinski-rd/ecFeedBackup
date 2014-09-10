package com.testify.ecfeed.modelif.operations;

import com.testify.ecfeed.model.PartitionNode;

public class PartitionOperationRenameLabel extends BulkOperation {

	public PartitionOperationRenameLabel(PartitionNode target, String currentLabel, String newLabel) {
		super(true);
		
		addOperation(new PartitionOperationRemoveLabel(target, currentLabel));
		addOperation(new PartitionOperationAddLabel(target, newLabel));
	}
}
