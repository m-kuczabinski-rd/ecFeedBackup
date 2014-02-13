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
import com.testify.ecfeed.generators.algorithms.IAlgorithm;
import com.testify.ecfeed.generators.algorithms.OptimalNWiseAlgorithm;
import com.testify.ecfeed.generators.algorithms.Tuples;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.utils.GeneratorTestUtils;

public class OptimalNWiseTest extends NWiseAlgorithmTest{

	final int MAX_VARIABLES = 5;
	final int MAX_PARTITIONS_PER_VARIABLE = 5;
	private final Collection<IConstraint<String>> EMPTY_CONSTRAINTS = new HashSet<IConstraint<String>>();
	
	@Test
	public void testCorrectness() {
		testCorrectness(OptimalNWiseAlgorithm.class);
	}
	
	@Test
	public void testConstraints() {
		testConstraints(OptimalNWiseAlgorithm.class);
	}
	
	@Test
	public void testSize(){
		try{
		for(int variables : new int[]{1, 2, 5}){
		for(int partitions : new int[]{1, 2, 5}){
			for(int n = 1; n <= variables; n++){
				List<List<String>>input = GeneratorTestUtils.prepareInput(variables, partitions);
				Collection<IConstraint<String>> constraints = EMPTY_CONSTRAINTS;
				IAlgorithm<String> algorithm = new OptimalNWiseAlgorithm<String>(n);

				algorithm.initialize(input, constraints);
				int generatedDataSize = GeneratorTestUtils.algorithmResult(algorithm).size();
				int referenceDataSize = referenceResult(input, n).size();
				assertTrue(Math.abs(generatedDataSize - referenceDataSize) <= referenceDataSize / 30);
			}
		}
		}
		}catch(GeneratorException e){
			fail("Unexpected generator exception: " + e.getMessage());
		}
	}

	private Set<List<String>> referenceResult(List<List<String>> input, int n) throws GeneratorException {
		List<Set<String>> referenceInput = GeneratorTestUtils.referenceInput(input); 
		Set<List<String>> cartesianProduct = Sets.cartesianProduct(referenceInput);
		Set<List<String>> referenceResult = new HashSet<List<String>>();
		Set<List<String>> remainingTuples = getAllTuples(input, n);
		for(int k = maxTuples(input, n); k > 0; k--){
			for(List<String> vector : cartesianProduct){
				Set<List<String>> originalTuples = getTuples(vector, n);
				originalTuples.retainAll(remainingTuples);
				if(originalTuples.size() == k){
					referenceResult.add(vector);
					remainingTuples.removeAll(originalTuples);
				}
			}
		}
		return referenceResult;
	}
	
	protected int maxTuples(List<List<String>> input, int n){
		return (new Tuples<List<String>>(input, n)).getAll().size();
	}

	protected Set<List<String>> getTuples(List<String> vector, int n){
		return (new Tuples<String>(vector, n)).getAll();
	}

}
