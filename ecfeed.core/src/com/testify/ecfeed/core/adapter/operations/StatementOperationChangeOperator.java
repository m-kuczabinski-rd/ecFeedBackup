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
import com.testify.ecfeed.model.EStatementOperator;
import com.testify.ecfeed.model.StatementArray;

public class StatementOperationChangeOperator extends AbstractModelOperation {

	private StatementArray fTarget;
	private EStatementOperator fNewOperator;
	private EStatementOperator fCurrentOperator;

	public StatementOperationChangeOperator(StatementArray target, EStatementOperator operator) {
		super(OperationNames.CHANGE_STATEMENT_OPERATOR);
		fTarget = target;
		fNewOperator = operator;
		fCurrentOperator = target.getOperator();
	}

	@Override
	public void execute() throws ModelOperationException {
		fTarget.setOperator(fNewOperator);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new StatementOperationChangeOperator(fTarget, fCurrentOperator);
	}

}
