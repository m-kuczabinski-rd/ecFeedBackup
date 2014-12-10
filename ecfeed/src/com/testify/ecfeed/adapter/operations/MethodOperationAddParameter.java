package com.testify.ecfeed.adapter.operations;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.TestCaseNode;

public class MethodOperationAddParameter extends GenericOperationAddParameter {

	List<TestCaseNode> fRemovedTestCases;
	MethodNode fTarget;
	MethodParameterNode fParameter;
	private int fNewIndex;

	private class MethodReverseOperation extends ReverseOperation{

		public MethodReverseOperation() {
			super(fTarget, fParameter);
		}

		@Override
		public void execute() throws ModelOperationException {
			fTarget.replaceTestCases(fRemovedTestCases);
			super.execute();
		}

		@Override
		public IModelOperation reverseOperation() {
			return new MethodOperationAddParameter(fTarget, fParameter);
		}

	}

	public MethodOperationAddParameter(MethodNode target, MethodParameterNode parameter, int index) {
		super(target, parameter, index);
		fRemovedTestCases = new ArrayList<TestCaseNode>(target.getTestCases());
		fTarget = target;
		fParameter = parameter;
		fNewIndex = index != -1 ? index : target.getParameters().size();
	}

	public MethodOperationAddParameter(MethodNode target, MethodParameterNode parameter) {
		this(target, parameter, -1);
	}

	@Override
	public void execute() throws ModelOperationException {
		List<String> types = fTarget.getParametersTypes();
		types.add(fNewIndex, fParameter.getType());
		if(fTarget.getClassNode() != null && fTarget.getClassNode().getMethod(fTarget.getName(), types) != null){
			String className = fTarget.getClassNode().getName();
			String methodName =  fTarget.getClassNode().getMethod(fTarget.getName(), types).getName();
			throw new ModelOperationException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM(className, methodName));
		}
		fTarget.removeTestCases();
		super.execute();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodReverseOperation();
	}

}
