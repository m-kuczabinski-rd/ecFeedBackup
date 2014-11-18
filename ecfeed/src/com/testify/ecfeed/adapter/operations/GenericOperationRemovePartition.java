package com.testify.ecfeed.adapter.operations;

import java.util.Set;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.PartitionedNode;

public class GenericOperationRemovePartition extends BulkOperation {

	private class RemovePartitionOperation extends AbstractModelOperation{
		
		private PartitionedNode fTarget;
		private ChoiceNode fPartition;
		private String fOriginalDefaultValue;
		private int fOriginalIndex;

		private class ReverseOperation extends AbstractModelOperation{

			public ReverseOperation() {
				super(RemovePartitionOperation.this.getName());
			}

			@Override
			public void execute() throws ModelOperationException {
				fTarget.addPartition(fPartition, fOriginalIndex);
				fTarget.getParameter().setDefaultValueString(fOriginalDefaultValue);
				markModelUpdated();
			}

			@Override
			public IModelOperation reverseOperation() {
				return new RemovePartitionOperation(fTarget, fPartition);
			}
			
		}
		
		public RemovePartitionOperation(PartitionedNode target, ChoiceNode partition){
			super(OperationNames.REMOVE_PARTITION);
			fTarget = target;
			fPartition = partition;
			fOriginalIndex = fPartition.getIndex();
			fOriginalDefaultValue = target.getParameter().getDefaultValue();
		}
		
		@Override
		public void execute() throws ModelOperationException {
			fOriginalIndex = fPartition.getIndex();
			ParameterNode parameter = fTarget.getParameter();
			if(parameter.isExpected() && JavaUtils.isPrimitive(parameter.getType()) == false && parameter.getPartitions().size() == 1 && parameter.getPartitions().get(0) == fPartition){
				// We are removing the only partition of expected parameter. 
				// The last parameter must represent the default expected value
				throw new ModelOperationException(Messages.EXPECTED_USER_TYPE_CATEGORY_LAST_PARTITION_PROBLEM);
			}
			fTarget.removePartition(fPartition);
			if(parameter.isExpected() && fPartition.getValueString().equals(parameter.getDefaultValue())){
				// the value of removed partition is the same as default expected value
				// Check if there are leaf partitions with the same value. If not, update the default value
				Set<String> leafValues = parameter.getLeafPartitionValues();
				if(leafValues.contains(parameter.getDefaultValue()) == false){
					if(leafValues.size() > 0){
						parameter.setDefaultValueString(leafValues.toArray(new String[]{})[0]);
					}
					else{
						throw new ModelOperationException(Messages.UNEXPECTED_PROBLEM_WHILE_REMOVING_ELEMENT);
					}
				}
			}
		}
		
		@Override
		public IModelOperation reverseOperation() {
			return new ReverseOperation();
		}
	}

	public GenericOperationRemovePartition(PartitionedNode target, ChoiceNode partition, boolean validate) {
		super(OperationNames.REMOVE_PARTITION, true);
		addOperation(new RemovePartitionOperation(target, partition));
		if((target.getParameter().getMethod() != null) && validate){
			addOperation(new MethodOperationMakeConsistent(target.getParameter().getMethod()));
		}
	}
}
