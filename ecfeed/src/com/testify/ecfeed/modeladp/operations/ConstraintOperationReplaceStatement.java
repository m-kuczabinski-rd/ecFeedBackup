package com.testify.ecfeed.modeladp.operations;

import com.testify.ecfeed.model.BasicStatement;
import com.testify.ecfeed.model.Constraint;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;

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
	public void execute() throws ModelOperationException {
		Constraint constraint = fTarget.getConstraint();
		if(constraint.getPremise() == fCurrentStatement){
			constraint.setPremise(fNewStatement);
		}
		else if(constraint.getConsequence() == fCurrentStatement){
			constraint.setConsequence(fNewStatement);
		}
		else{
			throw new ModelOperationException(Messages.TARGET_STATEMENT_NOT_FOUND_PROBLEM);
		}
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ConstraintOperationReplaceStatement(fTarget, fNewStatement, fCurrentStatement);
	}

}
