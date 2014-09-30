package com.testify.ecfeed.modeladp;

import com.testify.ecfeed.model.GenericNode;

public interface IModelImplementer {
	public boolean implementable(GenericNode node);
	public void implement(GenericNode node);
	public EImplementationStatus getImplementationStatus(GenericNode node);
}
