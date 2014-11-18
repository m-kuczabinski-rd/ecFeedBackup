package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.model.ChoiceNode;

public class ChoiceOperationRenameLabel extends BulkOperation {

	public ChoiceOperationRenameLabel(ChoiceNode target, String currentLabel, String newLabel) {
		super(OperationNames.RENAME_LABEL, true);
		addOperation(new ChoiceOperationRemoveLabel(target, currentLabel));
		addOperation(new ChoiceOperationAddLabel(target, newLabel));
	}
}
