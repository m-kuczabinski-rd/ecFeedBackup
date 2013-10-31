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

package com.testify.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.api.IGeneratorParameter;
import com.testify.ecfeed.api.IGeneratorParameter.TYPE;
import com.testify.generators.algorithms.FastNWiseAlgorithm;
import com.testify.generators.algorithms.INWiseAlgorithm;
import com.testify.generators.algorithms.OptimalNWiseAlgorithm;

public class NWiseGenerator<E> extends AbstractGenerator<E>{
	protected final static String N_PARAMETER_NAME = "N";
	protected final static String ALGORITHM_PARAMETER_NAME = "Algorithm";
	protected final INWiseAlgorithm<E> DEFAULT_ALGORITHM = new OptimalNWiseAlgorithm<E>();
	protected final static String[] SUPPORTED_ALGORITHMS = {"FAST", "COMPACT"};
	
	private List<IGeneratorParameter> fParameters;
	
	public NWiseGenerator(){
		fParameters = new ArrayList<IGeneratorParameter>();
		fParameters.add(new AbstractParameter(N_PARAMETER_NAME, TYPE.NUMERIC, 
				true, 2, null));
		fParameters.add(new AbstractParameter(ALGORITHM_PARAMETER_NAME, TYPE.STRING, 
				false, "COMPACT", SUPPORTED_ALGORITHMS));
	}
	
	@Override
	public List<IGeneratorParameter> parameters() {
		return fParameters;
	}

	@Override
	public void initialize(List<? extends List<E>> inputDomain,
			Collection<? extends IConstraint<E>> constraints,
			Map<String, Object> parameters,
			IProgressMonitor progressMonitor) throws GeneratorException{

		INWiseAlgorithm<E> algorithm = getAlgorithm(parameters);
		int N = getN(parameters);
		algorithm.initialize(N, inputDomain, constraints, progressMonitor);
		setAlgorithm(algorithm);
		super.initialize(inputDomain, constraints, parameters, progressMonitor);
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

	private INWiseAlgorithm<E> getAlgorithm(Map<String, Object> parameters) throws GeneratorException {
		INWiseAlgorithm<E> algorithm;
		if(parameters != null && parameters.containsKey(ALGORITHM_PARAMETER_NAME)){
			String algorithmName = (String) parameters.get(ALGORITHM_PARAMETER_NAME);
			if(algorithmName instanceof String == false){
				throw new GeneratorException(ALGORITHM_PARAMETER_NAME + " parameter must be String");
			}
			switch(algorithmName){
			case "COMPACT":
				algorithm = new OptimalNWiseAlgorithm<E>();
				break;
			case "FAST":
				algorithm = new FastNWiseAlgorithm<E>();
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
