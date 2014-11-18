package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.ChoiceNode;

public class ChoiceOperationRemoveLabel extends BulkOperation{

	private class RemoveLabelOperation extends AbstractModelOperation{

		private ChoiceNode fTarget;
		private String fLabel;

		public RemoveLabelOperation(ChoiceNode target, String label) {
			super(ChoiceOperationRemoveLabel.this.getName());
			fTarget = target;
			fLabel = label;
		}

		@Override
		public void execute() throws ModelOperationException {
			fTarget.removeLabel(fLabel);
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new ChoiceOperationAddLabel(fTarget, fLabel);
		}
	}

	public ChoiceOperationRemoveLabel(ChoiceNode target, String label) {
		super(OperationNames.REMOVE_PARTITION_LABEL, true);
		addOperation(new RemoveLabelOperation(target, label));
		if(target.getParameter().getMethod() != null){
			addOperation(new MethodOperationMakeConsistent(target.getParameter().getMethod()));
		}
	}
}
