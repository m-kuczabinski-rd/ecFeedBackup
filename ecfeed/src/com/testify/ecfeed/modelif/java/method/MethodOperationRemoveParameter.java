package com.testify.ecfeed.modelif.java.method;

import java.util.ArrayList;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.common.BulkOperation;

public class MethodOperationRemoveParameter extends BulkOperation{

	private class RemoveParameterOperation implements IModelOperation{

		private MethodNode fTarget;
		private CategoryNode fParameter;
		private int fOriginalIndex;
		private ArrayList<TestCaseNode> fOriginalTestCases;

		private class ReverseOperation implements IModelOperation{

			@Override
			public void execute() throws ModelIfException {
				fTarget.addCategory(fParameter, fOriginalIndex);
				fTarget.replaceTestCases(fOriginalTestCases);
			}

			@Override
			public IModelOperation reverseOperation() {
				return new RemoveParameterOperation(fTarget, fParameter);
			}
			
		}
		
		public RemoveParameterOperation(MethodNode target, CategoryNode parameter){
			fTarget = target;
			fParameter = parameter;
			fOriginalTestCases = new ArrayList<TestCaseNode>(target.getTestCases());
			fOriginalIndex = parameter.getIndex();
		}

		@Override
		public void execute() throws ModelIfException {
			fTarget.removeCategory(fParameter);
			for(TestCaseNode tc : fTarget.getTestCases()){
				tc.getTestData().remove(fOriginalIndex);
			}
		}

		@Override
		public IModelOperation reverseOperation() {
			return new ReverseOperation();
		}
	}

	public MethodOperationRemoveParameter(MethodNode target, CategoryNode parameter) {
		super(true);
		addOperation(new RemoveParameterOperation(target, parameter));
		addOperation(new MethodOperationMakeConsistent(target));
	}
}
