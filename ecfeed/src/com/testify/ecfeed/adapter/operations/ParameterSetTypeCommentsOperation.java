package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.AbstractParameterNode;

public class ParameterSetTypeCommentsOperation extends AbstractModelOperation {

	private String fComments;
	private AbstractParameterNode fTarget;
	private String fCurrentComments;

	public ParameterSetTypeCommentsOperation(AbstractParameterNode target, String comments) {
		super(OperationNames.SET_COMMENTS);
		fTarget = target;
		fComments = comments;
	}

	@Override
	public void execute() throws ModelOperationException {
		fCurrentComments = fTarget.getTypeComments() != null ? fTarget.getTypeComments() : "";
		fTarget.setTypeComments(fComments);
	}

	@Override
	public IModelOperation reverseOperation() {
		return new ParameterSetTypeCommentsOperation(fTarget, fCurrentComments);
	}

}
