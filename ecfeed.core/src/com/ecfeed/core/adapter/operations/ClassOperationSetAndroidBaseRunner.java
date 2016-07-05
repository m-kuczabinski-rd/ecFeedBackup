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
import com.ecfeed.core.model.ClassNode;

public class ClassOperationSetAndroidBaseRunner extends AbstractModelOperation {

	private ClassNode fTarget;
	private String fNewValue;
	private String fOriginalValue;

	public ClassOperationSetAndroidBaseRunner(ClassNode target, String newValue) {
		super(OperationNames.SET_ANDROID_BASE_RUNNER);
		fTarget = target;
		fNewValue = newValue;
		fOriginalValue = target.getAndroidBaseRunner();
	}

	@Override
	public void execute() throws ModelOperationException {
		fTarget.setAndroidBaseRunner(fNewValue);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ClassOperationSetAndroidBaseRunner(fTarget, fOriginalValue);
	}

}
