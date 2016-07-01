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

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.model.EStatementRelation;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.testify.ecfeed.core.adapter.operations.ChoiceOperationSetValue;
import com.testify.ecfeed.core.adapter.operations.StatementOperationSetRelation;
import com.testify.ecfeed.ui.common.EclipseTypeAdapterProvider;
import com.testify.ecfeed.ui.common.Messages;

public class ExpectedValueStatementInterface extends AbstractStatementInterface{

	public ExpectedValueStatementInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	@Override
	public boolean setRelation(EStatementRelation relation) {
		if(relation != getTarget().getRelation()){
			IModelOperation operation = new StatementOperationSetRelation(getTarget(), relation);
			return execute(operation, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public boolean setConditionValue(String newValue) {
		IModelOperation operation = new ChoiceOperationSetValue(getTarget().getCondition(), newValue, new EclipseTypeAdapterProvider());
		return 	execute(operation, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
	}

	@Override
	public String getConditionValue() {
		return getTarget().getCondition().getValueString();
	}

	@Override
	protected ExpectedValueStatement getTarget(){
		return (ExpectedValueStatement)super.getTarget();
	}

}
