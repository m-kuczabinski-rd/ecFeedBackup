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

public class ClassOperationSetRunOnAndroid extends AbstractModelOperation {

	private ClassNode fClassNode;
	private boolean fNewValue;
	private boolean fOriginalValue;

	public ClassOperationSetRunOnAndroid(ClassNode classNode, boolean newValue) {
		super(OperationNames.SET_ANDROID_BASE_RUNNER);
		fClassNode = classNode;
		fNewValue = newValue;
		fOriginalValue = classNode.getRunOnAndroid();
	}

	@Override
	public void execute() throws ModelOperationException {
		fClassNode.setRunOnAndroid(fNewValue);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ClassOperationSetRunOnAndroid(fClassNode, fOriginalValue);
	}

}
