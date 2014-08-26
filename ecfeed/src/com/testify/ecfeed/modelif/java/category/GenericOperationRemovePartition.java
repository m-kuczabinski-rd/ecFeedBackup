package com.testify.ecfeed.modelif.java.category;

import java.util.List;
import java.util.Set;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.IPartitionedNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.JavaUtils;
import com.testify.ecfeed.modelif.java.common.Messages;

public class GenericOperationRemovePartition implements IModelOperation {

	private IPartitionedNode fTarget;
	private PartitionNode fPartition;
	private List<TestCaseNode> fOriginalTestCases;
	private List<ConstraintNode> fOriginalConstraints;
	private String fOriginalDefaultValue;
	private int fOriginalIndex;

	private class ReverseOperation implements IModelOperation{
		
		@Override
		public void execute() throws ModelIfException {
			fTarget.addPartition(fPartition, fOriginalIndex);
			fTarget.getCategory().getMethod().replaceConstraints(fOriginalConstraints);
			fTarget.getCategory().getMethod().replaceTestCases(fOriginalTestCases);
			fTarget.getCategory().setDefaultValueString(fOriginalDefaultValue);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new GenericOperationRemovePartition(fTarget, fPartition);
		}
		
	}
	
	public GenericOperationRemovePartition(IPartitionedNode target, PartitionNode partition) {
		fTarget = target;
		fPartition = partition;
		fOriginalIndex = fTarget.getIndex();
		fOriginalTestCases = target.getCategory().getMethod().getTestCases();
		fOriginalConstraints = target.getCategory().getMethod().getConstraintNodes();
		fOriginalDefaultValue = target.getCategory().getDefaultValueString();
	}

	@Override
	public void execute() throws ModelIfException {
		CategoryNode category = fTarget.getCategory();
		if(category.isExpected() && JavaUtils.isPrimitive(category.getType()) == false && category.getPartitions().size() == 1 && category.getPartitions().get(0) == fPartition){
			// We are removing the only partition of expected category. 
			// The last category must represent the default expected value
			throw new ModelIfException(Messages.EXPECTED_USER_TYPE_CATEGORY_LAST_PARTITION_PROBLEM);
		}
		fTarget.removePartition(fPartition);
		if(category.isExpected() && fPartition.getValueString().equals(category.getDefaultValueString())){
			// the value of removed partition is the same as default expected value
			// Check if there are leaf partitions with the same value. If not, update the default value
			Set<String> leafValues = category.getLeafPartitionValues();
			if(leafValues.contains(category.getDefaultValueString()) == false){
				if(leafValues.size() > 0){
					category.setDefaultValueString(leafValues.toArray(new String[]{})[0]);
				}
				else{
					throw new ModelIfException(Messages.UNEXPECTED_PROBLEM_WHILE_REMOVING_ELEMENT);
				}
			}
		}
		fTarget.getCategory().getMethod().makeConsistent();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}
	
}
