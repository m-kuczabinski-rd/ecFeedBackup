package com.testify.ecfeed.modelif.operations;

import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class ConstraintOperationReplaceStatement implements IModelOperation {

	private BasicStatement fNewStatement;
	private BasicStatement fCurrentStatement;
	private ConstraintNode fTarget;

	public ConstraintOperationReplaceStatement(ConstraintNode target, BasicStatement current, BasicStatement newStatement) {
		fTarget = target;
		fCurrentStatement = current;
		fNewStatement = newStatement;
	}

	@Override
	public void execute() throws ModelIfException {
		Constraint constraint = fTarget.getConstraint();
		if(constraint.getPremise() == fCurrentStatement){
			constraint.setPremise(fNewStatement);
		}
		else if(constraint.getConsequence() == fCurrentStatement){
			constraint.setConsequence(fNewStatement);
		}
		else{
			throw new ModelIfException(Messages.TARGET_STATEMENT_NOT_FOUND_PROBLEM);
		}
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ConstraintOperationReplaceStatement(fTarget, fNewStatement, fCurrentStatement);
	}

}
