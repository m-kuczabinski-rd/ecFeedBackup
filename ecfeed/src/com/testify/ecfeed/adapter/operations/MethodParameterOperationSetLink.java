package com.testify.ecfeed.adapter.operations;

import com.testify.ecfeed.adapter.IModelOperation;
import com.testify.ecfeed.adapter.ModelOperationException;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.MethodParameterNode;

public class MethodParameterOperationSetLink extends BulkOperation {

	private class SetLinkOperation extends AbstractModelOperation{
		private MethodParameterNode fTarget;
		private GlobalParameterNode fNewLink;
		private GlobalParameterNode fCurrentLink;

		private class ReverseOperation extends AbstractReverseOperation{

			public ReverseOperation() {
				super(MethodParameterOperationSetLink.this);
			}

			@Override
			public void execute() throws ModelOperationException {
				fTarget.setLink(fCurrentLink);
			}

			@Override
			public IModelOperation reverseOperation() {
				return new SetLinkOperation(fTarget, fNewLink);
			}
		}

		public SetLinkOperation(MethodParameterNode target, GlobalParameterNode link) {
			super(OperationNames.SET_LINK);
			fTarget = target;
			fNewLink = link;
		}

		@Override
		public void execute() throws ModelOperationException {
			fCurrentLink = fTarget.getLink();
			fTarget.setLink(fNewLink);
		}

		@Override
		public IModelOperation reverseOperation() {
			return new ReverseOperation();
		}
	}

	public MethodParameterOperationSetLink(MethodParameterNode target, GlobalParameterNode link) {
		super(OperationNames.SET_LINK, true);
		addOperation(new SetLinkOperation(target, link));
		addOperation(new MethodOperationMakeConsistent(target.getMethod()));
	}
}
