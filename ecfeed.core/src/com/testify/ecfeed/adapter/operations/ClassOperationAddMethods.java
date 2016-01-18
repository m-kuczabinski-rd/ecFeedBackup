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

import java.util.Collection;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;

public class ClassOperationAddMethods extends BulkOperation{

	public ClassOperationAddMethods(ClassNode target, Collection<MethodNode> methods, int index) {
		super(OperationNames.ADD_METHODS, false);
		for(MethodNode method : methods){
			addOperation(new ClassOperationAddMethod(target, method, index++));
		}
	}
}
