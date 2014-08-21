package com.testify.ecfeed.modelif.java.constraint;

import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.modelif.IModelOperation;
import com.testify.ecfeed.modelif.ModelIfException;
import com.testify.ecfeed.modelif.java.common.Messages;

public class StatementOperationReplaceChild implements IModelOperation {

	private BasicStatement fNewChild;
	private BasicStatement fCurrentChild;
	private StatementArray fTarget;

	public StatementOperationReplaceChild(StatementArray target, BasicStatement child, BasicStatement newStatement) {
		fTarget = target;
		fCurrentChild = child;
		fNewChild = newStatement;
	}

	@Override
	public void execute() throws ModelIfException {
		if(fTarget == null){
			throw new ModelIfException(Messages.NULL_POINTER_TARGET);
		}
		fTarget.replaceChild(fCurrentChild, fNewChild);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new StatementOperationReplaceChild(fTarget, fNewChild, fCurrentChild);
	}

}
