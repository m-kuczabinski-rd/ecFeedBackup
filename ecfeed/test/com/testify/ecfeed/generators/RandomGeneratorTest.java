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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.testify.ecfeed.generators.algorithms.IAlgorithm;
import com.testify.ecfeed.generators.algorithms.RandomAlgorithm;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.utils.GeneratorTestUtils;

public class RandomGeneratorTest {
	@Test
	public void initializeTest(){
		try {
			RandomGenerator<String> generator = new RandomGenerator<String>();
			List<List<String>> inputDomain = GeneratorTestUtils.prepareInput(3, 3);
			Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("Length", 100);

			generator.initialize(inputDomain, constraints, parameters);
			IAlgorithm<String> algorithm = generator.getAlgorithm(); 
			assertTrue(algorithm instanceof RandomAlgorithm);
			assertEquals(false, ((RandomAlgorithm<String>)algorithm).getDuplicates());
			assertEquals(100, ((RandomAlgorithm<String>)algorithm).getLength());
			
			try{
				parameters.put("Duplicates", true);
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
