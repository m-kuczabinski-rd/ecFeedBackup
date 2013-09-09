package com.testify.ecfeed.api;

import java.util.Vector;

public interface ITestGenAlgorithm {
	@SuppressWarnings("rawtypes")
	public Vector[] generate(Vector[] input, IConstraint[] constraints);
}
