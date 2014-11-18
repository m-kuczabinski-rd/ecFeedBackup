package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ITypeAdapter;
import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.PartitionedNode;

public class GenericOperationAddPartition extends BulkOperation {

	private class AddPartitionOperation extends AbstractModelOperation{
		private PartitionedNode fTarget;
		private ChoiceNode fPartition;
		private int fIndex;
		private ITypeAdapterProvider fAdapterProvider;

		public AddPartitionOperation(PartitionedNode target, ChoiceNode partition, ITypeAdapterProvider adapterProvider, int index) {
			super(OperationNames.ADD_PARTITION);
			fTarget = target;
			fPartition = partition;
			fIndex = index;
			fAdapterProvider = adapterProvider;
		}

		@Override
		public void execute() throws ModelOperationException {
			if(fIndex == -1){
				fIndex = fTarget.getPartitions().size();
			}
			if(fTarget.getPartitionNames().contains(fPartition.getName())){
				throw new ModelOperationException(Messages.PARTITION_NAME_DUPLICATE_PROBLEM);
			}
			if(fIndex < 0){
				throw new ModelOperationException(Messages.NEGATIVE_INDEX_PROBLEM);
			}
			if(fIndex > fTarget.getPartitions().size()){
				throw new ModelOperationException(Messages.TOO_HIGH_INDEX_PROBLEM);
			}
			validateChoiceValue(fPartition);
			fTarget.addPartition(fPartition, fIndex);
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new GenericOperationRemovePartition(fTarget, fPartition, false);
		}

		private void validateChoiceValue(ChoiceNode choice) throws ModelOperationException{
			if(choice.isAbstract() == false){
				String type = fTarget.getParameter().getType();
				ITypeAdapter adapter = fAdapterProvider.getAdapter(type);
				String newValue = adapter.convert(choice.getValueString());
				if(newValue == null){
					throw new ModelOperationException(Messages.PARTITION_VALUE_PROBLEM(choice.getValueString()));
				}
			}
			else{
				for(ChoiceNode child : choice.getPartitions()){
					validateChoiceValue(child);
				}
			}
		}
	}

	public GenericOperationAddPartition(PartitionedNode target, ChoiceNode partition, ITypeAdapterProvider adapterProvider, int index, boolean validate) {
		super(OperationNames.ADD_PARTITION, true);
		addOperation(new AddPartitionOperation(target, partition, adapterProvider, index));
		if((target.getParameter().getMethod() != null) && validate){
			addOperation(new MethodOperationMakeConsistent(target.getParameter().getMethod()));
		}
	}

	public GenericOperationAddPartition(PartitionedNode target, ChoiceNode partition, ITypeAdapterProvider adapterProvider, boolean validate) {
		this(target, partition, adapterProvider, -1, validate);
	}
}
