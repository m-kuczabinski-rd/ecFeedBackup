package com.testify.ecfeed.adapter;

import com.testify.ecfeed.model.AbstractNode;

public interface IModelImplementer {
	public boolean implementable(Class<? extends AbstractNode> type);
	public boolean implementable(AbstractNode node);
	public boolean implement(AbstractNode node);
	public EImplementationStatus getImplementationStatus(AbstractNode node);
}
