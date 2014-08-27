package com.testify.ecfeed.modelif.java.partition;

import java.util.Collection;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.java.common.BulkOperation;

public class PartitionOperationRemoveLabels extends BulkOperation {

	public PartitionOperationRemoveLabels(PartitionNode target, Collection<String> labels) {
		super(false);
		for(String label : labels){
			addOperation(new PartitionOperationRemoveLabel(target, label));
		}
	}
}
