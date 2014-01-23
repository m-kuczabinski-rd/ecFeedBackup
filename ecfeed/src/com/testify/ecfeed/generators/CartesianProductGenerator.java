package com.testify.ecfeed.generators;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.testify.ecfeed.generators.algorithms.CartesianProductAlgorithm;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;

public class CartesianProductGenerator<E> extends AbstractGenerator<E> {

	public CartesianProductGenerator() {
		setAlgorithm(new CartesianProductAlgorithm<E>());
	}
	
	@Override
	public void initialize(List<? extends List<E>> inputDomain,
			Collection<? extends IConstraint<E>> constraints,
			Map<String, Object> parameters) throws GeneratorException {
		
		if(parameters != null && parameters.size() > 0){
			throw new GeneratorException("No parameters are expected by cartesian product generator");
		}
		super.initialize(inputDomain, constraints, parameters);
	}
}