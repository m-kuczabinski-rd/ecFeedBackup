package com.testify.ecfeed.ui.modelif;

import org.eclipse.ui.forms.AbstractFormPart;

import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.operations.ConstraintOperationRename;
import com.testify.ecfeed.modelif.operations.ConstraintOperationReplaceStatement;

public class ConstraintInterface extends GenericNodeInterface {

	private ConstraintNode fTarget;
	
	public ConstraintInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
	}

	public boolean setName(String newName, AbstractFormPart source, IModelUpdateListener updateListener) {
		if(newName.equals(getName())){
			return false;
		}
		return execute(new ConstraintOperationRename(fTarget, newName), source, updateListener, Messages.DIALOG_RENAME_METHOD_PROBLEM_TITLE);
	}

	public void setTarget(ConstraintNode target){
		fTarget = target;
		super.setTarget(target);
	}

	public boolean replaceStatement(BasicStatement current, BasicStatement newStatement, AbstractFormPart source, IModelUpdateListener updateListener) {
		if(current != newStatement){
			return execute(new ConstraintOperationReplaceStatement(fTarget, current, newStatement), source, updateListener, Messages.DIALOG_REMOVE_TEST_CASES_PROBLEM_TITLE);
		}
		return false;
	}
	
}
