package com.testify.ecfeed.modeladp.operations;

import java.util.Arrays;

import com.testify.ecfeed.model.constraint.IRelationalStatement;
import com.testify.ecfeed.model.constraint.Relation;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;

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
	public void execute() throws ModelOperationException {
		if(Arrays.asList(fTarget.getAvailableRelations()).contains(fNewRelation) == false){
			throw new ModelOperationException(Messages.DIALOG_UNALLOWED_RELATION_MESSAGE);
		}
		fTarget.setRelation(fNewRelation);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new StatementOperationSetRelation(fTarget, fCurrentRelation);
	}

}
