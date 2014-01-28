package com.testify.ecfeed.generators;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.testify.ecfeed.generators.algorithms.RandomAlgorithm;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.api.IGenerator;

public class RandomGenerator<E> extends AbstractGenerator<E> implements
		IGenerator<E> {

	private final String LENGTH_PARAMETER_NAME = "Test suite size";
	private final int DEFAULT_LENGTH = 1;
	private final String DUPLICATES_PARAMETER_NAME = "Duplicates"; 
	private final boolean DEFAULT_DUPLICATES = false;
	
	public RandomGenerator() throws GeneratorException{
		addParameterDefinition(new IntegerParameter(LENGTH_PARAMETER_NAME, true, DEFAULT_LENGTH, 0, Integer.MAX_VALUE));
		addParameterDefinition(new BooleanParameter(DUPLICATES_PARAMETER_NAME, false, DEFAULT_DUPLICATES));
	}
	
	@Override
	public void initialize(List<? extends List<E>> inputDomain,
			Collection<? extends IConstraint<E>> constraints,
			Map<String, Object> parameters) throws GeneratorException{

		super.initialize(inputDomain, constraints, parameters);
		int length = getIntParameter(LENGTH_PARAMETER_NAME);
		boolean duplicates = getBooleanParameter(DUPLICATES_PARAMETER_NAME);
		setAlgorithm(new RandomAlgorithm<E>(length, duplicates));
	}
}
