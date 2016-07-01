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

package com.testify.ecfeed.ui.modelif;

import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.EStatementOperator;
import com.ecfeed.core.model.StatementArray;
import com.testify.ecfeed.core.adapter.IModelOperation;
import com.testify.ecfeed.core.adapter.operations.StatementOperationAddStatement;
import com.testify.ecfeed.core.adapter.operations.StatementOperationChangeOperator;
import com.testify.ecfeed.core.adapter.operations.StatementOperationRemoveStatement;
import com.testify.ecfeed.core.adapter.operations.StatementOperationReplaceChild;
import com.testify.ecfeed.ui.common.Messages;

public class StatementArrayInterface extends AbstractStatementInterface{

	public StatementArrayInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	@Override
	public boolean addStatement(AbstractStatement statement){
		IModelOperation operation = new StatementOperationAddStatement(getTarget(), statement, getTarget().getChildren().size());
		return execute(operation, Messages.DIALOG_ADD_STATEMENT_PROBLEM_TITLE);
	}

	@Override
	public boolean removeChild(AbstractStatement child){
		IModelOperation operation = new StatementOperationRemoveStatement(getTarget(), child);
		return execute(operation, Messages.DIALOG_REMOVE_STATEMENT_PROBLEM_TITLE);
	}

	@Override
	public boolean setOperator(EStatementOperator operator) {
		if(operator != getTarget().getOperator()){
			IModelOperation operation = new StatementOperationChangeOperator(getTarget(), operator);
			return execute(operation, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public EStatementOperator getOperator() {
		return getTarget().getOperator();
	}

	@Override
	public boolean replaceChild(AbstractStatement child, AbstractStatement newStatement) {
		if(child != newStatement){
			IModelOperation operation = new StatementOperationReplaceChild(getTarget(), child, newStatement);
			return execute(operation, Messages.DIALOG_ADD_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	protected StatementArray getTarget(){
		return (StatementArray)super.getTarget();
	}
}
