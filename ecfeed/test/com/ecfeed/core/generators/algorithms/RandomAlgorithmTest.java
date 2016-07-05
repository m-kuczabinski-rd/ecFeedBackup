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

package com.ecfeed.core.generators.algorithms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.ecfeed.core.generators.algorithms.IAlgorithm;
import com.ecfeed.core.generators.algorithms.RandomAlgorithm;
import com.ecfeed.core.generators.api.GeneratorException;
import com.ecfeed.core.generators.api.IConstraint;
import com.ecfeed.core.generators.testutils.GeneratorTestUtils;

public class RandomAlgorithmTest {
	final int MAX_VARIABLES = 5;
	final int MAX_PARTITIONS_PER_VARIABLE = 5;
	final int SAMPLE_SIZE = (int) (10 * Math.pow(MAX_PARTITIONS_PER_VARIABLE, MAX_VARIABLES));
	
	Collection<IConstraint<String>> EMPTY_CONSTRAINTS(){return new ArrayList<IConstraint<String>>();}

	protected GeneratorTestUtils utils = new GeneratorTestUtils(); 
	
	@Test
	public void uniformityTest(){
		for(int variables = 1; variables <= MAX_VARIABLES; variables++){
			for(int choices = 1; choices <= MAX_PARTITIONS_PER_VARIABLE; choices++){
				uniformityTest(variables, choices);
			}
		}
	}
	
	protected void uniformityTest(int variables, int choices) {
		Map<List<String>, Long> histogram = new HashMap<List<String>, Long>();
		List<List<String>> input = GeneratorTestUtils.prepareInput(variables, choices);
		IAlgorithm<String> algorithm = new RandomAlgorithm<String>((int)(SAMPLE_SIZE), true);
		try {
			algorithm.initialize(input, EMPTY_CONSTRAINTS());
			List<String> next;
			while((next = algorithm.getNext()) != null){
				if(histogram.containsKey(next)){
					histogram.put(next, histogram.get(next) + 1);
				}
				else{
					histogram.put(next, 1l);
				}
			}
		} catch (GeneratorException e) {
			fail("Unexpected generator exception: " + e.getMessage());
		}
		testUniformity(histogram.values());
	}
	
	private void testUniformity(Collection<Long> values) {
		double mean = mean(values);
		double stdDev = 0;
		
		for(Long value : values){
			stdDev += (value - mean) * (value - mean);
		}
		stdDev /= values.size();
		stdDev = Math.sqrt(stdDev);
		assertTrue(stdDev < mean / 3);
	}

	@Test
	public void duplicatesTest(){
		for(int variables : new int[]{1, 2, 5}){
		for(int choices : new int[]{1, 2, 5}){
				duplicatesTest(variables, choices);
		}
		}
	}

	private void duplicatesTest(int variables, int choices) {
		Map<List<String>, Long> histogram = new HashMap<List<String>, Long>();
		List<List<String>> input = GeneratorTestUtils.prepareInput(variables, choices);
		RandomAlgorithm<String> algorithm = new RandomAlgorithm<String>(SAMPLE_SIZE, false);
		try {
			algorithm.initialize(input, EMPTY_CONSTRAINTS());
			List<String> next;
			while((next = algorithm.getNext()) != null){
				if(histogram.containsKey(next)){
					histogram.put(next, histogram.get(next) + 1);
				}
				else{
					histogram.put(next, 1l);
				}
			}
		} catch (GeneratorException e) {
			fail("Unexpected generator exception: " + e.getMessage());
		}
		
//		make sure that each value was chosen, given that the number of samples is higher than
//		number of possible results
		if(SAMPLE_SIZE > Math.pow(choices, variables)){
			assertEquals((int)Math.pow(choices, variables), histogram.size());
		}
		
//		check if no value was chosen more than once
		for(long freq : histogram.values()){
			assertEquals(1, freq);
		}
	}

	public double mean(Collection<Long> values){
		if(values.size() == 0) return 0;
		int sum = 0;
		for(long value: values){
			sum += value;
		}
		return (double)sum / (double)values.size(); 
	}
}
