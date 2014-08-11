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
	
	public CategoryOperationSetExpected(CategoryNode target, boolean enabled){
		fTarget = target;
		fExpected = enabled;
		
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
		return new CategoryOperationReverseSetExpected(fTarget, !fExpected, fOriginalTestCases, fOriginalConstraints);
	}
	
	protected CategoryNode getTarget(){
		return fTarget;
	}
	
	protected boolean getExpected(){
		return fExpected;
	}
	
}
