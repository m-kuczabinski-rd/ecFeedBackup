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
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.adapter.java.Messages;

public class ClassOperationRemoveMethod extends AbstractModelOperation {

	private ClassNode fTarget;
	private MethodNode fMethod;
	private int fCurrentIndex;

	public ClassOperationRemoveMethod(ClassNode target, MethodNode method) {
		super(OperationNames.REMOVE_METHOD);
		fTarget = target;
		fMethod = method;
		fCurrentIndex = fMethod.getIndex();
	}
	
	@Override
	public void execute() throws ModelOperationException {
		fCurrentIndex = fMethod.getIndex();
		if(fTarget.removeMethod(fMethod) == false){
			ModelOperationException.report(Messages.UNEXPECTED_PROBLEM_WHILE_REMOVING_ELEMENT);
		}
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ClassOperationAddMethod(fTarget, fMethod, fCurrentIndex);
	}

}
