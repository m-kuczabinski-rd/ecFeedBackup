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

package com.ecfeed.core.generators;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.ecfeed.core.generators.CartesianProductGenerator;
import com.ecfeed.core.generators.algorithms.CartesianProductAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraint;
import com.ecfeed.core.generators.testutils.GeneratorTestUtils;

public class CartesianGeneratorTest{
	@Test
	public void initializeTest(){
		CartesianProductGenerator<String> generator = new CartesianProductGenerator<String>();
		
		List<List<String>> inputDomain = GeneratorTestUtils.prepareInput(3, 3);
		Collection<IConstraint<String>> constraints = new ArrayList<IConstraint<String>>();

		try {
			generator.initialize(inputDomain, constraints, null);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
		assertTrue(generator.getAlgorithm() instanceof CartesianProductAlgorithm);
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			generator.initialize(inputDomain, constraints, parameters);
		} catch (GeneratorException e) {
			fail("Unexpected GeneratorException: " + e.getMessage());
		}
		assertTrue(generator.getAlgorithm() instanceof CartesianProductAlgorithm);
	}
}
