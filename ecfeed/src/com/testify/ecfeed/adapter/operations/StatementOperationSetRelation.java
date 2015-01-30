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

import java.util.Arrays;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.IRelationalStatement;
import com.testify.ecfeed.model.EStatementRelation;

public class StatementOperationSetRelation extends AbstractModelOperation {

	private IRelationalStatement fTarget;
	private EStatementRelation fNewRelation;
	private EStatementRelation fCurrentRelation;

	public StatementOperationSetRelation(IRelationalStatement target, EStatementRelation relation) {
		super(OperationNames.SET_STATEMENT_RELATION);
		fTarget = target;
		fNewRelation = relation;
		fCurrentRelation = target.getRelation();
	}

	@Override
	public void execute() throws ModelOperationException {
		if(Arrays.asList(fTarget.getAvailableRelations()).contains(fNewRelation) == false){
			throw new ModelOperationException(Messages.DIALOG_UNALLOWED_RELATION_MESSAGE);
		}
		fTarget.setRelation(fNewRelation);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new StatementOperationSetRelation(fTarget, fCurrentRelation);
	}

}
