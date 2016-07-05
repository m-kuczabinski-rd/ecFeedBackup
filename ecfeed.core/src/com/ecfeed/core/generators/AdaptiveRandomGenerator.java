/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.generators.algorithms.AdaptiveRandomAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraint;

public class AdaptiveRandomGenerator<E> extends AbstractGenerator<E> {
	public final String HISTORY_DEPTH_PARAMETER_NAME = "depth";
	public final int DEFAULT_HISTORY_DEPTH_VALUE = -1;
	public final String CANDIDATE_SET_SIZE_PARAMETER_NAME = "candidate set size";
	public final int DEFAULT_CANDIDATE_SET_SIZE_PARAMETER_VALUE = 100;
	public final String TEST_SUITE_SIZE_PARAMETER_NAME = "length";
	public final int DEFAULT_TEST_SUITE_SIZE_PARAMETER_VALUE = 1;
	public final String DUPLICATES_PARAMETER_NAME = "duplicates";
	public final boolean DEFAULT_DUPLICATES_PARAMETER_VALUE = false;

	public AdaptiveRandomGenerator() throws GeneratorException{
		addParameterDefinition(new IntegerParameter(HISTORY_DEPTH_PARAMETER_NAME, 
				false, DEFAULT_HISTORY_DEPTH_VALUE, -1, Integer.MAX_VALUE));
		addParameterDefinition(new IntegerParameter(CANDIDATE_SET_SIZE_PARAMETER_NAME, 
				false, DEFAULT_CANDIDATE_SET_SIZE_PARAMETER_VALUE, 0, Integer.MAX_VALUE));
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
