/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.core.generators;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraint;
import com.ecfeed.core.generators.api.IGenerator;
import com.testify.ecfeed.core.generators.algorithms.RandomAlgorithm;

public class RandomGenerator<E> extends AbstractGenerator<E> implements
		IGenerator<E> {

	public final String LENGTH_PARAMETER_NAME = "length";
	public final int DEFAULT_LENGTH = 1;
	public final String DUPLICATES_PARAMETER_NAME = "duplicates"; 
	public final boolean DEFAULT_DUPLICATES = false;
	
	public RandomGenerator() throws GeneratorException{
		addParameterDefinition(new IntegerParameter(LENGTH_PARAMETER_NAME, true, DEFAULT_LENGTH, 0, Integer.MAX_VALUE));
		addParameterDefinition(new BooleanParameter(DUPLICATES_PARAMETER_NAME, false, DEFAULT_DUPLICATES));
	}
	
	@Override
	public void initialize(List<List<E>> inputDomain,
			Collection<IConstraint<E>> constraints,
			Map<String, Object> parameters) throws GeneratorException{

		super.initialize(inputDomain, constraints, parameters);
		int length = getIntParameter(LENGTH_PARAMETER_NAME);
		boolean duplicates = getBooleanParameter(DUPLICATES_PARAMETER_NAME);
		setAlgorithm(new RandomAlgorithm<E>(length, duplicates));
	}
}
