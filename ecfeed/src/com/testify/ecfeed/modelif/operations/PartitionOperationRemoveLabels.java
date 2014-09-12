package com.testify.ecfeed.modelif.operations;

import java.util.Collection;

import com.testify.ecfeed.model.PartitionNode;

public class PartitionOperationRemoveLabels extends BulkOperation {

	public PartitionOperationRemoveLabels(PartitionNode target, Collection<String> labels) {
		super(false);
		for(String label : labels){
			if(target.getInheritedLabels().contains(label) == false){
				addOperation(new PartitionOperationRemoveLabel(target, label));
			}
		}
	}
}
