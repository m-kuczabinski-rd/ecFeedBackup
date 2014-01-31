package com.testify.ecfeed.generators;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.testify.ecfeed.generators.algorithms.AdaptiveRandomAlgorithm;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;

public class AdaptiveRandomGenerator<E> extends AbstractGenerator<E> {
	public final String HISTORY_DEPTH_PARAMETER_NAME = "Depth";
	public final int DEFAULT_HISTORY_DEPTH_VALUE = -1;
	public final String CANDIDATE_SET_SIZE_PARAMETER_NAME = "Candidate set size";
	public final int DEFAULT_CANDIDATE_SET_SIZE_PARAMETER_VALUE = 100;
	public final String TEST_SUITE_SIZE_PARAMETER_NAME = "Length";
	public final int DEFAULT_TEST_SUITE_SIZE_PARAMETER_VALUE = 1;
	public final String DUPLICATES_PARAMETER_NAME = "Duplicates";
	public final boolean DEFAULT_DUPLICATES_PARAMETER_VALUE = false;

	public AdaptiveRandomGenerator() throws GeneratorException{
		addParameterDefinition(new IntegerParameter(HISTORY_DEPTH_PARAMETER_NAME, 
				false, DEFAULT_HISTORY_DEPTH_VALUE, -1, Integer.MAX_VALUE));
		addParameterDefinition(new IntegerParameter(CANDIDATE_SET_SIZE_PARAMETER_NAME, 
				true, DEFAULT_CANDIDATE_SET_SIZE_PARAMETER_VALUE, 0, Integer.MAX_VALUE));
		addParameterDefinition(new IntegerParameter(TEST_SUITE_SIZE_PARAMETER_NAME, 
				true, DEFAULT_TEST_SUITE_SIZE_PARAMETER_VALUE, 0, Integer.MAX_VALUE));
		addParameterDefinition(new BooleanParameter(DUPLICATES_PARAMETER_NAME, 
				false, DEFAULT_DUPLICATES_PARAMETER_VALUE));
	}
	
	@Override
	public void initialize(List<List<E>> inputDomain,
			Collection<IConstraint<E>> constraints,
			Map<String, Object> parameters) throws GeneratorException{
		
		super.initialize(inputDomain, constraints, parameters);
		int executedSetSize = getIntParameter(HISTORY_DEPTH_PARAMETER_NAME);
		int candidateSetSize = getIntParameter(CANDIDATE_SET_SIZE_PARAMETER_NAME);
		int testSuiteSize = getIntParameter(TEST_SUITE_SIZE_PARAMETER_NAME);
		boolean duplicates = getBooleanParameter(DUPLICATES_PARAMETER_NAME);

		setAlgorithm(new AdaptiveRandomAlgorithm<E>(executedSetSize, 
				candidateSetSize, testSuiteSize, duplicates));
	}
}
