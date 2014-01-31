package com.testify.ecfeed.generators.algorithms;

public class RandomAlgorithm<E> extends AdaptiveRandomAlgorithm<E> {
	public RandomAlgorithm(int length,
			boolean duplicates) {
		super(0, 1, length, duplicates);
	}
}
