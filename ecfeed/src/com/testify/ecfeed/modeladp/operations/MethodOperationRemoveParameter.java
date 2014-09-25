package com.testify.ecfeed.modeladp.operations;

import java.util.ArrayList;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;

public class MethodOperationRemoveParameter extends BulkOperation{

	private class RemoveParameterOperation extends AbstractModelOperation{

		private MethodNode fTarget;
		private CategoryNode fParameter;
		private int fOriginalIndex;
		private ArrayList<TestCaseNode> fOriginalTestCases;

		private class ReverseOperation extends AbstractModelOperation{
			
			

			public ReverseOperation() {
				super(MethodOperationRemoveParameter.this.getName());
			}

			@Override
			public void execute() throws ModelOperationException {
				fTarget.addCategory(fParameter, fOriginalIndex);
				fTarget.replaceTestCases(fOriginalTestCases);
				markModelUpdated();
			}

			@Override
			public IModelOperation reverseOperation() {
				return new RemoveParameterOperation(fTarget, fParameter);
			}
			
		}
		
		public RemoveParameterOperation(MethodNode target, CategoryNode parameter){
			super(OperationNames.REMOVE_PARAMETER);
			fTarget = target;
			fParameter = parameter;
			fOriginalTestCases = new ArrayList<TestCaseNode>(target.getTestCases());
			fOriginalIndex = parameter.getIndex();
		}

		@Override
		public void execute() throws ModelOperationException {
			fOriginalIndex = fParameter.getIndex();
			fTarget.removeCategory(fParameter);
			for(TestCaseNode tc : fTarget.getTestCases()){
				tc.getTestData().remove(fOriginalIndex);
			}
			markModelUpdated();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new ReverseOperation();
		}
	}

	public MethodOperationRemoveParameter(MethodNode target, CategoryNode parameter, boolean validate) {
		super(OperationNames.REMOVE_PARAMETER, true);
		addOperation(new RemoveParameterOperation(target, parameter));
		if(validate){
			addOperation(new MethodOperationMakeConsistent(target));
		}
	}
	public MethodOperationRemoveParameter(MethodNode target, CategoryNode parameter) {
		this(target, parameter, true);
	}
}
