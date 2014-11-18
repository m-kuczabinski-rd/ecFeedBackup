package com.testify.ecfeed.adapter.operations;

import java.util.Collection;

import com.testify.ecfeed.model.ChoiceNode;

public class ChoiceOperationAddLabels extends BulkOperation {
	public ChoiceOperationAddLabels(ChoiceNode target, Collection<String> labels) {
		super(OperationNames.ADD_PARTITION_LABELS, false);
		for(String label : labels){
			if(target.getInheritedLabels().contains(label) == false){
				addOperation(new ChoiceOperationAddLabel(target, label));
			}
		}
	}
}
