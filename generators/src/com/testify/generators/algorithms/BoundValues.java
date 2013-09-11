package com.testify.generators.algorithms;

import java.util.ArrayList;

import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.api.ITestGenAlgorithm;

public class BoundValues implements ITestGenAlgorithm {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ArrayList[] generate(ArrayList[] input, IConstraint[] constraints) {
		ArrayList[] preparedInput = new ArrayList[input.length];
		for(int i = 0; i < input.length; i++){
			ArrayList preparedElement = new ArrayList();
			preparedElement.add(input[i].get(0));
			preparedElement.add(input[i].get(input[i].size() - 1));
			preparedInput[i] = preparedElement;
		}
		
		return (new Cartesian()).generate(preparedInput, constraints);
	}

}
