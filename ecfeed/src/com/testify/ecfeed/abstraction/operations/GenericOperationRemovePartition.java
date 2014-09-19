package com.testify.ecfeed.abstraction.operations;

import java.util.Set;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.abstraction.java.JavaUtils;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.PartitionedNode;

public class GenericOperationRemovePartition extends BulkOperation {

	private class RemovePartitionOperation implements IModelOperation{
		
		private PartitionedNode fTarget;
		private PartitionNode fPartition;
		private String fOriginalDefaultValue;
		private int fOriginalIndex;

		private class ReverseOperation implements IModelOperation{

			@Override
			public void execute() throws ModelIfException {
				fTarget.addPartition(fPartition, fOriginalIndex);
				fTarget.getCategory().setDefaultValueString(fOriginalDefaultValue);
			}

			@Override
			public IModelOperation reverseOperation() {
				return new RemovePartitionOperation(fTarget, fPartition);
			}
			
		}
		
		public RemovePartitionOperation(PartitionedNode target, PartitionNode partition){
			fTarget = target;
			fPartition = partition;
			fOriginalIndex = fPartition.getIndex();
			fOriginalDefaultValue = target.getCategory().getDefaultValue();
		}
		
		@Override
		public void execute() throws ModelIfException {
			fOriginalIndex = fPartition.getIndex();
			CategoryNode category = fTarget.getCategory();
			if(category.isExpected() && JavaUtils.isPrimitive(category.getType()) == false && category.getPartitions().size() == 1 && category.getPartitions().get(0) == fPartition){
				// We are removing the only partition of expected category. 
				// The last category must represent the default expected value
				throw new ModelIfException(Messages.EXPECTED_USER_TYPE_CATEGORY_LAST_PARTITION_PROBLEM);
			}
			fTarget.removePartition(fPartition);
			if(category.isExpected() && fPartition.getValueString().equals(category.getDefaultValue())){
				// the value of removed partition is the same as default expected value
				// Check if there are leaf partitions with the same value. If not, update the default value
				Set<String> leafValues = category.getLeafPartitionValues();
				if(leafValues.contains(category.getDefaultValue()) == false){
					if(leafValues.size() > 0){
						category.setDefaultValueString(leafValues.toArray(new String[]{})[0]);
					}
					else{
						throw new ModelIfException(Messages.UNEXPECTED_PROBLEM_WHILE_REMOVING_ELEMENT);
					}
				}
			}
		}
		
		@Override
		public IModelOperation reverseOperation() {
			return new ReverseOperation();
		}
	}

	public GenericOperationRemovePartition(PartitionedNode target, PartitionNode partition, boolean validate) {
		super(true);
		addOperation(new RemovePartitionOperation(target, partition));
		if((target.getCategory().getMethod() != null) && validate){
			addOperation(new MethodOperationMakeConsistent(target.getCategory().getMethod()));
		}
	}
}
