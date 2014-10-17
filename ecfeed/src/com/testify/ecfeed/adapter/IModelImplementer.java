package com.testify.ecfeed.adapter;

import com.testify.ecfeed.model.GenericNode;

public interface IModelImplementer {
	public boolean implementable(Class<? extends GenericNode> type);
	public boolean implementable(GenericNode node);
	public boolean implement(GenericNode node);
	public EImplementationStatus getImplementationStatus(GenericNode node);
}
