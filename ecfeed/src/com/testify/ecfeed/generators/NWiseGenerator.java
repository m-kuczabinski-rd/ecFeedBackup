/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.generators;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.testify.ecfeed.generators.algorithms.OptimalNWiseAlgorithm;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;

public class NWiseGenerator<E> extends AbstractGenerator<E>{
	protected final static String N_PARAMETER_NAME = "N";
	
	public NWiseGenerator() throws GeneratorException{
		addParameterDefinition(new IntegerParameter(N_PARAMETER_NAME, true, 2, 1, Integer.MAX_VALUE));
	}
	
	@Override
	public void initialize(List<? extends List<E>> inputDomain,
			Collection<? extends IConstraint<E>> constraints,
			Map<String, Object> parameters) throws GeneratorException{
		super.initialize(inputDomain, constraints, parameters);
		int N = getIntParameter(N_PARAMETER_NAME);
		setAlgorithm(new OptimalNWiseAlgorithm<E>(N));
	}
}
