package com.testify.ecfeed.api;

import java.util.List;

public interface IConstraint<E> {
	public boolean evaluate(List<E> values);
}
