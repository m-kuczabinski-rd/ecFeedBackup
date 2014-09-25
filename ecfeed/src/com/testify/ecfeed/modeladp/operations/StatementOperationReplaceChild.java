package com.testify.ecfeed.modeladp.operations;

import com.testify.ecfeed.model.BasicStatement;
import com.testify.ecfeed.model.StatementArray;
import com.testify.ecfeed.modeladp.IModelOperation;
import com.testify.ecfeed.modeladp.ModelOperationException;

public class StatementOperationReplaceChild extends AbstractModelOperation {

	private BasicStatement fNewChild;
	private BasicStatement fCurrentChild;
	private StatementArray fTarget;

	public StatementOperationReplaceChild(StatementArray target, BasicStatement child, BasicStatement newStatement) {
		super(OperationNames.REPLACE_STATEMENT);
		fTarget = target;
		fCurrentChild = child;
		fNewChild = newStatement;
	}

	@Override
	public void execute() throws ModelOperationException {
		if(fTarget == null){
			throw new ModelOperationException(Messages.NULL_POINTER_TARGET);
		}
		fTarget.replaceChild(fCurrentChild, fNewChild);
		markModelUpdated();
	}

	@Override
	public IModelOperation reverseOperation() {
		return new StatementOperationReplaceChild(fTarget, fNewChild, fCurrentChild);
	}

}
