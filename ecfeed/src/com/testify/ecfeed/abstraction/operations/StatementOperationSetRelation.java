package com.testify.ecfeed.abstraction.operations;

import java.util.Arrays;

import com.testify.ecfeed.abstraction.IModelOperation;
import com.testify.ecfeed.abstraction.ModelIfException;
import com.testify.ecfeed.model.constraint.IRelationalStatement;
import com.testify.ecfeed.model.constraint.Relation;

public class StatementOperationSetRelation extends AbstractModelOperation {

	private IRelationalStatement fTarget;
	private Relation fNewRelation;
	private Relation fCurrentRelation;

	public StatementOperationSetRelation(IRelationalStatement target, Relation relation) {
		super(OperationNames.SET_STATEMENT_RELATION);
		fTarget = target;
		fNewRelation = relation;
		fCurrentRelation = target.getRelation();
	}

	@Override
	public void execute() throws ModelIfException {
		if(Arrays.asList(fTarget.getAvailableRelations()).contains(fNewRelation) == false){
			throw new ModelIfException(Messages.DIALOG_UNALLOWED_RELATION_MESSAGE);
		}
		fTarget.setRelation(fNewRelation);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new StatementOperationSetRelation(fTarget, fCurrentRelation);
	}

}
