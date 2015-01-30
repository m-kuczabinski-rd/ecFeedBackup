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

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.GlobalParametersParentNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public class FactoryRemoveOperation {

	private static class UnsupportedModelOperation implements IModelOperation{
		@Override
		public void execute() throws ModelOperationException {
			throw new ModelOperationException(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new UnsupportedModelOperation();
		}

		@Override
		public boolean modelUpdated() {
			return false;
		}

		@Override
		public String getName() {
			return "";
		}
	}

	private static class RemoveOperationVisitor implements IModelVisitor{

		private boolean fValidate;
		private ITypeAdapterProvider fAdapterProvider;

		public RemoveOperationVisitor(ITypeAdapterProvider adapterProvider, boolean validate){
			fValidate = validate;
			fAdapterProvider = adapterProvider;
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			return new UnsupportedModelOperation();
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return new RootOperationRemoveClass(node.getRoot(), node);
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return new ClassOperationRemoveMethod(node.getClassNode(), node);
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return new MethodOperationRemoveParameter(node.getMethod(), node, fValidate);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return new GenericOperationRemoveGlobalParameter((GlobalParametersParentNode)node.getParametersParent(), node);
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return new MethodOperationRemoveTestCase(node.getMethod(), node);
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return new MethodOperationRemoveConstraint(node.getMethod(), node);
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return new GenericOperationRemoveChoice(node.getParent(), node, fAdapterProvider, fValidate);
		}
	}

	public static IModelOperation getRemoveOperation(AbstractNode node, ITypeAdapterProvider adapterProvider, boolean validate){
		try {
			return (IModelOperation)node.accept(new RemoveOperationVisitor(adapterProvider, validate));
		} catch (Exception e) {
			return new UnsupportedModelOperation();
		}
	}
}
