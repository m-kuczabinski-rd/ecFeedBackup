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

import java.util.Collection;

import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.RootNode;

public class RootOperationAddClasses extends BulkOperation {
	public RootOperationAddClasses(RootNode target, Collection<ClassNode> classes, int index) {
		super(OperationNames.ADD_CLASSES, false);
		for(ClassNode classNode : classes){
			addOperation(new RootOperationAddNewClass(target, classNode, index++));
		}
	}
}
