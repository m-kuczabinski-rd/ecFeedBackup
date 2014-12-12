package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.operations.ConstraintOperationReplaceStatement;
import com.testify.ecfeed.model.AbstractStatement;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.ui.common.Messages;

public class ConstraintInterface extends AbstractNodeInterface {

	public ConstraintInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	@Override
	protected ConstraintNode getTarget(){
		return (ConstraintNode)super.getTarget();
	}

	public boolean replaceStatement(AbstractStatement current, AbstractStatement newStatement) {
		if(current != newStatement){
			IModelOperation operation = new ConstraintOperationReplaceStatement(getTarget(), current, newStatement);
			return execute(operation, Messages.DIALOG_REMOVE_TEST_CASES_PROBLEM_TITLE);
		}
		return false;
	}

}
