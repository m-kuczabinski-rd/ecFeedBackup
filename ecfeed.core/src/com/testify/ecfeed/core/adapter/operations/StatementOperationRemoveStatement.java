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

import com.testify.ecfeed.core.adapter.IModelOperation;
import com.testify.ecfeed.core.adapter.ModelOperationException;
import com.testify.ecfeed.core.model.AbstractStatement;
import com.testify.ecfeed.core.model.StatementArray;

public class StatementOperationRemoveStatement extends AbstractModelOperation {

	private StatementArray fTarget;
	private AbstractStatement fStatement;
	private int fIndex;

	public StatementOperationRemoveStatement(StatementArray target, AbstractStatement statement){
		super(OperationNames.REMOVE_STATEMENT);
		fTarget = target;
		fStatement = statement;
		fIndex = target.getChildren().indexOf(statement);
	}
	
	@Override
	public void execute() throws ModelOperationException {
		fTarget.removeChild(fStatement);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new StatementOperationAddStatement(fTarget, fStatement, fIndex);
	}
}
