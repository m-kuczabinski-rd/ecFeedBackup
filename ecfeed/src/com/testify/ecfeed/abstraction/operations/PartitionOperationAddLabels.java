package com.testify.ecfeed.abstraction.operations;

import java.util.Collection;

import com.testify.ecfeed.model.PartitionNode;

public class PartitionOperationAddLabels extends BulkOperation {
	public PartitionOperationAddLabels(PartitionNode target, Collection<String> labels) {
		super(false);
		for(String label : labels){
			if(target.getInheritedLabels().contains(label) == false){
				addOperation(new PartitionOperationAddLabel(target, label));
			}
		}
	}
}
