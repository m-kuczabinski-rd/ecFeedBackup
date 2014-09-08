package com.testify.ecfeed.modelif.java.common;

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.PartitionedNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class GenericOperationAddPartition implements IModelOperation {

	private PartitionedNode fTarget;
	private PartitionNode fPartition;
	private int fIndex;

	public GenericOperationAddPartition(PartitionedNode target, PartitionNode partition, int index) {
		fTarget = target;
		fPartition = partition;
		fIndex = index;
	}

	@Override
	public void execute() throws ModelIfException {
		if(fTarget.getPartitionNames().contains(fPartition.getName())){
			throw new ModelIfException(Messages.PARTITION_NAME_DUPLICATE_PROBLEM);
		}
		if(fIndex < 0){
			throw new ModelIfException(Messages.NEGATIVE_INDEX_PROBLEM);
		}
		if(fIndex > fTarget.getPartitions().size()){
			throw new ModelIfException(Messages.TOO_HIGH_INDEX_PROBLEM);
		}
		fTarget.addPartition(fPartition, fIndex);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new GenericOperationRemovePartition(fTarget, fPartition);
	}

}
