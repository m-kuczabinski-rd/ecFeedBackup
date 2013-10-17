package com.testify.ecfeed.api;

import java.util.Collection;
import java.util.List;

public interface IAlgorithmInput<E> {
	List<List<E>> getInput();
	Collection<IConstraint<E>> getConstraints();
}
