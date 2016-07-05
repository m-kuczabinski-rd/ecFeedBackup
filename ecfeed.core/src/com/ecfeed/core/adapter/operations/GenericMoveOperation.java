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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ITypeAdapterProvider;
import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoicesParentNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;

public class GenericMoveOperation extends BulkOperation {

	public GenericMoveOperation(List<? extends AbstractNode> moved, AbstractNode newParent, ITypeAdapterProvider adapterProvider) throws ModelOperationException {
		this(moved, newParent, adapterProvider, -1);
	}

	public GenericMoveOperation(List<? extends AbstractNode> moved, AbstractNode newParent, ITypeAdapterProvider adapterProvider, int newIndex) throws ModelOperationException {
		super(OperationNames.MOVE, true);
		Set<MethodNode> methodsInvolved = new HashSet<>();
		try {
			//all nodes have parents other than newParent
			if(externalNodes(moved, newParent)){
				for(AbstractNode node : moved){
					if(node instanceof ChoicesParentNode){
						methodsInvolved.addAll(((ChoicesParentNode)node).getParameter().getMethods());
					}
					addOperation((IModelOperation)node.getParent().accept(new FactoryRemoveChildOperation(node, adapterProvider, false)));
					if(node instanceof GlobalParameterNode && newParent instanceof MethodNode){
						GlobalParameterNode parameter = (GlobalParameterNode)node;
						node = new MethodParameterNode(parameter, adapterProvider.getAdapter(parameter.getType()).defaultValue(), false);
					}
					if(newIndex != -1){
						addOperation((IModelOperation)newParent.accept(new FactoryAddChildOperation(node, newIndex, adapterProvider, false)));
					}
					else{
						addOperation((IModelOperation)newParent.accept(new FactoryAddChildOperation(node, adapterProvider, false)));
					}
					for(MethodNode method : methodsInvolved){
						addOperation(new MethodOperationMakeConsistent(method));
					}
				}
			}
			else if(internalNodes(moved, newParent)){
				GenericShiftOperation operation = FactoryShiftOperation.getShiftOperation(moved, newIndex);
				addOperation(operation);
			}
		} catch (Exception e) {
			ModelOperationException.report(Messages.OPERATION_NOT_SUPPORTED_PROBLEM);
		}
	}

	protected boolean externalNodes(List<? extends AbstractNode> moved, AbstractNode newParent){
		for(AbstractNode node : moved){
			if(node.getParent() == newParent){
				return false;
			}
		}
		return true;
	}

	protected boolean internalNodes(List<? extends AbstractNode> moved, AbstractNode newParent){
		for(AbstractNode node : moved){
			if(node.getParent() != newParent){
				return false;
			}
		}
		return true;
	}
}
