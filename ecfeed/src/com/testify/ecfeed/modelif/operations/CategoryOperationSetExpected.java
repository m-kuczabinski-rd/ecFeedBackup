package com.testify.ecfeed.modelif.operations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.JavaUtils;

public class CategoryOperationSetExpected implements IModelOperation {
	
	private CategoryNode fTarget;
	private boolean fExpected;
	private List<TestCaseNode> fOriginalTestCases;
	private List<ConstraintNode> fOriginalConstraints;
	private List<PartitionNode> fOriginalPartitions;
	private String fOriginalDefaultValue;
	
	private class ReverseOperation implements IModelOperation{

		@Override
		public void execute() throws ModelIfException {
			fTarget.setExpected(!fExpected);
			if(fTarget.getMethod() != null){
				fTarget.getMethod().replaceConstraints(fOriginalConstraints);
				fTarget.getMethod().replaceTestCases(fOriginalTestCases);
			}
			fTarget.replacePartitions(fOriginalPartitions);
			fTarget.setDefaultValueString(fOriginalDefaultValue);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new CategoryOperationSetExpected(fTarget, fExpected);
		}
		
	}
	
	public CategoryOperationSetExpected(CategoryNode target, boolean expected){
		fTarget = target;
		fExpected = expected;
		
		MethodNode method = target.getMethod(); 
		if(method != null){
			fOriginalTestCases = new ArrayList<TestCaseNode>();
			fOriginalTestCases.addAll(method.getTestCases());
			fOriginalConstraints = new ArrayList<ConstraintNode>();
			fOriginalConstraints.addAll(method.getConstraintNodes());
		}
		fOriginalPartitions = new ArrayList<PartitionNode>();
		fOriginalPartitions.addAll(fTarget.getPartitions());
		fOriginalDefaultValue = fTarget.getDefaultValue();
	}

	@Override
	public void execute() throws ModelIfException {
		fTarget.setExpected(fExpected);
		String type = fTarget.getType();
		if(fExpected && JavaUtils.hasLimitedValuesSet(type)){
			boolean validDefaultValue = false;
			String currentDefaultValue = fTarget.getDefaultValue();
			for(PartitionNode leaf : fTarget.getLeafPartitions()){
				if(currentDefaultValue.equals(leaf.getValueString())){
					validDefaultValue = true;
					break;
				}
			}
			if(validDefaultValue == false){
				if(fTarget.getLeafPartitions().size() > 0){
					fTarget.setDefaultValueString(fTarget.getLeafPartitions().toArray(new PartitionNode[]{})[0].getValueString());
				}
				else{
					fTarget.addPartition(new PartitionNode("partition", currentDefaultValue));
				}
			}
		}
		
		MethodNode method = fTarget.getMethod(); 
		if(method != null){
			int index = fTarget.getIndex();
			Iterator<TestCaseNode> tcIt = method.getTestCases().iterator();
			while(tcIt.hasNext()){
				TestCaseNode testCase = tcIt.next();
				if(fExpected){
					PartitionNode p = new PartitionNode("expected", fTarget.getDefaultValue());
					p.setParent(fTarget);
					testCase.getTestData().set(index, p.getCopy());
				}
				else{
					tcIt.remove();
				}
			}
			Iterator<ConstraintNode> cIt = method.getConstraintNodes().iterator();
			while(cIt.hasNext()){
				if(cIt.next().mentions(fTarget)){
					cIt.remove();
				}
			}
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}
	
	protected CategoryNode getTarget(){
		return fTarget;
	}
	
	protected boolean getExpected(){
		return fExpected;
	}
	
}
