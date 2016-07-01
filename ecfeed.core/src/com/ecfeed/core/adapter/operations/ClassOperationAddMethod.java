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

package com.ecfeed.core.adapter.operations;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.ModelOperationException;
import com.ecfeed.core.adapter.java.JavaUtils;
import com.ecfeed.core.adapter.java.Messages;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;

public class ClassOperationAddMethod extends AbstractModelOperation{
	
	private ClassNode fTarget;
	private MethodNode fMethod;
	private int fIndex;

	public ClassOperationAddMethod(ClassNode target, MethodNode method, int index) {
		super(OperationNames.ADD_METHOD);
		fTarget = target;
		fMethod = method;
		fIndex = index;
	}

	public ClassOperationAddMethod(ClassNode target, MethodNode method) {
		this(target, method, -1);
	}

	@Override
	public void execute() throws ModelOperationException {
		List<String> problems = new ArrayList<String>();
		if(fIndex == -1){
			fIndex = fTarget.getMethods().size();
		}
		if(JavaUtils.validateNewMethodSignature(fTarget, fMethod.getName(), fMethod.getParametersTypes(), problems) == false){
			ModelOperationException.report(JavaUtils.consolidate(problems));
		}
		if(fTarget.addMethod(fMethod, fIndex) == false){
			ModelOperationException.report(Messages.UNEXPECTED_PROBLEM_WHILE_ADDING_ELEMENT);
		}
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ClassOperationRemoveMethod(fTarget, fMethod);
	}

}
