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

package com.testify.ecfeed.generators.algorithms;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.testify.ecfeed.generators.algorithms.CartesianProductAlgorithm;
import com.testify.ecfeed.generators.algorithms.IAlgorithm;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.testutils.GeneratorTestUtils;

public class CartesianProductTest {

	final int MAX_VARIABLES = 6;
	final int MAX_PARTITIONS_PER_VARIABLE = 5;
	final IAlgorithm<String> ALGORITHM = new CartesianProductAlgorithm<String>();
	private final Collection<IConstraint<String>> EMPTY_CONSTRAINTS = new HashSet<IConstraint<String>>();

	@Test
	public void testCorrectness() {
		for (int variables : new int[] { 1, 2, 5 }) {
			for (int choices : new int[] { 1, 2, 5 }) {
				try {
					List<List<String>> input = GeneratorTestUtils.prepareInput(
							variables, choices);
					Set<List<String>> referenceSet = referenceSet(input);
					ALGORITHM.initialize(input, EMPTY_CONSTRAINTS);
					Set<List<String>> algorithmResult = GeneratorTestUtils
							.algorithmResult(ALGORITHM);
					assertEquals(referenceSet.size(), algorithmResult.size());
					for (List<String> element : referenceSet) {
						assertTrue(algorithmResult.contains(element));
					}
				} catch (GeneratorException e) {
					fail("Unexpected generator exception: " + e.getMessage());
				}
			}
		}
	}

	@Test
	public void testCancel() {
		for (int variables : new int[] { 1, 2, 5 }) {
			for (int choices : new int[] { 1, 2, 5 }) {
				try {
					List<List<String>> input = GeneratorTestUtils.prepareInput(
							variables, choices);
					ALGORITHM.initialize(input, EMPTY_CONSTRAINTS);

					ALGORITHM.cancel();

					Set<List<String>> algorithmResult = GeneratorTestUtils
							.algorithmResult(ALGORITHM);
					assertEquals(0, algorithmResult.size());

				} catch (GeneratorException e) {
					fail("Unexpected generator exception: " + e.getMessage());
				}
			}
		}
	}

	@Test
	public void testConstraints() {
		try {
			for (int noOfVariables = 1; noOfVariables <= MAX_VARIABLES; noOfVariables++) {
				for (int choicesPerVariable = 1; choicesPerVariable <= MAX_PARTITIONS_PER_VARIABLE; choicesPerVariable++) {
					List<List<String>> input = GeneratorTestUtils.prepareInput(
							noOfVariables, choicesPerVariable);
					Collection<IConstraint<String>> constraints = GeneratorTestUtils
							.generateRandomConstraints(input);
					Set<List<String>> referenceSet = referenceSet(input);
					referenceSet = filter(referenceSet, constraints);
					ALGORITHM.initialize(input, constraints);
					Set<List<String>> algorithmResult = GeneratorTestUtils
							.algorithmResult(ALGORITHM);
					assertEquals(referenceSet.size(), algorithmResult.size());
					for (List<String> element : referenceSet) {
						assertTrue(algorithmResult.contains(element));
					}
				}
			}
		} catch (GeneratorException e) {
			fail("Unexpected generator exception: " + e.getMessage());
		}
	}

	private Set<List<String>> filter(Set<List<String>> input,
			Collection<IConstraint<String>> constraints) {
		Set<List<String>> filtered = new HashSet<List<String>>();
		for (List<String> vector : input) {
			boolean valid = true;
			for (IConstraint<String> constraint : constraints) {
				if (constraint.evaluate(vector) == false) {
					valid = false;
					break;
				}
				;
			}
			if (valid == true)
				filtered.add(vector);
		}
		return filtered;
	}

	private Set<List<String>> referenceSet(List<List<String>> input) {
		List<Set<String>> referenceInput = GeneratorTestUtils
				.referenceInput(input);
		return Sets.cartesianProduct(referenceInput);
	}

}
