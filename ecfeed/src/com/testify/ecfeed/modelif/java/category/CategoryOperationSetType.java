package com.testify.ecfeed.modelif.java.category;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.IPartitionedNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.JavaUtils;
import com.testify.ecfeed.modelif.java.common.Messages;

public class CategoryOperationSetType implements IModelOperation {

	private CategoryNode fTarget;
	private String fNewType;
	private String fCurrentType;
	
	private ITypeAdapterProvider fAdapterProvider;
	
	private String fOriginalDefaultValue;
	private List<PartitionNode> fOriginalPartitions;
	private List<ConstraintNode> fOriginalConstraints;
	private List<TestCaseNode> fOriginalTestCases;

	private class ReverseOperation implements IModelOperation{

		@Override
		public void execute() throws ModelIfException {
			fTarget.setType(fCurrentType);
			fTarget.setDefaultValueString(fOriginalDefaultValue);
			fTarget.replacePartitions(fOriginalPartitions);
			if(fTarget.getMethod() != null){
				fTarget.getMethod().replaceTestCases(fOriginalTestCases);
				fTarget.getMethod().replaceConstraints(fOriginalConstraints);
			}
		}

		@Override
		public IModelOperation reverseOperation() {
			return new CategoryOperationSetType(fTarget, fNewType, fAdapterProvider);
		}
		
	}
	
	public CategoryOperationSetType(CategoryNode target, String newType, ITypeAdapterProvider adapterProvider) {
		fTarget = target;
		fNewType = newType;

		fAdapterProvider = adapterProvider;
		
		fOriginalDefaultValue = target.getDefaultValueString();
		if(fTarget.getMethod() != null){
			fOriginalPartitions = fTarget.getPartitions();
			fOriginalConstraints = fTarget.getMethod().getConstraintNodes();
			fOriginalTestCases = fTarget.getMethod().getTestCases();
		}
	}

	@Override
	public void execute() throws ModelIfException {
		if(JavaUtils.isValidTypeName(fNewType) == false){
			throw new ModelIfException(Messages.CATEGORY_TYPE_REGEX_PROBLEM);
		}
		
		ITypeAdapter adapter = fAdapterProvider.getAdapter(fNewType);
		fTarget.setType(fNewType);
		
		String defaultValue = adapter.convert(fTarget.getDefaultValueString());
		if(defaultValue == null){
			defaultValue = adapter.defaultValue();
		}
		fTarget.setDefaultValueString(defaultValue);
		
		convertPartitionValues(fTarget, adapter);
		removeDeadPartitions(fTarget);
		fTarget.getMethod().makeConsistent();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}

	private void convertPartitionValues(IPartitionedNode parent, ITypeAdapter adapter) {
		for(PartitionNode p : parent.getPartitions()){
			convertPartitionValue(p, adapter);
			convertPartitionValues(p, adapter);
		}
	}

	private void convertPartitionValue(PartitionNode p, ITypeAdapter adapter) {
		p.setValueString(adapter.convert(p.getValueString()));
	}

	private void removeDeadPartitions(IPartitionedNode parent) {
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
