package com.testify.ecfeed.api;

import java.util.Vector;

public interface IConstraint {
	@SuppressWarnings("rawtypes")
	public boolean evaluate(Vector values);
}
