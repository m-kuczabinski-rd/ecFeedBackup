package com.testify.ecfeed.abstraction.operations;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ITypeAdapter;
import com.testify.ecfeed.abstraction.ITypeAdapterProvider;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.abstraction.java.JavaUtils;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.PartitionedNode;

public class CategoryOperationSetType extends BulkOperation{

	private class SetTypeOperation implements IModelOperation{
		
		private CategoryNode fTarget;
		private String fNewType;
		private String fCurrentType;
		private String fOriginalDefaultValue;
		private List<PartitionNode> fOriginalPartitions;
		
		private ITypeAdapterProvider fAdapterProvider;

		private class ReverseOperation implements IModelOperation{

			@Override
			public void execute() throws ModelIfException {
				fTarget.setType(fCurrentType);
				fTarget.setDefaultValueString(fOriginalDefaultValue);
				fTarget.replacePartitions(fOriginalPartitions);
			}

			@Override
			public IModelOperation reverseOperation() {
				return new SetTypeOperation(fTarget, fNewType, fAdapterProvider);
			}
			
		}
		
		public SetTypeOperation(CategoryNode target, String newType, ITypeAdapterProvider adapterProvider) {
			fTarget = target;
			fNewType = newType;
			fCurrentType = target.getType();
			fAdapterProvider = adapterProvider;
			fOriginalDefaultValue = target.getDefaultValue();
			fOriginalPartitions = target.getPartitions();
		}
		
		@Override
		public void execute() throws ModelIfException {
			if(JavaUtils.isValidTypeName(fNewType) == false){
				throw new ModelIfException(Messages.CATEGORY_TYPE_REGEX_PROBLEM);
			}
			
			ITypeAdapter adapter = fAdapterProvider.getAdapter(fNewType);
			fTarget.setType(fNewType);

			convertPartitionValues(fTarget, adapter);
			removeDeadPartitions(fTarget);

			String defaultValue = adapter.convert(fTarget.getDefaultValue());
			if(defaultValue == null){
				if(fTarget.getLeafPartitions().size() > 0){
					defaultValue = fTarget.getLeafPartitions().toArray(new PartitionNode[]{})[0].getValueString();
				}
				defaultValue = adapter.defaultValue();
			}
			fTarget.setDefaultValueString(defaultValue);
		}

		@Override 
		public IModelOperation reverseOperation(){
			return new ReverseOperation();
		}
		
		private void convertPartitionValues(PartitionedNode parent, ITypeAdapter adapter) {
			for(PartitionNode p : parent.getPartitions()){
				convertPartitionValue(p, adapter);
				convertPartitionValues(p, adapter);
			}
		}

		private void convertPartitionValue(PartitionNode p, ITypeAdapter adapter) {
			p.setValueString(adapter.convert(p.getValueString()));
		}

		private void removeDeadPartitions(PartitionedNode parent) {
			List<PartitionNode> toRemove = new ArrayList<PartitionNode>();
			for(PartitionNode p : parent.getPartitions()){
				if(isDead(p)){
					toRemove.add(p);
				}
				else{
					removeDeadPartitions(p);
				}
			}
			for(PartitionNode removed : toRemove){
				parent.removePartition(removed);
			}
		}

		private boolean isDead(PartitionNode p) {
			if(p.isAbstract() == false){
				return p.getValueString() == null;
			}
			boolean allChildrenDead = true;
			for(PartitionNode child : p.getPartitions()){
				if(isDead(child) == false){
					allChildrenDead = false;
					break;
				}
			}
			return allChildrenDead;
		}
	}
	
	public CategoryOperationSetType(CategoryNode target, String newType, ITypeAdapterProvider adapterProvider) {
		super(true);
		addOperation(new SetTypeOperation(target, newType, adapterProvider));
		if(target.getMethod() != null){
			addOperation(new MethodOperationMakeConsistent(target.getMethod()));
		}
	}


}
