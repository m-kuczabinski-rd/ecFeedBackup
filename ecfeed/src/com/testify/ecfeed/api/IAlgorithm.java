package com.testify.ecfeed.api;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface IAlgorithm<E> {
	Set<List<E>> 
	generate(List<List<E>> input, Collection<IConstraint<E>> constraints);

}
