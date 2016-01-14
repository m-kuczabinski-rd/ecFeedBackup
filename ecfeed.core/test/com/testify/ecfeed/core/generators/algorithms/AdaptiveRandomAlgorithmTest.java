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

package com.testify.ecfeed.core.generators.algorithms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.testify.ecfeed.core.generators.algorithms.AdaptiveRandomAlgorithm;
import com.testify.ecfeed.core.generators.api.GeneratorException;
import com.testify.ecfeed.core.generators.api.IConstraint;
import com.testify.ecfeed.core.generators.testutils.GeneratorTestUtils;

public class AdaptiveRandomAlgorithmTest {
	private final Collection<IConstraint<String>> EMPTY_CONSTRAINTS = new ArrayList<IConstraint<String>>();
	
	@Test
	public void distanceTest(){
		AdaptiveRandomAlgorithm<String> algorithm = new AdaptiveRandomAlgorithm<String>(0, 0, 0, false);
		
		List<String> v1 = new ArrayList<String>(); 
		List<String> v2 = new ArrayList<String>(); 

		for(int i = 0; i < 10; ++i){
			String s = GeneratorTestUtils.randomString(10);
			v1.add(s);
			v2.add(s);
		}

		int distance = algorithm.distance(v1, v2);
		assertEquals(0, distance);
		for(int i = 0; i < 10; ++i){
			v1.set(i, "some string that is unlikely to occure by random and is also longer than those generated");
			assertEquals(i+1, algorithm.distance(v1, v2));
		}
	}
	
	@Test
	public  void candidatesCountDuplicatesTest() {
		for(int parameters : new int[]{1, 2, 5}){
		for(int choices : new int[]{1, 2, 5}){
		for(int candidatesCount : new int[]{10, 100}){
		for(int steps : new int[]{0, 1, 10, 100}){
			try {
//				System.out.println("parameters: " + parameters + ", choices: " + choices + ", candidatesCount: " + candidatesCount + ", steps: " + steps);
				List<List<String>> input = GeneratorTestUtils.prepareInput(parameters, choices);
				AdaptiveRandomAlgorithm<String> algorithm = 
						new AdaptiveRandomAlgorithm<String>(0, candidatesCount, Integer.MAX_VALUE, true);
				algorithm.initialize(input, EMPTY_CONSTRAINTS);
				for(int i = 0; i < steps; i++){
					algorithm.getNext();
				}

				List<List<String>> candidates = algorithm.getCandidates();
				assertEquals(Math.min(candidatesCount, productSize(input)), candidates.size());
			} catch (GeneratorException e) {
				fail("Unexpected GeneratorException: " + e.getMessage());
			}
		}
		}
		}
		}
	}

	@Test
	public void candidatesCountNoDuplicatesTest() {
		for(int parameters : new int[]{1, 2, 5}){
		for(int choices : new int[]{1, 2, 5}){
		for(int candidatesCount : new int[]{1, 10, 100}){
		for(int steps : new int[]{0, 1, 10, 100}){
			try {
				List<List<String>> input = GeneratorTestUtils.prepareInput(parameters, choices);
				AdaptiveRandomAlgorithm<String> algorithm = 
						new AdaptiveRandomAlgorithm<String>(0, candidatesCount, Integer.MAX_VALUE, false);
				algorithm.initialize(input, EMPTY_CONSTRAINTS);
				for(int i = 0; i < steps; i++){
					algorithm.getNext();
				}
				
				List<List<String>> candidates = algorithm.getCandidates();
				assertTrue(candidates.size() <= candidatesCount);
			} catch (GeneratorException e) {
				fail("Unexpected GeneratorException: " + e.getMessage());
			}
		}
		}
		}
		}
	}

	@Test
	public void candidatesUniformityTest() {
		for(int parameters : new int[]{1, 2, 5}){
		for(int choices : new int[]{1, 2, 5}){
		for(int candidatesCount : new int[]{1, 10, 100}){
		for(int steps : new int[]{0, 1, 10, 100, 1000}){
			try {
				List<List<String>> input = GeneratorTestUtils.prepareInput(parameters, choices);
				AdaptiveRandomAlgorithm<String> algorithm = 
						new AdaptiveRandomAlgorithm<String>(0, candidatesCount, Integer.MAX_VALUE, true);
				algorithm.initialize(input, EMPTY_CONSTRAINTS);
				for(int i = 0; i < steps; i++){
					algorithm.getNext();
				}
				
				List<List<String>> candidates = algorithm.getCandidates();
				if(sampleUniform(candidates, input) == false){
					fail("Failed uniformity test for:\n"
							+ " parameters = " + parameters 
							+ "\nchoices = " + choices
							+ "\ncandidatesCount = " + candidatesCount
							+ "\nsteps = " + steps);
				}
			} catch (GeneratorException e) {
				fail("Unexpected GeneratorException: " + e.getMessage());
			}
		}
		}
		}
		}
	}

	@Test
	public void candidatesNoDuplicatesTest() {
		for(int parameters : new int[]{1, 2, 5}){
		for(int choices : new int[]{1, 2, 5}){
		for(int candidatesCount : new int[]{1, 10, 100, 1000}){
			try {
				List<List<String>> input = GeneratorTestUtils.prepareInput(parameters, choices);
				AdaptiveRandomAlgorithm<String> algorithm = 
						new AdaptiveRandomAlgorithm<String>(0, candidatesCount, Integer.MAX_VALUE, false);
				algorithm.initialize(input, EMPTY_CONSTRAINTS);

				List<List<String>> candidates = algorithm.getCandidates();
				Map<List<String>, Integer> histogram = createHistogram(candidates);
				for(int value : histogram.values()){
					assertEquals(1, value);
				}

				for(List<String> candidate : candidates){
					assertFalse(algorithm.getHistory().contains(candidate));
				}
			} catch (GeneratorException e) {
				fail("Unexpected GeneratorException: " + e.getMessage());
			}
		}
		}
		}
	}

	@Test
	public void candidatesDuplicatesTest() {
		for(int parameters : new int[]{1, 2, 5}){
		for(int choices : new int[]{1, 2, 5}){
		for(int candidatesCount : new int[]{1, 10, 100, 1000}){
			try {
				List<List<String>> input = GeneratorTestUtils.prepareInput(parameters, choices);
				AdaptiveRandomAlgorithm<String> algorithm = 
						new AdaptiveRandomAlgorithm<String>(0, candidatesCount, Integer.MAX_VALUE, true);
				algorithm.initialize(input, EMPTY_CONSTRAINTS);

				List<List<String>> candidates = algorithm.getCandidates();
				assertEquals(candidates.size(), Math.min(candidatesCount, productSize(input)));
			} catch (GeneratorException e) {
				fail("Unexpected GeneratorException: " + e.getMessage());
			}
		}
		}
		}
	}
	
	@Test
	public void optimalCandidateTest(){
		for(int historySize : new int[]{0, 1, 5, Integer.MAX_VALUE}){
		for(int candidatesSize : new int[]{1, 10, 100}){
		for(int length : new int[]{10000}){
		for(boolean duplicates : new boolean[]{true, false}){
		for(int parameters : new int[]{1, 2, 5}){
		for(int choices : new int[]{1, 2, 5}){
			try{
				AdaptiveRandomAlgorithm<String> algorithm = 
						new AdaptiveRandomAlgorithm<String>(historySize, candidatesSize, length, duplicates);
				algorithm.initialize(GeneratorTestUtils.prepareInput(parameters, choices), EMPTY_CONSTRAINTS);

				List<List<String>> candidates = algorithm.getCandidates(); 
				List<List<String>> history = algorithm.getCandidates();
				List<String> optimalCandidate = algorithm.getOptimalCandidate(candidates, history);

				int optimalCandidateDistance = Integer.MAX_VALUE;
				for(List<String> event : history){
					optimalCandidateDistance = Math.min(algorithm.distance(event, optimalCandidate), optimalCandidateDistance);
				}
				for(List<String> candidate : candidates){
					int candidateDistance = Integer.MAX_VALUE;
					for(List<String> event : history){
						int eventDistance = algorithm.distance(candidate, event);
						candidateDistance = Math.min(candidateDistance, eventDistance);
					}
					assertTrue(candidateDistance <= optimalCandidateDistance);
				}
			}
			catch(GeneratorException e){
				fail("Unexpected GeneratorException: " + e.getMessage());
			}
		}
		}
		}
		}
		}
		}
		
	}

	protected boolean sampleUniform(List<List<String>> candidates, List<List<String>> input) {
		Map<List<String>, Integer> histogram = createHistogram(candidates);
		double expVal = (double)candidates.size() / (double) productSize(input);
		double stdDev = stdDev(histogram, expVal, input);
		for(List<String> candidate : candidates){
			if(histogram.get(candidate) - expVal > 3 * stdDev){
				return false;
			}
		}
		return true;
	}

	private double stdDev(Map<List<String>, Integer> histogram, double expVal, List<List<String>> input) {
		if(histogram.size() == 0) return 0.0;
		double stdDev = 0;
		for(int value : histogram.values()){
			stdDev += (value * value) / productSize(input);
		}
		stdDev = Math.sqrt(stdDev - expVal);
		return stdDev;
	}

	private Map<List<String>, Integer> createHistogram(List<List<String>> candidates) {
		Map<List<String>, Integer> histogram = new HashMap<List<String>, Integer>();
		for(List<String> candidate : candidates){
			if(histogram.containsKey(candidate)){
				histogram.put(candidate, histogram.get(candidate) + 1);
			}
			else{
				histogram.put(candidate, 1);
			}
		}
		return histogram;
	}

	private int productSize(List<List<String>> input) {
		int size = 1;
		for(List<String> parameter : input){
			size *= parameter.size();
		}
		return size;
	}
}
