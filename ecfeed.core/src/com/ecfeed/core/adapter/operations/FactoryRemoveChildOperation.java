/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.adapter.operations;

import com.ecfeed.core.adapter.ITypeAdapterProvider;
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

public class FactoryRemoveChildOperation implements IModelVisitor{

	private AbstractNode fChild;
	private boolean fValidate;
	private ITypeAdapterProvider fAdapterProvider;

	public FactoryRemoveChildOperation(AbstractNode child, ITypeAdapterProvider adapterProvider, boolean validate) {
		fChild = child;
		fValidate = validate;
	}

	@Override
	public Object visit(RootNode node) throws Exception {
		if(fChild instanceof ClassNode){
			return new RootOperationRemoveClass(node, (ClassNode)fChild);
		}
		if(fChild instanceof GlobalParameterNode){
			return new GenericOperationRemoveParameter(node, (AbstractParameterNode)fChild);
		}
		return null;
	}

	@Override
	public Object visit(ClassNode node) throws Exception {
		if(fChild instanceof MethodNode){
			return new ClassOperationRemoveMethod(node, (MethodNode)fChild);
		}
		if(fChild instanceof GlobalParameterNode){
			return new GenericOperationRemoveParameter(node, (AbstractParameterNode)fChild);
		}
		return null;
	}

	@Override
	public Object visit(MethodNode node) throws Exception {
		if(fChild instanceof MethodParameterNode){
			return new MethodOperationRemoveParameter(node, (MethodParameterNode)fChild);
		}
		if(fChild instanceof ConstraintNode){
			return new MethodOperationRemoveConstraint(node, (ConstraintNode)fChild);
		}
		if(fChild instanceof TestCaseNode){
			return new MethodOperationRemoveTestCase(node, (TestCaseNode)fChild);
		}
		return null;
	}

	@Override
	public Object visit(MethodParameterNode node) throws Exception {
		if(fChild instanceof ChoiceNode){
			return new GenericOperationRemoveChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate);
		}
		return null;
	}

	@Override
	public Object visit(GlobalParameterNode node) throws Exception {
		if(fChild instanceof ChoiceNode){
			return new GenericOperationRemoveChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate);
		}
		return null;
	}

	@Override
	public Object visit(TestCaseNode node) throws Exception {
		return null;
	}

	@Override
	public Object visit(ConstraintNode node) throws Exception {
		return null;
	}

	@Override
	public Object visit(ChoiceNode node) throws Exception {
		if(fChild instanceof ChoiceNode){
			return new GenericOperationRemoveChoice(node, (ChoiceNode)fChild, fAdapterProvider, fValidate);
		}
		return null;
	}

}
