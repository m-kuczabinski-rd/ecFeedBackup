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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraint;
import com.testify.ecfeed.core.generators.NWiseGenerator;
import com.testify.ecfeed.core.generators.algorithms.AbstractNWiseAlgorithm;
import com.testify.ecfeed.core.generators.algorithms.IAlgorithm;
import com.testify.ecfeed.core.generators.testutils.GeneratorTestUtils;

public class NWiseGeneratorTest {

	@Test
	public void initializeNTest() {
		try {
			/*
			 *	Should I change the way algorithms are initialized? I guess So. There is no real situation in which we alter parameters
			 *	once initialized, though. Awaiting approval.
			 */
			NWiseGenerator<String> generator = new NWiseGenerator<String>();
			List<List<String>> inputDomain = GeneratorTestUtils.prepareInput(3,
					3);
			Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("N", 2);

			generator.initialize(inputDomain, constraints, parameters);
			IAlgorithm<String> algorithm = generator.getAlgorithm();
			assertTrue(algorithm instanceof AbstractNWiseAlgorithm);
			assertEquals(2, ((AbstractNWiseAlgorithm<String>) algorithm).getN());

			try {
				parameters.put("N", 5);
				generator.initialize(inputDomain, constraints, parameters);
				fail("GeneratorException expected");
			} catch (GeneratorException e) {
			}
			try {
				parameters.put("N", -1);
				generator.initialize(inputDomain, constraints, parameters);
				fail("GeneratorException expected");
			} catch (GeneratorException e) {
			}
			try {
				parameters.put("N", 2);
				generator.initialize(inputDomain, constraints, parameters);
			} catch (GeneratorException e) {
				fail("Unexpected GeneratorException");
			}

		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
	}
	
	@Test
	public void initializeCoverageTest() {
		try {

			NWiseGenerator<String> generator = new NWiseGenerator<String>();
			List<List<String>> inputDomain = GeneratorTestUtils.prepareInput(3,
					3);
			Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("N", 2);
			parameters.put("coverage", 100);

			generator.initialize(inputDomain, constraints, parameters);
			IAlgorithm<String> algorithm = generator.getAlgorithm();
			assertTrue(algorithm instanceof AbstractNWiseAlgorithm);
			assertEquals(100,
					((AbstractNWiseAlgorithm<String>) algorithm).getCoverage());

			try {
				parameters.put("coverage", 101);
				generator.initialize(inputDomain, constraints, parameters);
				fail("GeneratorException expected");
			} catch (GeneratorException e) {
			}
			try {
				parameters.put("coverage", -1);
				generator.initialize(inputDomain, constraints, parameters);
				fail("GeneratorException expected");
			} catch (GeneratorException e) {
			}
			try {
				parameters.put("coverage", 50);
				generator.initialize(inputDomain, constraints, parameters);
			} catch (GeneratorException e) {
				fail("Unexpected GeneratorException");
			}
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
	}
}
