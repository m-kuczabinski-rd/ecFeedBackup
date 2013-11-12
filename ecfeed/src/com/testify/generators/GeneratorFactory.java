package com.testify.generators;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IGenerator;

public class GeneratorFactory<E> {
	
	private Map<String, Class<? extends IGenerator<E>>> fAvailableGenerators;
	
	@SuppressWarnings("unchecked")
	public GeneratorFactory(){
		fAvailableGenerators = new HashMap<String, Class<? extends IGenerator<E>>>();
		registerGenerator("Cartesian Product generator", (Class<? extends IGenerator<E>>) CartesianProductGenerator.class);
		registerGenerator("Random generator", (Class<? extends IGenerator<E>>) RandomGenerator.class);
		registerGenerator("N-wise generator", (Class<? extends IGenerator<E>>) NWiseGenerator.class);
	}

	public Set<String> availableGenerators(){
		return fAvailableGenerators.keySet();
	}
	
	public IGenerator<E> getGenerator(String name) throws GeneratorException{
		try {
			return fAvailableGenerators.get(name).newInstance();
		} catch (Exception e) {
			throw new GeneratorException("Cannot instantiate " + name + ": " + e);
		}
	}

	private void registerGenerator(String name, Class<? extends IGenerator<E>> generatorClass) {
		fAvailableGenerators.put(name, generatorClass);
	}
}
