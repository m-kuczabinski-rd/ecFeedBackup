/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.adapter.operations;

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
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
				return new MethodOperationRemoveParameter(getMethodTarget(), (MethodParameterNode)getParameter());
			}

		}

		public RemoveMethodParameterOperation(MethodNode target, MethodParameterNode parameter) {
			super(target, parameter);
			fOriginalTestCases = new ArrayList<>();
		}

		@Override
		public void execute() throws ModelOperationException{
			if(validateNewSignature() == false){
				String className = getTarget().getParent().getName();
				String methodName = getTarget().getName();
				throw new ModelOperationException(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM(className, methodName));
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

	public MethodOperationRemoveParameter(MethodNode target, MethodParameterNode parameter, boolean validate) {
		super(OperationNames.REMOVE_METHOD_PARAMETER, true);
		addOperation(new RemoveMethodParameterOperation(target, parameter));
		if(validate){
			addOperation(new MethodOperationMakeConsistent(target));
		}
	}
	public MethodOperationRemoveParameter(MethodNode target, MethodParameterNode parameter) {
		this(target, parameter, true);
	}
}
