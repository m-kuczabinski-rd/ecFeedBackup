package com.testify.generators.algorithms;

import java.util.Vector;

import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.api.ITestGenAlgorithm;

public class BoundValues implements ITestGenAlgorithm {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Vector[] generate(Vector[] input, IConstraint[] constraints) {
		Vector[] preparedInput = new Vector[input.length];
		for(int i = 0; i < input.length; i++){
			Vector preparedElement = new Vector();
			preparedElement.add(input[i].firstElement());
			preparedElement.add(input[i].lastElement());
			preparedInput[i] = preparedElement;
		}
		
		return (new Cartesian()).generate(preparedInput, constraints);
	}

}
