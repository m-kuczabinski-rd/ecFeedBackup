package com.testify.ecfeed.adapter.operations;

import java.util.Set;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.PartitionNode;

public class PartitionOperationAddLabel extends AbstractModelOperation {

	private PartitionNode fTarget;
	private String fLabel;
	private Set<PartitionNode> fLabeledDescendants;
	
	private class ReverseOperation extends AbstractModelOperation{

		public ReverseOperation() {
			super(PartitionOperationAddLabel.this.getName());
		}

		@Override
		public void execute() throws ModelOperationException {
			fTarget.removeLabel(fLabel);
			for(PartitionNode p : fLabeledDescendants){
				p.addLabel(fLabel);
			}
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new PartitionOperationAddLabel(fTarget, fLabel);
		}
		
	}

	public PartitionOperationAddLabel(PartitionNode target, String label){
		super(OperationNames.ADD_PARTITION_LABEL);
		fTarget = target;
		fLabel = label;
		fLabeledDescendants = target.getLabeledPartitions(fLabel);
	}
	
	@Override
	public void execute() throws ModelOperationException {
		fTarget.addLabel(fLabel);
		for(PartitionNode p : fLabeledDescendants){
			p.removeLabel(fLabel);
		}
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}
}
