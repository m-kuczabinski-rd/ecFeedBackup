package com.testify.ecfeed.api;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IAlgorithm<E> {
	Set<List<E>> 
	generate(List<List<E>> input, Collection<IConstraint<E>> constraints, IProgressMonitor progressMonitor);

}
