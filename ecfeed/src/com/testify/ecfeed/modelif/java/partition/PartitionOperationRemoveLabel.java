package com.testify.ecfeed.modelif.java.partition;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.common.BulkOperation;
import com.testify.ecfeed.modelif.java.method.MethodOperationMakeConsistent;

public class PartitionOperationRemoveLabel extends BulkOperation{

	private class RemoveLabelOperation implements IModelOperation{

		private PartitionNode fTarget;
		private String fLabel;

		public RemoveLabelOperation(PartitionNode target, String label) {
			fTarget = target;
			fLabel = label;
		}

		@Override
		public void execute() throws ModelIfException {
			fTarget.removeLabel(fLabel);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new PartitionOperationAddLabel(fTarget, fLabel);
		}
	}

	public PartitionOperationRemoveLabel(PartitionNode target, String label) {
		super(true);
		addOperation(new RemoveLabelOperation(target, label));
		if(target.getCategory().getMethod() != null){
			addOperation(new MethodOperationMakeConsistent(target.getCategory().getMethod()));
		}
	}
}
