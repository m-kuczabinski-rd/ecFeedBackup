package com.testify.ecfeed.adapter.operations;


public abstract class AbstractReverseOperation extends AbstractModelOperation {

	public AbstractReverseOperation(AbstractModelOperation baseOperation) {
		super("reverse " + baseOperation);
	}
}
