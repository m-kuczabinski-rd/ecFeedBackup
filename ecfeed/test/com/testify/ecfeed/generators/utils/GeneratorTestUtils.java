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

package com.testify.ecfeed.generators.utils;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.testify.ecfeed.generators.algorithms.IAlgorithm;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.api.IGenerator;

public class GeneratorTestUtils{
	private static class Constraint implements IConstraint<String>{
		private Set<String> fRestrictedValues;

		public Constraint(Set<String> restrictedValues){
			fRestrictedValues = restrictedValues;
		}
		
		@Override
		public boolean evaluate(List<String> values) {
			for(String value : values){
				if(fRestrictedValues.contains(value)){
					return false;
				}
			}
			return true;
		}
		
		@Override public String toString(){
			return fRestrictedValues.toString();
		}
		
	}
	
	private static final String[] VARIABLE_NAMES = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", 
			"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "aa", "ab", 
			"ac", "ad", "ae", "af", "ag", "ah", "ai", "aj", "ak", "al", "am", "an", "ao", "ap", "aq"};
	
	public static List<List<String>> prepareInput(int variables, int partitions){
		List<List<String>> input = new ArrayList<List<String>>();
		for(int i = 0; i < variables; i++){
			String variableName = VARIABLE_NAMES[i];
			List<String> category = new ArrayList<String>();
			for(int j = 1; j <= partitions; j++){
				category.add(variableName + String.valueOf(j));
			}
			input.add(category);
		}
		return input;
	}
	
	public static List<Set<String>> referenceInput(List<List<String>> input){
		List<Set<String>> referenceInput = new ArrayList<Set<String>>();
		for(List<String> category : input){
			referenceInput.add(new LinkedHashSet<String>(category));
		}
		return referenceInput;
	}
	
	public static Set<List<String>> algorithmResult(IAlgorithm<String> initializedAlgorithm) {
		Set<List<String>> result = new HashSet<List<String>>();
		try {
			List<String> next;
			while((next = initializedAlgorithm.getNext()) != null){
				result.add(next);
			}
		} catch (GeneratorException e) {
			fail("Unexpected algorithm exception: " + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	public Set<List<String>> generatorResult(IGenerator<String> initializedGenerator) {
		Set<List<String>> result = new HashSet<List<String>>();
		try {
			List<String> next;
			while((next = initializedGenerator.next()) != null){
				result.add(next);
			}
		} catch (GeneratorException e) {
			fail("Unexpected algorithm exception: " + e.getMessage());
		}
		return result;
	}

	public static Collection<IConstraint<String>> generateRandomConstraints(List<List<String>> inputDomain) {
		Set<IConstraint<String>> result = new HashSet<IConstraint<String>>();
		for(int i = 2; i < inputDomain.size() - 1; i++){
			result.add(generateConstraint(inputDomain, i));
		}
		return result;
	}
	
	public static String randomString(int length){
		Random rand = new Random();
		String validCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890 ";
		String result = new String();
		for(int i = 0; i < length; i++){
			result += validCharacters.charAt(rand.nextInt(validCharacters.length())); 
		}
		return result;
	}

	private static IConstraint<String> generateConstraint(List<List<String>> inputDomain, int restricted) {
		Random random = new Random();
		Set<String> restrictedValues = new HashSet<String>();
		for(int i = 0; i < restricted; i++){
			List<String> constrainedCategory = inputDomain.get(random.nextInt(inputDomain.size()));
			restrictedValues.add(constrainedCategory.get(random.nextInt(constrainedCategory.size())));
		}
		return new Constraint(restrictedValues);
	}
}
