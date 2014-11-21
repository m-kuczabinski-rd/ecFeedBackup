package com.testify.ecfeed.adapter.operations;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.TestCaseNode;

public class MethodOperationRemoveParameter extends BulkOperation{

	private class RemoveMethodParameterOperation extends GenericOperationRemoveParameter{

		private List<TestCaseNode> fOriginalTestCases;

		private class ReverseOperation extends AbstractReverseOperation{
			public ReverseOperation() {
				super(RemoveMethodParameterOperation.this);
			}

			@Override
			public void execute() throws ModelOperationException {
				getMethodTarget().replaceTestCases(fOriginalTestCases);
				RemoveMethodParameterOperation.super.reverseOperation().execute();
			}

			@Override
			public IModelOperation reverseOperation() {
				return new MethodOperationRemoveParameter(getMethodTarget(), getParameter());
			}

		}

		public RemoveMethodParameterOperation(MethodNode target, ParameterNode parameter) {
			super(target, parameter);
			fOriginalTestCases = new ArrayList<>();
		}

		@Override
		public void execute() throws ModelOperationException{
			if(validateNewSignature() == false){
				throw new ModelOperationException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM);
			}
			fOriginalTestCases.clear();
			for(TestCaseNode tcase : getMethodTarget().getTestCases()){
				fOriginalTestCases.add(tcase.getCopy(getMethodTarget()));
			}
			for(TestCaseNode tc : getMethodTarget().getTestCases()){
				tc.getTestData().remove(getParameter().getIndex());
			}
			super.execute();
		}

		@Override
		public IModelOperation reverseOperation(){
			return new ReverseOperation();
		}

		private MethodNode getMethodTarget(){
			return (MethodNode) getTarget();
		}

		private boolean validateNewSignature() {
			List<String> types = getMethodTarget().getParametersTypes();
			int index = getParameter().getIndex();
			types.remove(index);
			return JavaUtils.validateNewMethodSignature(getMethodTarget().getClassNode(), getMethodTarget().getName(), types);
		}
	}

	public MethodOperationRemoveParameter(MethodNode target, ParameterNode parameter, boolean validate) {
		super(OperationNames.REMOVE_PARAMETER, true);
		addOperation(new RemoveMethodParameterOperation(target, parameter));
		if(validate){
			addOperation(new MethodOperationMakeConsistent(target));
		}
	}
	public MethodOperationRemoveParameter(MethodNode target, ParameterNode parameter) {
		this(target, parameter, true);
	}


//
//
//
//	private class RemoveParameterOperation extends AbstractModelOperation{
//
//		private final MethodNode fTarget;
//		private final ParameterNode fParameter;
//		private int fOriginalIndex;
//		private final ArrayList<TestCaseNode> fOriginalTestCases;
//
//		private class ReverseOperation extends AbstractModelOperation{
//
//			public ReverseOperation() {
//				super("reverse " + MethodOperationRemoveParameter.this.getName());
//			}
//
//			@Override
//			public void execute() throws ModelOperationException {
//				fTarget.addParameter(fParameter, fOriginalIndex);
//				fTarget.replaceTestCases(fOriginalTestCases);
//				markModelUpdated();
//			}
//
//			@Override
//			public IModelOperation reverseOperation() {
//				return new RemoveParameterOperation(fTarget, fParameter);
//			}
//
//		}
//
//		public RemoveParameterOperation(MethodNode target, ParameterNode parameter){
//			super(OperationNames.REMOVE_PARAMETER);
//			fTarget = target;
//			fParameter = parameter;
//			fOriginalTestCases = new ArrayList<TestCaseNode>();
//			fOriginalIndex = parameter.getIndex();
//		}
//
//		@Override
//		public void execute() throws ModelOperationException {
//			if(validateNewSignature() == false){
//				throw new ModelOperationException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM);
//			}
//			fOriginalTestCases.clear();
//			for(TestCaseNode tcase : fTarget.getTestCases()){
//				fOriginalTestCases.add(tcase.getCopy(fTarget));
//			}
//			fOriginalIndex = fParameter.getIndex();
//			fTarget.removeParameter(fParameter);
//			for(TestCaseNode tc : fTarget.getTestCases()){
//				tc.getTestData().remove(fOriginalIndex);
//			}
//			markModelUpdated();
//		}
//
//		@Override
//		public IModelOperation reverseOperation() {
//			return new ReverseOperation();
//		}
//
//		private boolean validateNewSignature() {
//			List<String> types = fTarget.getParametersTypes();
//			int index = fParameter.getIndex();
//			types.remove(index);
//			if(fTarget.getClassNode().getMethod(fTarget.getName(), types) != null){
//				return false;
//			}
//			return true;
//		}
//	}
//
//	public MethodOperationRemoveParameter(MethodNode target, ParameterNode parameter, boolean validate) {
//		super(OperationNames.REMOVE_PARAMETER, true);
//		addOperation(new RemoveParameterOperation(target, parameter));
//		if(validate){
//			addOperation(new MethodOperationMakeConsistent(target));
//		}
//	}
//	public MethodOperationRemoveParameter(MethodNode target, ParameterNode parameter) {
//		this(target, parameter, true);
//	}
}
