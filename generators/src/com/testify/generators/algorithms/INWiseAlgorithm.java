package com.testify.generators.algorithms;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IConstraint;

public interface INWiseAlgorithm<E> extends IAlgorithm<E>{
	public void initialize(int n,
			List<? extends List<E>> input, 
			Collection<? extends IConstraint<E>> constraints,
			IProgressMonitor progressMonitor) throws GeneratorException;
//	public List<E> getNext() throws GeneratorException;
//	public void reset() throws GeneratorException;
}
