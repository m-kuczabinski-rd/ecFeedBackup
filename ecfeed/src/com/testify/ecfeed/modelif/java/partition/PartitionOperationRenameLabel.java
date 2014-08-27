package com.testify.ecfeed.modelif.java.partition;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.java.common.BulkOperation;

public class PartitionOperationRenameLabel extends BulkOperation {

	public PartitionOperationRenameLabel(PartitionNode target, String currentLabel, String newLabel) {
		super(true);
		
		addOperation(new PartitionOperationRemoveLabel(target, currentLabel));
		addOperation(new PartitionOperationAddLabel(target, newLabel));
	}
}
