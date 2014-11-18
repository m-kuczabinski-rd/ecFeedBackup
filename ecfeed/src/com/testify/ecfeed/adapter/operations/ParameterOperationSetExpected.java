package com.testify.ecfeed.adapter.operations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;

public class ParameterOperationSetExpected extends AbstractModelOperation {
	
	private ParameterNode fTarget;
	private boolean fExpected;
	private List<TestCaseNode> fOriginalTestCases;
	private List<ConstraintNode> fOriginalConstraints;
	private List<PartitionNode> fOriginalPartitions;
	private String fOriginalDefaultValue;
	
	private class ReverseOperation extends AbstractModelOperation{

		public ReverseOperation() {
			super(ParameterOperationSetExpected.this.getName());
		}

		@Override
		public void execute() throws ModelOperationException {
			fTarget.setExpected(!fExpected);
			if(fTarget.getMethod() != null){
				fTarget.getMethod().replaceConstraints(fOriginalConstraints);
				fTarget.getMethod().replaceTestCases(fOriginalTestCases);
			}
			fTarget.replacePartitions(fOriginalPartitions);
			fTarget.setDefaultValueString(fOriginalDefaultValue);
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new ParameterOperationSetExpected(fTarget, fExpected);
		}
		
	}
	
	public ParameterOperationSetExpected(ParameterNode target, boolean expected){
		super(OperationNames.SET_EXPECTED_STATUS);
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
	public void execute() throws ModelOperationException {
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
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}
	
	protected ParameterNode getTarget(){
		return fTarget;
	}
	
	protected boolean getExpected(){
		return fExpected;
	}
	
}
