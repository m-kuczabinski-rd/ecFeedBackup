package com.testify.ecfeed.api;

import java.util.List;

public interface IGenerator<E> {
	public void initialize(List<List<E>> inputDomain);
	public String[] getAlgorithms();
	public IAlgorithm<E> getAlgorithm(String name);
}
