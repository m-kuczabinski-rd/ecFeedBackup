package com.testify.ecfeed.modelif.operations;

import java.util.Arrays;

import com.testify.ecfeed.model.constraint.IRelationalStatement;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;

public class StatementOperationSetRelation implements IModelOperation {

	private IRelationalStatement fTarget;
	private Relation fNewRelation;
	private Relation fCurrentRelation;

	public StatementOperationSetRelation(IRelationalStatement target, Relation relation) {
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
	}

	@Override
	public IModelOperation reverseOperation() {
		return new StatementOperationSetRelation(fTarget, fCurrentRelation);
	}

}
