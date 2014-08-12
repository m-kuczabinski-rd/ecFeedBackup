package com.testify.ecfeed.modelif.java.category;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class CategoryOperationSetExpected implements IModelOperation {
	
	private CategoryNode fTarget;
	private boolean fExpected;
	private List<TestCaseNode> fOriginalTestCases;
	private List<ConstraintNode> fOriginalConstraints;
	
	private class ReverseOperation implements IModelOperation{

		@Override
		public void execute() throws ModelIfException {
			fTarget.setExpected(!fExpected);
			if(fTarget.getMethod() != null){
				fTarget.getMethod().replaceConstraints(fOriginalConstraints);
				fTarget.getMethod().replaceTestCases(fOriginalTestCases);
			}
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
			for(TestCaseNode tc : method.getTestCases()){
				fOriginalTestCases.add(tc);
			}
			fOriginalConstraints = new ArrayList<ConstraintNode>();
			for(ConstraintNode c : method.getConstraintNodes()){
				fOriginalConstraints.add(c);
			}
		}
	}

	@Override
	public void execute() throws ModelIfException {
		fTarget.setExpected(fExpected);
		MethodNode method = fTarget.getMethod(); 
		if(method != null){
			int index = fTarget.getIndex();
			Iterator<TestCaseNode> tcIt = method.getTestCases().iterator();
			while(tcIt.hasNext()){
				TestCaseNode testCase = tcIt.next();
				if(fExpected){
					testCase.getTestData().set(index, fTarget.getDefaultValuePartition().getCopy());
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
