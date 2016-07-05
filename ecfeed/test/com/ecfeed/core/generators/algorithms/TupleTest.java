/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.core.generators.algorithms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.ecfeed.core.generators.algorithms.Tuples;
import com.google.common.collect.Sets;

public class TupleTest{
	private final List<String> fInput = new ArrayList<String>(Arrays.asList(new String[]{"a", "b", "c", "d", "e", "f"}));
	
	@Test
	public void nextTest(){
		for(int n = 1; n <= fInput.size(); n++){
			checkTuples(fInput, n);
		}
	}
	
	@Test
	public void getAllTest(){
		for(int n = 1; n <= fInput.size(); n++){
			Tuples<String> tuples = new Tuples<String>(fInput, n);
			assertEquals(referenceTuples(fInput, n), tuples.getAll());
		}
	}

	private void checkTuples(List<String> input, int n) {
		Tuples<String> tuples = new Tuples<String>(input, n);
		Set<List<String>> allTuples = new HashSet<List<String>>();
		
		while(tuples.hasNext()){
			List<String> next = tuples.next();
			assertFalse(allTuples.contains(next));
			allTuples.add(next);
		}
		
		
		assertEquals(referenceTuples(input, n), allTuples);
	}
	
	private Set<List<String>> referenceTuples(List<String> input, int n){
		Set<List<String>> referenceTuples = new HashSet<List<String>>(); 
		Set<Set<String>> powerSet = Sets.powerSet(new LinkedHashSet<String>(input));
		for(Set<String> set : powerSet){
			if(set.size() == n){
				referenceTuples.add(new ArrayList<String>(set));
			}
		}
		return referenceTuples;
	}
	
}
