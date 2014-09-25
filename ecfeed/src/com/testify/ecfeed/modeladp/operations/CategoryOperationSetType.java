package com.testify.ecfeed.modeladp.operations;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.PartitionedNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ITypeAdapter;
import com.testify.ecfeed.modeladp.ITypeAdapterProvider;
import com.testify.ecfeed.modeladp.ModelOperationException;
import com.testify.ecfeed.modeladp.java.JavaUtils;

public class CategoryOperationSetType extends BulkOperation{

	private class SetTypeOperation extends AbstractModelOperation{
		
		private CategoryNode fTarget;
		private String fNewType;
		private String fCurrentType;
		private String fOriginalDefaultValue;
		private List<PartitionNode> fOriginalPartitions;
		
		private ITypeAdapterProvider fAdapterProvider;

		private class ReverseOperation extends AbstractModelOperation{

			public ReverseOperation() {
				super(CategoryOperationSetType.this.getName());
			}

			@Override
			public void execute() throws ModelOperationException {
				fTarget.setType(fCurrentType);
				fTarget.setDefaultValueString(fOriginalDefaultValue);
				fTarget.replacePartitions(fOriginalPartitions);
				markModelUpdated();
			}

			@Override
			public IModelOperation reverseOperation() {
				return new SetTypeOperation(fTarget, fNewType, fAdapterProvider);
			}
			
		}
		
		public SetTypeOperation(CategoryNode target, String newType, ITypeAdapterProvider adapterProvider) {
			super(OperationNames.SET_TYPE);
			fTarget = target;
			fNewType = newType;
			fCurrentType = target.getType();
			fAdapterProvider = adapterProvider;
			fOriginalDefaultValue = target.getDefaultValue();
			fOriginalPartitions = target.getPartitions();
		}
		
		@Override
		public void execute() throws ModelOperationException {
			if(JavaUtils.isValidTypeName(fNewType) == false){
				throw new ModelOperationException(Messages.CATEGORY_TYPE_REGEX_PROBLEM);
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
			markModelUpdated();
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
		super(OperationNames.SET_TYPE, true);
		addOperation(new SetTypeOperation(target, newType, adapterProvider));
		if(target.getMethod() != null){
			addOperation(new MethodOperationMakeConsistent(target.getMethod()));
		}
	}


}
