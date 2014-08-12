package com.testify.ecfeed.modelif.java.method;

import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class MethodOperationRemoveParameter implements IModelOperation {

	private MethodNode fTarget;
	private CategoryNode fParameter;
	private int fOriginalIndex;
	private List<TestCaseNode> fOriginalTestCases;
	private List<ConstraintNode> fOriginalConstraints;
	
	private class ReverseOperation implements IModelOperation{
		
		@Override
		public void execute() throws ModelIfException {
			fTarget.addCategory(fParameter, fOriginalIndex);
			fTarget.replaceTestCases(fOriginalTestCases);
			fTarget.replaceConstraints(fOriginalConstraints);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new MethodOperationRemoveParameter(fTarget, fParameter);
		}
		
	}

	public MethodOperationRemoveParameter(MethodNode target, CategoryNode parameter){
		fTarget = target;
		fParameter = parameter;
		fOriginalTestCases = target.getTestCases();
		fOriginalConstraints = target.getConstraintNodes();
	}
	
	@Override
	public void execute() throws ModelIfException {
		fTarget.removeCategory(fParameter);
		fTarget.makeConsistent();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ReverseOperation();
	}

}
