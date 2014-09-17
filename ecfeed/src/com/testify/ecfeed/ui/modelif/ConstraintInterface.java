package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.operations.ConstraintOperationRename;
import com.testify.ecfeed.abstraction.operations.ConstraintOperationReplaceStatement;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.ui.common.Messages;

public class ConstraintInterface extends GenericNodeInterface {

	private ConstraintNode fTarget;
	
	public boolean setName(String newName, IModelUpdateContext context) {
		if(newName.equals(getName())){
			return false;
		}
		return execute(new ConstraintOperationRename(fTarget, newName), context, Messages.DIALOG_RENAME_METHOD_PROBLEM_TITLE);
	}

	public void setTarget(ConstraintNode target){
		fTarget = target;
		super.setTarget(target);
	}

	public boolean replaceStatement(BasicStatement current, BasicStatement newStatement, IModelUpdateContext context) {
		if(current != newStatement){
			IModelOperation operation = new ConstraintOperationReplaceStatement(fTarget, current, newStatement);
			return execute(operation, context, Messages.DIALOG_REMOVE_TEST_CASES_PROBLEM_TITLE);
		}
		return false;
	}
	
}
