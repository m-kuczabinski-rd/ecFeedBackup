package com.testify.ecfeed.abstraction.operations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;

public class MethodOperationMakeConsistent extends AbstractModelOperation {

	private MethodNode fTarget;
	private List<ConstraintNode> fOriginalConstraints;
	private List<TestCaseNode> fOriginalTestCases;
	
	private class ReverseOperation extends AbstractModelOperation{

		@Override
		public void execute() throws ModelIfException {
			fTarget.replaceTestCases(fOriginalTestCases);
			fTarget.replaceConstraints(fOriginalConstraints);
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new MethodOperationMakeConsistent(fTarget);
		}
		
	}
	
	public MethodOperationMakeConsistent(MethodNode target){
		fTarget = target;
		fOriginalConstraints = new ArrayList<ConstraintNode>(target.getConstraintNodes());
		fOriginalTestCases = new ArrayList<TestCaseNode>(target.getTestCases());
	}
	
	@Override
	public void execute() throws ModelIfException {
		boolean modelUpdated = false;
		Iterator<TestCaseNode> tcIt = fTarget.getTestCases().iterator();
		while(tcIt.hasNext()){
			if(tcIt.next().isConsistent() == false){
				tcIt.remove();
				modelUpdated = true;
			}
		}
		Iterator<ConstraintNode> cIt = fTarget.getConstraintNodes().iterator();
		while(cIt.hasNext()){
			if(cIt.next().isConsistent() == false){
				cIt.remove();
				modelUpdated = true;
			}
		}
		if(modelUpdated){
			markModelUpdated();
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}

}
