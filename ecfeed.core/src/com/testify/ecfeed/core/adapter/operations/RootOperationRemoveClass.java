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
import com.ecfeed.core.model.RootNode;

public class RootOperationRemoveClass extends AbstractModelOperation {

	private ClassNode fRemovedClass;
	private RootNode fTarget;
	private int fCurrentIndex;
	
	public RootOperationRemoveClass(RootNode target, ClassNode removedClass) {
		super(OperationNames.REMOVE_CLASS);
		fTarget = target;
		fRemovedClass = removedClass;
		fCurrentIndex = removedClass.getIndex();
	}

	@Override
	public void execute() throws ModelOperationException {
		fCurrentIndex = fRemovedClass.getIndex();
		fTarget.removeClass(fRemovedClass);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new RootOperationAddNewClass(fTarget, fRemovedClass, fCurrentIndex);
	}

}
