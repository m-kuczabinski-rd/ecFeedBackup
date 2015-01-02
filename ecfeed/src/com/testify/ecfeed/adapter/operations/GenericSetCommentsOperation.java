package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.AbstractNode;

public class GenericSetCommentsOperation extends AbstractModelOperation {

	private String fComments;
	private AbstractNode fTarget;
	private String fCurrentComments;

	public GenericSetCommentsOperation(AbstractNode target, String comments) {
		super(OperationNames.SET_COMMENTS);
		fTarget = target;
		fComments = comments;
	}

	@Override
	public void execute() throws ModelOperationException {
		fCurrentComments = fTarget.getDescription() != null ? fTarget.getDescription() : "";
		fTarget.setDescription(fComments);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new GenericSetCommentsOperation(fTarget, fCurrentComments);
	}

}
