package com.testify.ecfeed.abstraction.operations;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Constraint;

public class ConstraintOperationReplaceStatement extends AbstractModelOperation{

	private BasicStatement fNewStatement;
	private BasicStatement fCurrentStatement;
	private ConstraintNode fTarget;

	public ConstraintOperationReplaceStatement(ConstraintNode target, BasicStatement current, BasicStatement newStatement) {
		super(OperationNames.REPLACE_STATEMENT);
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
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ConstraintOperationReplaceStatement(fTarget, fNewStatement, fCurrentStatement);
	}

}
