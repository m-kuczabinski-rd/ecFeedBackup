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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.ecfeed.core.generators.RandomGenerator;
import com.ecfeed.core.generators.algorithms.IAlgorithm;
import com.ecfeed.core.generators.algorithms.RandomAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraint;
import com.ecfeed.core.generators.testutils.GeneratorTestUtils;

public class RandomGeneratorTest {
	@Test
	public void initializeTest(){
		try {
			RandomGenerator<String> generator = new RandomGenerator<String>();
			List<List<String>> inputDomain = GeneratorTestUtils.prepareInput(3, 3);
			Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("length", 100);

			generator.initialize(inputDomain, constraints, parameters);
			IAlgorithm<String> algorithm = generator.getAlgorithm(); 
			assertTrue(algorithm instanceof RandomAlgorithm);
			assertEquals(false, ((RandomAlgorithm<String>)algorithm).getDuplicates());
			assertEquals(100, ((RandomAlgorithm<String>)algorithm).getLength());
			
			try{
				parameters.put("duplicates", true);
				generator.initialize(inputDomain, constraints, parameters);
				assertEquals(false, ((RandomAlgorithm<String>)algorithm).getDuplicates());
			}catch(GeneratorException e) {
				fail("Unexpected GeneratorException: " + e.getMessage());
			}
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
	}

}
