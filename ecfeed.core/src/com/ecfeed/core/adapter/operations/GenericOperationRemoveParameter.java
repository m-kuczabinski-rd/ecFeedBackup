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

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ParametersParentNode;

public class GenericOperationRemoveParameter extends AbstractModelOperation{

	private ParametersParentNode fTarget;
	private AbstractParameterNode fParameter;
	private int fOriginalIndex;

	public GenericOperationRemoveParameter(ParametersParentNode target, AbstractParameterNode parameter) {
		super(OperationNames.REMOVE_METHOD_PARAMETER);
		fTarget = target;
		fParameter = parameter;
	}

	@Override
	public void execute() throws ModelOperationException {
		fOriginalIndex = fTarget.getParameters().indexOf(fParameter);
		fTarget.removeParameter(fParameter);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new GenericOperationAddParameter(fTarget, fParameter, fOriginalIndex);
	}

	protected ParametersParentNode getTarget(){
		return fTarget;
	}

	protected AbstractParameterNode getParameter(){
		return fParameter;
	}

	protected int getOriginalIndex(){
		return fOriginalIndex;
	}
}
