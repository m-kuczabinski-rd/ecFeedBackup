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

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.AbstractStatement;
import com.testify.ecfeed.model.StatementArray;

public class StatementOperationReplaceChild extends AbstractModelOperation {

	private AbstractStatement fNewChild;
	private AbstractStatement fCurrentChild;
	private StatementArray fTarget;

	public StatementOperationReplaceChild(StatementArray target, AbstractStatement child, AbstractStatement newStatement) {
		super(OperationNames.REPLACE_STATEMENT);
		fTarget = target;
		fCurrentChild = child;
		fNewChild = newStatement;
	}

	@Override
	public void execute() throws ModelOperationException {
		if(fTarget == null){
			throw new ModelOperationException(Messages.NULL_POINTER_TARGET);
		}
		fTarget.replaceChild(fCurrentChild, fNewChild);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new StatementOperationReplaceChild(fTarget, fNewChild, fCurrentChild);
	}

}
