package com.testify.ecfeed.abstraction;

import com.testify.ecfeed.model.GenericNode;

public interface IImplementationStatusResolver {
	public ImplementationStatus getImplementationStatus(GenericNode node);
}
