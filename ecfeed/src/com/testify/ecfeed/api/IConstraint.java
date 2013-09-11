package com.testify.ecfeed.api;

import java.util.ArrayList;

public interface IConstraint {
	@SuppressWarnings("rawtypes")
	public boolean evaluate(ArrayList values);
}
