package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.operations.StatementOperationSetCondition;
import com.testify.ecfeed.adapter.operations.StatementOperationSetRelation;
import com.testify.ecfeed.model.ChoicesParentStatement;
import com.testify.ecfeed.model.ChoicesParentStatement.ICondition;
import com.testify.ecfeed.model.EStatementRelation;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.ui.common.Messages;

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