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

package com.testify.ecfeed.core.adapter.operations;

import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.IModelVisitor;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;

public class FactoryAddChildOperation implements IModelVisitor{

	private AbstractNode fChild;
	private int fIndex;
	private boolean fValidate;
	private ITypeAdapterProvider fAdapterProvider;

	public FactoryAddChildOperation(AbstractNode child, int index, ITypeAdapterProvider adapterProvider, boolean validate) {
		fChild = child;
		fIndex = index;
		fValidate = validate;
		fAdapterProvider = adapterProvider;
	}

	public FactoryAddChildOperation(AbstractNode child, ITypeAdapterProvider adapterProvider, boolean validate) {
		this(child, -1, adapterProvider, validate);
	}

	@Override
	public Object visit(RootNode node) throws Exception {
		if(fChild instanceof ClassNode){
			if(fIndex == -1){
				return new RootOperationAddNewClass(node, (ClassNode)fChild);
			}
			return new RootOperationAddNewClass(node, (ClassNode)fChild, fIndex);
		}else if(fChild instanceof AbstractParameterNode){
			//It might be problematic that we actually add a copy of the requested node, so the option to add
			//a MethodParameterNode to GlobalParameterParent (and vice versa) might be removed
			GlobalParameterNode globalParameter = new GlobalParameterNode((AbstractParameterNode)fChild);
			if(fIndex == -1){
				return new GenericOperationAddParameter(node, globalParameter);
			}
			return new GenericOperationAddParameter(node, globalParameter, fIndex);
		}
		ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		return null;
	}

	@Override
	public Object visit(ClassNode node) throws Exception {
		if(fChild instanceof MethodNode){
			if(fIndex == -1){
				return new ClassOperationAddMethod(node, (MethodNode)fChild);
			}
			return new ClassOperationAddMethod(node, (MethodNode)fChild, fIndex);
		}else if(fChild instanceof AbstractParameterNode){
			GlobalParameterNode globalParameter = new GlobalParameterNode((AbstractParameterNode)fChild);
			if(fIndex == -1){
				return new GenericOperationAddParameter(node, globalParameter);
			}
			return new GenericOperationAddParameter(node, globalParameter, fIndex);
		}
		ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		return null;
	}

	@Override
	public Object visit(MethodNode node) throws Exception {
		if(fChild instanceof GlobalParameterNode){
			GlobalParameterNode globalParameter = (GlobalParameterNode)fChild;
			String defaultValue = fAdapterProvider.getAdapter(globalParameter.getType()).defaultValue();
			MethodParameterNode parameter = new MethodParameterNode(globalParameter, defaultValue, false);

			if(fIndex == -1){
				return new MethodOperationAddParameter(node,parameter);
			}
			return new MethodOperationAddParameter(node, parameter, fIndex);
		}
		if(fChild instanceof MethodParameterNode){
			if(fIndex == -1){
				return new MethodOperationAddParameter(node, (MethodParameterNode)fChild);
			}
			return new MethodOperationAddParameter(node, (MethodParameterNode)fChild, fIndex);
		}
		if(fChild instanceof ConstraintNode){
			if(fIndex == -1){
				return new MethodOperationAddConstraint(node, (ConstraintNode)fChild);
			}
			return new MethodOperationAddConstraint(node, (ConstraintNode)fChild, fIndex);
		}
		if(fChild instanceof TestCaseNode){
			if(fIndex == -1){
				return new MethodOperationAddTestCase(node, (TestCaseNode)fChild, fAdapterProvider);
			}
			return new MethodOperationAddTestCase(node, (TestCaseNode)fChild, fAdapterProvider, fIndex);
		}
		ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		return null;
	}

	@Override
	public Object visit(MethodParameterNode node) throws Exception {
		if(fChild instanceof ChoiceNode){
			if(fIndex == -1){
				return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate);
			}
			return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fIndex, fValidate);
		}
		ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		return null;
	}

	@Override
	public Object visit(GlobalParameterNode node) throws Exception {
		if(fChild instanceof ChoiceNode){
			if(fIndex == -1){
				return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate);
			}
			return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fIndex, fValidate);
		}
		ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		return null;
	}

	@Override
	public Object visit(TestCaseNode node) throws Exception {
		ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		return null;
	}

	@Override
	public Object visit(ConstraintNode node) throws Exception {
		ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		return null;
	}

	@Override
	public Object visit(ChoiceNode node) throws Exception {
		if(fChild instanceof ChoiceNode){
			if(fIndex == -1){
				return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate);
			}
			return new GenericOperationAddChoice(node, (ChoiceNode)fChild, fAdapterProvider, fIndex, fValidate);
		}
		ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		return null;
	}

}
