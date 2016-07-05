/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.modelif;

import com.ecfeed.core.adapter.IModelOperation;
import com.ecfeed.core.adapter.operations.StatementOperationSetCondition;
import com.ecfeed.core.adapter.operations.StatementOperationSetRelation;
import com.ecfeed.core.model.ChoicesParentStatement;
import com.ecfeed.core.model.EStatementRelation;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ChoicesParentStatement.ICondition;
import com.ecfeed.ui.common.Messages;

public class ChoicesParentStatementInterface extends AbstractStatementInterface{

	public ChoicesParentStatementInterface(IModelUpdateContext updateContext) {
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
	public boolean setConditionValue(String text) {
		if(getTarget().getConditionName().equals(text) == false){
			ICondition newCondition;
			MethodParameterNode parameter = getTarget().getParameter();
			if(parameter.getChoice(text) != null){
				newCondition = getTarget().new ChoiceCondition(parameter.getChoice(text));
			}
			else{
				if(text.contains("[label]")){
					text = text.substring(0, text.indexOf("[label]"));
				}
				newCondition = getTarget().new LabelCondition(text);
			}
			IModelOperation operation = new StatementOperationSetCondition(getTarget(), newCondition);
			return execute(operation, Messages.DIALOG_EDIT_STATEMENT_PROBLEM_TITLE);
		}
		return false;
	}

	@Override
	public String getConditionValue() {
		return getTarget().getConditionName();
	}

	@Override
	protected ChoicesParentStatement getTarget(){
		return (ChoicesParentStatement)super.getTarget();
	}
}
