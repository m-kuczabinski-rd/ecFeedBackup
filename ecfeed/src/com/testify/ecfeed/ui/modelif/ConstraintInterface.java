package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.BasicStatement;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.operations.ConstraintOperationReplaceStatement;
import com.testify.ecfeed.ui.common.Messages;

public class ConstraintInterface extends GenericNodeInterface {

	private ConstraintNode fTarget;

	public ConstraintInterface(IModelUpdateContext updateContext) {
		super(updateContext);
	}

	public void setTarget(ConstraintNode target){
		fTarget = target;
		super.setTarget(target);
	}

	public boolean replaceStatement(BasicStatement current, BasicStatement newStatement) {
		if(current != newStatement){
			IModelOperation operation = new ConstraintOperationReplaceStatement(fTarget, current, newStatement);
			return execute(operation, Messages.DIALOG_REMOVE_TEST_CASES_PROBLEM_TITLE);
		}
		return false;
	}
	
}
