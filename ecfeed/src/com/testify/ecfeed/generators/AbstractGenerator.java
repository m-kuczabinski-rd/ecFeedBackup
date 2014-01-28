package com.testify.ecfeed.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.testify.ecfeed.generators.algorithms.IAlgorithm;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.api.IGenerator;
import com.testify.ecfeed.generators.api.IGeneratorParameter;

public class AbstractGenerator<E> implements IGenerator<E> {

	private List<IGeneratorParameter> fParameterDefinitions = new ArrayList<IGeneratorParameter>();
	private Map<String, Object> fParameterValues = null;
	private IAlgorithm<E> fAlgorithm = null;
	private List<? extends List<E>> fInputDomain;
	private Collection<? extends IConstraint<E>> fConstraints;
	private boolean fInitialized = false;
	
	@Override
	public List<IGeneratorParameter> parameters() {
		return fParameterDefinitions;
	}

	@Override
	public void initialize(List<? extends List<E>> inputDomain,
			Collection<? extends IConstraint<E>> constraints,
			Map<String, Object> parameters)
			throws GeneratorException {
		validateInput(inputDomain);
		validateParameters(parameters);
		fParameterValues = parameters;
		fInputDomain = inputDomain;
		fConstraints = constraints;

		if(fAlgorithm != null){
			fAlgorithm.initialize(inputDomain, constraints);
		}
		fInitialized = true;
	}

	private void validateInput(List<? extends List<E>> inputDomain) throws GeneratorException {
		for(List<E> category : inputDomain){
			if(category.size() == 0){
				throw new GeneratorException("Generator input domain cannot contain empty vectors");
			}
		}
	}

	protected void validateParameters(Map<String, Object> parameters) throws GeneratorException {
		int requiredParameters = 0;
		
		for(IGeneratorParameter definition : fParameterDefinitions){
			Object providedValue = parameters.get(definition.getName());
			if(providedValue == null){
				if(definition.isRequired()){
					throw new GeneratorException("Value of required parameret " + definition.getName() + " is not provided");
				}
			}
			else if(!definition.test(providedValue)){
				throw new GeneratorException("Value " + providedValue + " is not allowed for parameter " + definition.getName());
			}
			
			if(definition.isRequired()){
				++requiredParameters;
			}
		}
		
		if(parameters != null){
			for(String parameterName : parameters.keySet()){
				IGeneratorParameter definition = getParameterDefinition(parameterName);
				if(definition == null){
					throw new GeneratorException("Unknown parameter " + parameterName);
				}
			}
		}
		else if(requiredParameters > 0){
			throw new GeneratorException("Unexpected null value");
		}
	}
	
	@Override
	public List<E> next() throws GeneratorException {
		return fAlgorithm.getNext();
	}

	@Override
	public void reset(){
		fAlgorithm.reset();
	}
	
	protected void setAlgorithm(IAlgorithm<E> algorithm) throws GeneratorException{
		fAlgorithm = algorithm;
		fAlgorithm.initialize(fInputDomain, fConstraints);
	}

	protected IAlgorithm<E> getAlgorithm(){
		return fAlgorithm;
	}

	@Override
	public int totalWork() {
		return 0;
	}

	@Override
	public int workProgress() {
		return 0;
	}

	@Override
	public int totalProgress() {
		return 0;
	}
	
	protected void addParameterDefinition(IGeneratorParameter definition){
		for(int i = 0; i < fParameterDefinitions.size(); i++){
			if(fParameterDefinitions.get(i).getName().equals(definition.getName())){
				fParameterDefinitions.set(i, definition);
			}
		}
		fParameterDefinitions.add(definition);
	}

	protected IGeneratorParameter getParameterDefinition(String name) throws GeneratorException{
		for(IGeneratorParameter parameter : fParameterDefinitions){
			if(parameter.getName().equals(name)){
				return parameter;
			}
		}
		throw new GeneratorException("Parameter " + name + " is not defined for " + this.getClass().getName());
	}


	protected int getIntParameter(String name) throws GeneratorException {
		if(!fInitialized){
			throw new GeneratorException("Parameter values can be obtained after the generator is initialized");
		}
		Object value = getParameterValue(name, fParameterValues);
		if(value instanceof Integer == false){
			throw new GeneratorException("Parameter type must be integer: " + name);
		}
		return (int)value;
	}

	protected boolean getBooleanParameter(String name) throws GeneratorException {
		Object value = getParameterValue(name, fParameterValues);
		if(value instanceof Boolean == false){
			throw new GeneratorException("Parameter type must be boolean: " + name);
		}
		return (boolean)value;
	}

	protected double getDoubleParameter(String name) throws GeneratorException {
		Object value = getParameterValue(name, fParameterValues);
		if(value instanceof Double == false){
			throw new GeneratorException("Parameter type must be double: " + name);
		}
		return (double)value;
	}
	
	protected String getStringParameter(String name) throws GeneratorException {
		Object value = getParameterValue(name, fParameterValues);
		if(value instanceof String == false){
			throw new GeneratorException("Parameter type must be integer: " + name);
		}
		return (String)value;
	}


	private Object getParameterValue(String name, Map<String, Object> values) throws GeneratorException {
		IGeneratorParameter definition = null;
		for(IGeneratorParameter def : fParameterDefinitions){
			if(def.getName().equals(name)){
				definition = def;
			}
		}
		if(definition == null){
			throw new GeneratorException("Unknown parameter: " + name);
		}
		Object value = values.get(name);
		if(value == null){
			if(definition.isRequired()){
				throw new GeneratorException("Required parameter not defined: " + name);
			}
			else{
				return definition.defaultValue();
			}
		}
		
		if(definition.allowedValues() != null){
			Object[] allowedValues = definition.allowedValues();
			boolean valueAllowed = false;
			for(Object allowed : allowedValues){
				if(value.equals(allowed)){
					valueAllowed = true;
				}
			}
			if(!valueAllowed){
				throw new GeneratorException("Value " + value + " is not allowed for parameter " + name);
			}
		}
		return value;
	}


}
