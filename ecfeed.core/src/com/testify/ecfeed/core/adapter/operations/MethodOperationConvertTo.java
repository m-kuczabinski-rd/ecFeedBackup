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

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;

public class MethodOperationConvertTo extends AbstractModelOperation {

	private MethodNode fTarget;
	private MethodNode fSource;

	public MethodOperationConvertTo(MethodNode target, MethodNode source) {
		super(OperationNames.CONVERT_METHOD);
		fTarget = target;
		fSource = source;
	}

	@Override
	public void execute() throws ModelOperationException {
		if(fTarget.getClassNode().getMethod(fSource.getName(), fSource.getParametersTypes()) != null){
			String className = fTarget.getClassNode().getName();
			String methodName = fSource.getName();
			ModelOperationException.report(Messages.METHOD_SIGNATURE_DUPLICATE_PROBLEM(className, methodName));
		}
		if(fTarget.getParametersTypes().equals(fSource.getParametersTypes()) == false){
			ModelOperationException.report(Messages.METHODS_INCOMPATIBLE_PROBLEM);
		}

		fTarget.setName(fSource.getName());
		for(int i = 0; i < fTarget.getParameters().size(); i++){
			MethodParameterNode targetParameter = fTarget.getMethodParameters().get(i);
			MethodParameterNode sourceParameter = fSource.getMethodParameters().get(i);

			targetParameter.setName(sourceParameter.getName());
		}
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new MethodOperationConvertTo(fSource, fTarget);
	}

}
