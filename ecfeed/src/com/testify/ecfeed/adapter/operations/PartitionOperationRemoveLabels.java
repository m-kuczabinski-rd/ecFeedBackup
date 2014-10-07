package com.testify.ecfeed.adapter.operations;

import java.util.Collection;

import com.testify.ecfeed.model.PartitionNode;

public class PartitionOperationRemoveLabels extends BulkOperation {

	public PartitionOperationRemoveLabels(PartitionNode target, Collection<String> labels) {
		super(OperationNames.REMOVE_PARTITION_LABELS, false);
		for(String label : labels){
			if(target.getInheritedLabels().contains(label) == false){
				addOperation(new PartitionOperationRemoveLabel(target, label));
			}
		}
	}
}
