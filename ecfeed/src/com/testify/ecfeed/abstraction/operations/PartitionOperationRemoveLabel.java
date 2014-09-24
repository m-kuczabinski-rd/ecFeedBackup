package com.testify.ecfeed.abstraction.operations;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.model.PartitionNode;

public class PartitionOperationRemoveLabel extends BulkOperation{

	private class RemoveLabelOperation extends AbstractModelOperation{

		private PartitionNode fTarget;
		private String fLabel;

		public RemoveLabelOperation(PartitionNode target, String label) {
			super(PartitionOperationRemoveLabel.this.getName());
			fTarget = target;
			fLabel = label;
		}

		@Override
		public void execute() throws ModelIfException {
			fTarget.removeLabel(fLabel);
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new PartitionOperationAddLabel(fTarget, fLabel);
		}
	}

	public PartitionOperationRemoveLabel(PartitionNode target, String label) {
		super(OperationNames.REMOVE_PARTITION_LABEL, true);
		addOperation(new RemoveLabelOperation(target, label));
		if(target.getCategory().getMethod() != null){
			addOperation(new MethodOperationMakeConsistent(target.getCategory().getMethod()));
		}
	}
}
