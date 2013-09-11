package com.testify.ecfeed.api;

import java.util.ArrayList;

public interface ITestGenAlgorithm {
	@SuppressWarnings("rawtypes")
	public ArrayList[] generate(ArrayList[] input, IConstraint[] constraints);
}
