package com.testify.ecfeed.modeladp.operations;

import com.testify.ecfeed.model.PartitionNode;

public class PartitionOperationRenameLabel extends BulkOperation {

	public PartitionOperationRenameLabel(PartitionNode target, String currentLabel, String newLabel) {
		super(OperationNames.RENAME_LABEL, true);
		addOperation(new PartitionOperationRemoveLabel(target, currentLabel));
		addOperation(new PartitionOperationAddLabel(target, newLabel));
	}
}
