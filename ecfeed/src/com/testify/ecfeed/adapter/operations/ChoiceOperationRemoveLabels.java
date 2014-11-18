package com.testify.ecfeed.adapter.operations;

import java.util.Collection;

import com.testify.ecfeed.model.ChoiceNode;

public class ChoiceOperationRemoveLabels extends BulkOperation {

	public ChoiceOperationRemoveLabels(ChoiceNode target, Collection<String> labels) {
		super(OperationNames.REMOVE_PARTITION_LABELS, false);
		for(String label : labels){
			if(target.getInheritedLabels().contains(label) == false){
				addOperation(new ChoiceOperationRemoveLabel(target, label));
			}
		}
	}
}
