package com.testify.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.api.IGenerator;
import com.testify.ecfeed.api.IGeneratorParameter;
import com.testify.ecfeed.api.IGeneratorParameter.TYPE;
import com.testify.generators.algorithms.RandomAlgorithm;

public class RandomGenerator<E> extends AbstractGenerator<E> implements
		IGenerator<E> {

	private final String LENGTH_PARAMETER_NAME = "Test suite size";
	private final int DEFAULT_LENGTH = 1;
	private final String DUPLICATES_PARAMETER_NAME = "Duplicates"; 
	private final boolean DEFAULT_DUPLICATES = false;
	private List<IGeneratorParameter> fParameters;
	
	public RandomGenerator(){
		fParameters = new ArrayList<IGeneratorParameter>();
		fParameters.add(new AbstractParameter(LENGTH_PARAMETER_NAME, TYPE.INTEGER, true, DEFAULT_LENGTH, null));
		fParameters.add(new AbstractParameter(DUPLICATES_PARAMETER_NAME, TYPE.BOOLEAN, false, DEFAULT_DUPLICATES, null));
	}
	
	@Override
	public List<IGeneratorParameter> parameters() {
		return fParameters;
	}
	
	@Override
	public void initialize(List<? extends List<E>> inputDomain,
			Collection<? extends IConstraint<E>> constraints,
			Map<String, Object> parameters) throws GeneratorException{

		setAlgorithm(new RandomAlgorithm<E>(getLength(parameters), getDuplicates(parameters)));
		super.initialize(inputDomain, constraints, parameters);
	}

	private boolean getDuplicates(Map<String, Object> parameters) throws GeneratorException {
		if(!parameters.containsKey(DUPLICATES_PARAMETER_NAME)){
			return DEFAULT_DUPLICATES;
		}
		Object duplicatesObject = parameters.get(DUPLICATES_PARAMETER_NAME);
		if(duplicatesObject instanceof Boolean == false){
			throw new GeneratorException(DUPLICATES_PARAMETER_NAME + " parameter must be bolean");
		}
		return (boolean) duplicatesObject;
	}

	private int getLength(Map<String, Object> parameters) throws GeneratorException {
		if(!parameters.containsKey(LENGTH_PARAMETER_NAME)){
			throw new GeneratorException(LENGTH_PARAMETER_NAME + " parameter must be defined");
		}
		Object lengthObject = parameters.get(LENGTH_PARAMETER_NAME);
		if(lengthObject instanceof Integer == false){
			throw new GeneratorException(LENGTH_PARAMETER_NAME + " parameter must be integer");
		}
		return (int) lengthObject;
	}

}
