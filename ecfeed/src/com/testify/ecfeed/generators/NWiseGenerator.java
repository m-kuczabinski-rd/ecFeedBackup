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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.testify.ecfeed.generators.algorithms.FastNWiseAlgorithm;
import com.testify.ecfeed.generators.algorithms.IAlgorithm;
import com.testify.ecfeed.generators.algorithms.OptimalNWiseAlgorithm;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.api.IGeneratorParameter;
import com.testify.ecfeed.generators.api.IGeneratorParameter.TYPE;

public class NWiseGenerator<E> extends AbstractGenerator<E>{
	protected final static String N_PARAMETER_NAME = "N";
	protected final static String ALGORITHM_PARAMETER_NAME = "Algorithm";
	
	protected final static String[] SUPPORTED_ALGORITHMS = {"FAST", "COMPACT"};
	
	private List<IGeneratorParameter> fParameters;
	
	public NWiseGenerator(){
		fParameters = new ArrayList<IGeneratorParameter>();
		fParameters.add(new AbstractParameter(N_PARAMETER_NAME, TYPE.INTEGER, 
				true, 2, null));
	}
	
	@Override
	public List<IGeneratorParameter> parameters() {
		return fParameters;
	}

	@Override
	public void initialize(List<? extends List<E>> inputDomain,
			Collection<? extends IConstraint<E>> constraints,
			Map<String, Object> parameters) throws GeneratorException{
		int N = getN(parameters);
		IAlgorithm<E> algorithm = new OptimalNWiseAlgorithm<E>(N);
		algorithm.initialize(inputDomain, constraints);
		setAlgorithm(algorithm);
		super.initialize(inputDomain, constraints, parameters);
	}

	private int getN(Map<String, Object> parameters) throws GeneratorException {
		if(parameters == null || !parameters.containsKey(N_PARAMETER_NAME)){
			throw new GeneratorException("Parameter " + N_PARAMETER_NAME + " is required for NWiseGenerator");
		}
		Object nObject = parameters.get(N_PARAMETER_NAME);
		if(nObject instanceof Integer == false){
			throw new GeneratorException("Parameter " + N_PARAMETER_NAME + " must be integer");
		}
		return (int)nObject;
	}

	@SuppressWarnings("unused")
	private IAlgorithm<E> getAlgorithm(Map<String, Object> parameters) throws GeneratorException {
		IAlgorithm<E> algorithm;
		int n = getN(parameters);
		final IAlgorithm<E> DEFAULT_ALGORITHM = new OptimalNWiseAlgorithm<E>(n);
		if(parameters != null && parameters.containsKey(ALGORITHM_PARAMETER_NAME)){
			String algorithmName = (String) parameters.get(ALGORITHM_PARAMETER_NAME);
			if(algorithmName instanceof String == false){
				throw new GeneratorException(ALGORITHM_PARAMETER_NAME + " parameter must be String");
			}
			switch(algorithmName){
			case "COMPACT":
				algorithm = new OptimalNWiseAlgorithm<E>(n);
				break;
			case "FAST":
				algorithm = new FastNWiseAlgorithm<E>(n);
				break;
			default:
				throw new GeneratorException("Algorithm " + algorithmName + " is not supported");	
			}
		}
		else{
			algorithm = DEFAULT_ALGORITHM;
		}
		return algorithm;
	}

}
