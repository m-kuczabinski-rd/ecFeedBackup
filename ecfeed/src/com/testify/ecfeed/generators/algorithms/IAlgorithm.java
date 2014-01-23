package com.testify.ecfeed.generators.algorithms;

import java.util.Collection;
import java.util.List;

import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;

public interface IAlgorithm<E> {
	public void initialize(List<? extends List<E>> input, 
			Collection<? extends IConstraint<E>> constraints) throws GeneratorException;
	public List<E> getNext() throws GeneratorException;
	public void reset();
	public int totalWork();
	public int totalProgress();
	public int workProgress();
}
