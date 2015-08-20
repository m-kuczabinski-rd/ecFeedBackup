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

package com.testify.ecfeed.generators;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IGenerator;

public class GeneratorFactory<E> {
	
	private Map<String, Class<? extends IGenerator<E>>> fAvailableGenerators;
	
	@SuppressWarnings("unchecked")
	public GeneratorFactory(){
		fAvailableGenerators = new LinkedHashMap<String, Class<? extends IGenerator<E>>>();
		registerGenerator("N-wise generator", (Class<? extends IGenerator<E>>) NWiseGenerator.class);
		registerGenerator("Cartesian Product generator", (Class<? extends IGenerator<E>>) CartesianProductGenerator.class);
		registerGenerator("Adaptive random generator", (Class<? extends IGenerator<E>>) AdaptiveRandomGenerator.class);
		registerGenerator("Random generator", (Class<? extends IGenerator<E>>) RandomGenerator.class);
	}

	public Set<String> availableGenerators(){
		return fAvailableGenerators.keySet();
	}
	
	public IGenerator<E> getGenerator(String name) throws GeneratorException{
		try {
			return fAvailableGenerators.get(name).newInstance();
		} catch (Exception e) {
			GeneratorException.report("Cannot instantiate " + name + ": " + e);
			return null;
		}
	}

	private void registerGenerator(String name, Class<? extends IGenerator<E>> generatorClass) {
		fAvailableGenerators.put(name, generatorClass);
	}
}
